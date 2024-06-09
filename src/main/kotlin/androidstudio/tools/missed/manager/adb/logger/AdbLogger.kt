package androidstudio.tools.missed.manager.adb.logger

import androidstudio.tools.missed.manager.adb.command.AdbCommand
import androidstudio.tools.missed.manager.device.model.Device
import java.io.IOException

interface AdbLogger {

    fun println(message: String)
    fun printResult(device: Device?, adbCommand: AdbCommand, message: String, exception: IOException?)
}
