package rs.gdgoc.core.lsp

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.util.concurrent.atomic.AtomicInteger

class Client(config: LSPConfig) {

    private val server: Server = Server(config)
    private val requestIdCounter = AtomicInteger(0)
    private val pendingRequests = mutableMapOf<Int, CompletableDeferred<LSPResponse>>()
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val messageChannel = Channel<String>(Channel.UNLIMITED)

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

        scope.launch {
            listenForMessages()
        }

        scope.launch {
            monitorErrors()
        }
    }

    fun stop() {
        scope.cancel()
        writer?.close()
        reader?.close()
        server.stop()
        if (server.isAlive()) {
            server.forceStop()
            if (server.isAlive()) {
                throw IllegalStateException("The server won't shut down")
            }
        }
        pendingRequests.clear()
    }

    suspend fun sendRequest(method: String, params: Map<String, Any>? = null): LSPResponse {
        val id = requestIdCounter.getAndIncrement();
        val deferred = CompletableDeferred<LSPResponse>()
        pendingRequests[id] = deferred
        sendMessage(buildJsonRequest(id, method, params))
        return deferred.await()
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

    private suspend fun listenForMessages() = withContext(Dispatchers.IO) {
        while (isActive) {
            val message = readMessage() ?: break
            processMsg(message)
        }
    }

    private fun readMessage(): String? {
        var contentLength = -1

        // Read headers
        while (true) {
            val line = reader?.readLine() ?: return null
            if (line.isEmpty()) break // Empty line separates headers from content

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
        val jsonObject = json.parseToJsonElement(jsonMsg).jsonObject
        val hasId = jsonObject.containsKey("id")
        val hasMethod = jsonObject.containsKey("method")

        when {
            hasId and hasMethod -> handleServerRequest(buildRequestFromJson(jsonMsg))
            !hasId and hasMethod -> handleServerNotification(buildNotificationFromJson(jsonMsg))
            hasId and !hasMethod -> handleServerResponse(buildResponseFromJson(jsonMsg))
            else -> throw IllegalArgumentException("Invalid message type")
        }
    }

    private fun handleServerResponse(response: LSPResponse) {

    }

    private fun handleServerRequest(request: LSPRequest) {

    }

    private fun handleServerNotification(notification: LSPNotification) {

    }

    fun monitorErrors() {}

    fun buildJsonRequest(id: Int, method: String, params: Map<String, Any>? = null): String {
        val jsonParams = params?.let { json.encodeToJsonElement(it) }
        return json.encodeToString(LSPRequest(id=id, method=method, params=jsonParams))
    }

    fun buildJsonNotification(method: String, params: Map<String, Any>? = null): String {
        val jsonParams = params?.let { json.encodeToJsonElement(it) }
        return json.encodeToString(LSPNotification(method=method, params=jsonParams))
    }

    fun buildJsonResponse(id: Int, result:Map<String, Any>? = null, error: LSPError? = null): String {
        val jsonResult = result?.let { json.encodeToJsonElement(it) }
        return json.encodeToString(LSPResponse(id=id, result=jsonResult, error=error))
    }

    fun buildRequestFromJson(jsonRequest: String): LSPRequest {
        return json.decodeFromString<LSPRequest>(jsonRequest)
    }

    fun buildNotificationFromJson(jsonNotification: String): LSPNotification {
        return json.decodeFromString<LSPNotification>(jsonNotification)
    }

    fun buildResponseFromJson(jsonResponse: String): LSPResponse {
        return json.decodeFromString<LSPResponse>(jsonResponse)
    }
}