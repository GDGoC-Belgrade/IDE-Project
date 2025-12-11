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
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

class Client(config: LSPConfig) {

    private val server: Server = Server(config)
    private val requestIdCounter = AtomicInteger(0)
    private val pendingRequests = ConcurrentHashMap<Int, CompletableDeferred<LSPResponse>>()
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

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

    fun stop() {
        // Try graceful shutdown via LSP
        runBlocking {
            try {
                withTimeout(2000) {
                    sendRequest("shutdown", null)
                    sendNotification("exit", null)
                }
            } catch (e: TimeoutCancellationException) {}
        }

        writer?.close()
        reader?.close()
        scope.cancel()

        if (!server.waitForExit(2000)) {
            // If graceful didn't work, stop the process manually
            server.stop()

            if (!server.waitForExit(5000)) {
                // Last resort, kill it forcefully
                server.forceStop()
            }
        }

        pendingRequests.clear()
    }

    suspend fun sendRequest(method: String, params: Map<String, Any>? = null): LSPResponse {
        val id = requestIdCounter.getAndIncrement();

        // Instantiate a future object
        val deferred = CompletableDeferred<LSPResponse>()
        pendingRequests[id] = deferred
        sendMessage(buildJsonRequest(id, method, params))

        // Wait for the future object (timeout at 10 seconds)
        return withTimeout(10000) {
            try {
                deferred.await()
            } catch (e: TimeoutCancellationException) {
                pendingRequests.remove(id)
                // TODO: handle timeout error
                throw e
            }
        }
    }

    fun sendNotification(method: String, params: Map<String, Any>? = null) {
        scope.launch {
            sendMessage(buildJsonNotification(method, params))
        }
    }

    private suspend fun sendMessage(jsonMessage: String) = withContext(Dispatchers.IO) {
        val contentBytes = jsonMessage.toByteArray(Charsets.UTF_8)
        val header = "Content-Length: ${contentBytes.size}\r\n\r\n"

        writer?.write(header)
        writer?.write(jsonMessage)
        writer?.flush()
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
            // TODO: handle different errors
        }
    }

    private fun readMessage(): String? {
        var contentLength = -1

        // Read headers
        while (true) {
            val line = reader?.readLine() ?: return null
            if (line.isEmpty()) break

            if (line.startsWith("Content-Length:")) {
                contentLength = line.substringAfter(":").trim().toInt()
            }
        }

        if (contentLength < 0) return null

        // Read content
        val buffer = CharArray(contentLength)
        var totalRead = 0
        while (totalRead < contentLength) {
            val read = reader?.read(buffer, totalRead, contentLength - totalRead) ?: -1
            if (read == -1) return null
            totalRead += read
        }

        return String(buffer)
    }

    private fun processMsg(jsonMsg: String) {
        try {
            val jsonObject = json.parseToJsonElement(jsonMsg).jsonObject
            val hasId = jsonObject.containsKey("id")
            val hasMethod = jsonObject.containsKey("method")

            when {
                hasId && hasMethod -> handleServerRequest(buildRequestFromJson(jsonMsg))
                !hasId && hasMethod -> handleServerNotification(buildNotificationFromJson(jsonMsg))
                hasId && !hasMethod -> handleServerResponse(buildResponseFromJson(jsonMsg))
                else -> throw IllegalArgumentException("Invalid message type")
            }
        } catch (e: Exception) {
            // TODO: handle malformed messages
        }
    }

    private fun handleServerResponse(response: LSPResponse) {
        val deferred = pendingRequests.remove(response.id)
        deferred?.complete(response)
    }

    private fun handleServerRequest(request: LSPRequest) {
        // TODO: handle different requests
        scope.launch {
            sendMessage(buildJsonResponse(request.id, null, null))
        }
    }

    private fun handleServerNotification(notification: LSPNotification) {
        // TODO: handle different notifications
    }

    private fun buildJsonRequest(id: Int, method: String, params: Map<String, Any>? = null): String {
        val jsonParams = params?.let { json.encodeToJsonElement(it) }
        return json.encodeToString(LSPRequest(id=id, method=method, params=jsonParams))
    }

    private fun buildJsonNotification(method: String, params: Map<String, Any>? = null): String {
        val jsonParams = params?.let { json.encodeToJsonElement(it) }
        return json.encodeToString(LSPNotification(method=method, params=jsonParams))
    }

    private fun buildJsonResponse(id: Int, result:Map<String, Any>? = null, error: LSPError? = null): String {
        val jsonResult = result?.let { json.encodeToJsonElement(it) }
        return json.encodeToString(LSPResponse(id=id, result=jsonResult, error=error))
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
}