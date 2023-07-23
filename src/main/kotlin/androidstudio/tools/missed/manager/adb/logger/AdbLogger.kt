package androidstudio.tools.missed.manager.adb.logger

import androidstudio.tools.missed.manager.adb.command.AdbCommand
import androidstudio.tools.missed.manager.device.model.DeviceInformation
import java.io.IOException

interface AdbLogger {

    fun printResult(device: DeviceInformation?, adbCommand: AdbCommand, message: String, exception: IOException?)
}
