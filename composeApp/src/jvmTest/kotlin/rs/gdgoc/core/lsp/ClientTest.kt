package rs.gdgoc.core.lsp

import org.junit.Test
import java.io.File
import kotlin.test.assertNotNull
import kotlin.test.fail

class ClientTest {

    @Test
    fun testConnect() {
        println("=== Starting Python LSP Test ===")

        // Config may be different based on your installation of pip
        val config = LSPConfig(
            command = "py",
            args = listOf("-m", "pylsp"),
            workingDirectory = File(System.getProperty("user.home")),
            env = emptyMap()
        )

        val client = Client(config)

        try {
            // Start client
            println("Starting client...")
            client.start()
            println("✓ Client started")

            // Give server time to start
            Thread.sleep(1500)

            // Prepare initialize parameters
            val initParams = mapOf(
                "processId" to ProcessHandle.current().pid(),
                "rootUri" to "file://${System.getProperty("user.home")}",
                "capabilities" to mapOf(
                    "textDocument" to mapOf(
                        "synchronization" to mapOf(
                            "didSave" to true
                        )
                    )
                )
            )

            println("Initializing client...")
            val capabilitiesResponse = client.initialize(initParams)
            assertNotNull(capabilitiesResponse)
            println("✓ Client initialized")

            // Wait to see any server logs
            Thread.sleep(2000)

        } catch (e: Exception) {
            fail("Internal error occured: $e")
        } finally {
            println("Stopping client...")
            client.stop()
            println("✓ Client stopped")
            println("=== Test Complete ===")
        }
    }
}