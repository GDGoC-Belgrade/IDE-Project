package rs.gdgoc.core.lsp

import java.io.InputStream
import java.io.OutputStream

class Server(private val config: LSPConfig) {

    private var process: Process? = null
    private var isRunning = false

    val inputStream: InputStream?
        get() = process?.inputStream
    val outputStream: OutputStream?
        get() = process?.outputStream
    val errStream: InputStream?
        get() = process?.errorStream

    fun start() {
        if (isRunning) {
            throw IllegalStateException("LSP Server is already running")
        }

        val processBuilder = ProcessBuilder(listOf(config.command) + config.args)

        config.workingDirectory?.let { processBuilder.directory(it) }

        if (config.env.isNotEmpty()) {
            processBuilder.environment().putAll(config.env)
        }

        processBuilder.redirectErrorStream(false)

        process = processBuilder.start()
        isRunning = true
    }

    fun stop() {
        process?.destroy()
        isRunning = false
    }

    fun forceStop() {
        process?.destroyForcibly()
        isRunning = false
    }

    fun isAlive(): Boolean = process?.isAlive ?: false

    fun waitForExit(): Int = process?.waitFor() ?: -1
}