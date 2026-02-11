package rs.gdgoc.core.lsp

import java.io.File

data class LSPConfig(
    val command: String,
    val args: List<String> = emptyList(),
    val workingDirectory: File? = null,
    val env: Map<String, String> = emptyMap()
)