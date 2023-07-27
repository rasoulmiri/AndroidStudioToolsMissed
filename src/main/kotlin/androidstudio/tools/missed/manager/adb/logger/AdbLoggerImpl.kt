package androidstudio.tools.missed.manager.adb.logger

import androidstudio.tools.missed.manager.adb.command.AdbCommand
import androidstudio.tools.missed.manager.device.model.DeviceInformation
import java.io.IOException

class AdbLoggerImpl : AdbLogger {

    override fun printResult(
        device: DeviceInformation?,
        adbCommand: AdbCommand,
        message: String,
        exception: IOException?
    ) {
        println(
            "\nExecuteShellCommand ____________________________________________________________________________" +
                "\nDevice  = ${
                if (device?.name == "Unknow") {
                    device.iDevice.serialNumber
                } else {
                    device?.name?.replace("\n", "")
                }
                }\nCommandName = ${adbCommand::class.java.simpleName}\n    ${adbCommand.command} \nResult  = ${
                message.ifEmpty {
                    "Success"
                }
                }${
                exception?.printStackTrace() ?: ""
                }\n____________________________________________________________________________________________"
        )
    }
}
