package rs.gdgoc.core.lsp

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class LSPRequest(
    val jsonrpc: String = "2.0",
    val id: Int,
    val method: String,
    val params: JsonElement? = null
)

@Serializable
data class LSPResponse(
    val jsonrpc: String = "2.0",
    val id: Int,
    val result: JsonElement? = null,
    val error: LSPError? = null
)

@Serializable
data class LSPNotification(
    val jsonrpc: String = "2.0",
    val method: String,
    val params: JsonElement? = null
)

@Serializable
data class LSPError(
    val code: Int,
    val message: String,
    val data: JsonElement? = null
)