package androidstudio.tools.missed.manager.adb.logger

import androidstudio.tools.missed.manager.adb.command.NetworkAdbCommands
import androidstudio.tools.missed.manager.device.model.Device
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.PrintStream

class AdbLoggerImplTest {

    @Test
    fun `test print result`() {
        val adbLogger = AdbLoggerImpl()

        // Create sample input data
        val device = Device("Device1", "id1")
        val adbCommand = NetworkAdbCommands.GetMobileDataState()
        val message = "Success"
        val exception: IOException? = null

        // Redirect standard output to capture the printed result
        val outputStream = ByteArrayOutputStream()
        System.setOut(PrintStream(outputStream))

        // Call the printResult method
        adbLogger.printResult(device, adbCommand, message, exception)

        // Restore standard output
        System.setOut(System.out)

        // Get the printed result
        val printedResult = outputStream.toString().trim()

        // Define the expected output
        val expectedOutput = """
            ExecuteShellCommand ____________________________________________________________________________
            Device  = Device1
            CommandName = GetMobileDataState
                settings get global mobile_data 
            Result  = Success
            ____________________________________________________________________________________________
        """.trimIndent()

        // Assert that the printed result matches the expected output
        assertEquals(expectedOutput, printedResult)
    }
}
