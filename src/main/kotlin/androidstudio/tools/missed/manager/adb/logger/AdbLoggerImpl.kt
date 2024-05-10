package androidstudio.tools.missed.manager.adb.logger

import androidstudio.tools.missed.manager.adb.command.AdbCommand
import androidstudio.tools.missed.manager.device.model.Device
import java.io.IOException

class AdbLoggerImpl : AdbLogger {

    override fun println(message: String) {
        System.out.println(message)
    }

    override fun printResult(device: Device?, adbCommand: AdbCommand, message: String, exception: IOException?) {
        println(
            "\nExecuteShellCommand ____________________________________________________________________________" +
                "\nDevice  = ${
                if (device?.name == "Unknow") {
                    device.id
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
