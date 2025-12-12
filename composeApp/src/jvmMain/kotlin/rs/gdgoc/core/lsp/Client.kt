package rs.gdgoc.core.lsp

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.cancel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

class Client(config: LSPConfig) {

    private companion object {
        const val RESPONSE_TIMEOUT = 10000L
        const val SHUTDOWN_TIMEOUT_1 = 2000L
        const val SHUTDOWN_TIMEOUT_2 = 5000L
    }

    private val server: Server = Server(config)
    private val requestIdCounter = AtomicInteger(0)
    private val pendingRequests = ConcurrentHashMap<Int, CompletableDeferred<LSPResponse>>()
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val writeMutex = Mutex()

    private var writer: BufferedWriter? = null
    private var reader: BufferedReader? = null

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    fun start() {
        server.start()

        writer = BufferedWriter(OutputStreamWriter(server.outputStream ?: throw IllegalStateException("The server output stream is null")))
        reader = BufferedReader(InputStreamReader(server.inputStream ?: throw IllegalStateException("The server input stream is null")))

        // Starts listener coroutine for communication (requests, responses, notifications)
        scope.launch {
            messageListener()
        }
        // Starts listener coroutine for errors
        scope.launch {
            errorListener()
        }
    }

    fun initialize(params: Map<String, Any>? = null): LSPResponse? = runBlocking {
        val response = sendRequest("initialize", params)
        if (response == null || response.error != null) return@runBlocking null
        sendNotification("initialized")
        return@runBlocking response
    }

    fun stop() {
        // Try graceful shutdown via LSP
        runBlocking {
            sendRequest("shutdown", null)
            sendNotification("exit", null)
        }

        writer?.close()
        reader?.close()
        scope.cancel()

        if (!server.waitForExit(SHUTDOWN_TIMEOUT_1)) {
            // Graceful didn't work, stop the process manually
            server.stop()

            if (!server.waitForExit(SHUTDOWN_TIMEOUT_2)) {
                // Last resort, kill it forcefully
                server.forceStop()
            }
        }

        pendingRequests.clear()
    }

    // Returns LSPResponse or null if the response doesn't come in RESPONSE_TIMEOUT milliseconds
    suspend fun sendRequest(method: String, params: Map<String, Any>? = null): LSPResponse? {
        val id = requestIdCounter.getAndIncrement();

        // Instantiate a future object
        val deferred = CompletableDeferred<LSPResponse>()
        pendingRequests[id] = deferred
        sendMessage(buildJsonRequest(id, method, params))

        // Wait for the future object (timeout at RESPONSE_TIMEOUT milliseconds)
        return withTimeout(RESPONSE_TIMEOUT) {
            try {
                deferred.await()
            } catch (e: TimeoutCancellationException) {
                pendingRequests.remove(id)
                null
            }
        }
    }

    suspend fun sendNotification(method: String, params: Map<String, Any>? = null) {
        sendMessage(buildJsonNotification(method, params))
    }

    private suspend fun sendMessage(jsonMessage: String) = withContext(Dispatchers.IO) {
        val contentBytes = jsonMessage.toByteArray(Charsets.UTF_8)
        val header = "Content-Length: ${contentBytes.size}\r\n\r\n"

        // Write header and content and flush the writer atomically
        writeMutex.withLock {
            writer?.let {
                it.write(header)
                it.write(jsonMessage)
                it.flush()
            }
        }
    }

    private suspend fun messageListener() = withContext(Dispatchers.IO) {
        while (isActive) {
            val message = readMessage() ?: break
            processMsg(message)
        }
    }

    private suspend fun errorListener() = withContext(Dispatchers.IO) {
        val errorReader = BufferedReader(InputStreamReader(server.errorStream ?: throw IllegalStateException("The server error stream is null")))
        while (isActive) {
            val error = errorReader.readLine() ?: break
            println("Error: $error")
            // TODO: handle different errors
        }
    }

