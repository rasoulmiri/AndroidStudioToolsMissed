package androidstudio.tools.missed.manager.adb

import androidstudio.tools.missed.features.customcommand.model.CustomCommand
import androidstudio.tools.missed.manager.adb.command.AdbCommand
import androidstudio.tools.missed.manager.device.model.Device

interface AdbManager {

    suspend fun initialAdb(): Result<Boolean>
    suspend fun getDevices(): Result<List<Device>>
    suspend fun executeADBCommand(device: Device?, command: AdbCommand): Result<String>
    suspend fun executeADBShellCommand(device: Device?, command: AdbCommand): Result<String>
    suspend fun executeCustomCommand(device: Device?, packageId: String?, command: CustomCommand): Result<String>
}
