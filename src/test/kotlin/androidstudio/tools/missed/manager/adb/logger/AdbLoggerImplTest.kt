package androidstudio.tools.missed.manager.adb.logger

import androidstudio.tools.missed.manager.adb.command.InternetAdbConnectionCommands
import androidstudio.tools.missed.manager.device.model.DeviceInformation
import com.android.ddmlib.IDevice
import io.mockk.mockk
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
        val mockDevice = mockk<IDevice>(relaxed = true)
        val device = DeviceInformation("Device 1", "Brand", "Model", mockDevice)
        val adbCommand = InternetAdbConnectionCommands.GetMobileDataState()
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
            Device  = Device 1
            CommandName = GetMobileDataState
                settings get global mobile_data 
            Result  = Success
            ____________________________________________________________________________________________
        """.trimIndent()

        // Assert that the printed result matches the expected output
        assertEquals(expectedOutput, printedResult)
    }
}