    private suspend fun readMessage(): String? = withContext(Dispatchers.IO) {
        var contentLength = -1

        // Read headers
        while (true) {
            val line = reader?.readLine() ?: return@withContext null
            if (line.isEmpty()) break

            if (line.startsWith("Content-Length:")) {
                contentLength = line.substringAfter(":").trim().toInt()
            }
        }

        if (contentLength < 0) return@withContext null

        // Read content
        val buffer = CharArray(contentLength)
        var totalRead = 0
        while (totalRead < contentLength) {
            val read = reader?.read(buffer, totalRead, contentLength - totalRead) ?: -1
            if (read == -1) return@withContext null
            totalRead += read
        }

        return@withContext String(buffer)
    }

    private fun processMsg(jsonMsg: String) {
        val jsonObject = json.parseToJsonElement(jsonMsg).jsonObject
        val hasId = jsonObject.containsKey("id")
        val hasMethod = jsonObject.containsKey("method")

        when {
            hasId && hasMethod -> handleServerRequest(buildRequestFromJson(jsonMsg))
            !hasId && hasMethod -> handleServerNotification(buildNotificationFromJson(jsonMsg))
            hasId && !hasMethod -> handleServerResponse(buildResponseFromJson(jsonMsg))
            else -> {
                // Malformed message
                println("Malformed message: $jsonMsg")
            }
        }
    }

    private fun handleServerResponse(response: LSPResponse) {
        val deferred = pendingRequests.remove(response.id)
        // Set the future object, thus unblocking the coroutine waiting on it
        deferred?.complete(response)
    }

    private fun handleServerRequest(request: LSPRequest) {
        // TODO: handle different requests
        println("Server request: $request")
        // Return the response
        scope.launch {
            sendMessage(buildJsonResponse(request.id)) // TODO: set result or error
        }
    }

    private fun handleServerNotification(notification: LSPNotification) {
        // TODO: handle different notifications
        println("Server notification: $notification")
    }

    private fun buildJsonRequest(id: Int, method: String, params: Map<String, Any>? = null): String {
        val jsonParams = params?.let { json.encodeToJsonElement(anyToJson(it)) }
        return json.encodeToString(LSPRequest(id=id, method=method, params=jsonParams))
    }

    private fun buildJsonNotification(method: String, params: Map<String, Any>? = null): String {
        val jsonParams = params?.let { json.encodeToJsonElement(anyToJson(it)) }
        return json.encodeToString(LSPNotification(method=method, params=jsonParams))
    }

    private fun buildJsonResponse(id: Int, result:Map<String, Any>? = null): String {
        val jsonResult = result?.let { json.encodeToJsonElement(anyToJson(it)) }
        return json.encodeToString(LSPResponse(id=id, result=jsonResult))
    }

    private fun buildRequestFromJson(jsonRequest: String): LSPRequest {
        return json.decodeFromString<LSPRequest>(jsonRequest)
    }

    private fun buildNotificationFromJson(jsonNotification: String): LSPNotification {
        return json.decodeFromString<LSPNotification>(jsonNotification)
    }

    private fun buildResponseFromJson(jsonResponse: String): LSPResponse {
        return json.decodeFromString<LSPResponse>(jsonResponse)
    }

    // Map<String, Any> can't be converted to JSON, so we must transform it recursively into JsonElement
    private fun anyToJson(value: Any?): JsonElement =
        when (value) {
            null -> JsonNull
            is String -> JsonPrimitive(value)
            is Number -> JsonPrimitive(value)
            is Boolean -> JsonPrimitive(value)
            is Map<*, *> -> buildJsonObject {
                value.forEach { (k, v) ->
                    if (k is String) put(k, anyToJson(v))
                }
            }

            is List<*> -> buildJsonArray {
                value.forEach { add(anyToJson(it)) }
            }

            else -> throw IllegalArgumentException("Unsupported type: ${value::class}")
        }
}