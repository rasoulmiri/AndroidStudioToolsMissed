package androidstudio.tools.missed.manager.device

import androidstudio.tools.missed.features.customcommand.model.CustomCommand
import androidstudio.tools.missed.manager.adb.command.AdbCommand
import androidstudio.tools.missed.manager.device.model.Device
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

interface DeviceManager {

    val devicesStateFlow: StateFlow<List<Device>>
    val selectedDeviceStateFlow: StateFlow<Device?>
    val packageIdSelectedStateFlow: StateFlow<String?>

    suspend fun configure(coroutineScope: CoroutineScope): Result<Boolean>
    suspend fun executeShellCommand(adbCommand: AdbCommand): Result<String>
    suspend fun executeCustomCommand(customCommand: CustomCommand): Result<String>
    suspend fun installApk(packageFilePath: String): Result<String>
    suspend fun pullFile(remoteFilepath: String, localFilePath: String): Result<String>
    suspend fun setSelectedDevice(device: Device?)
    suspend fun setSelectedPackageId(packageId: String?)
    fun getDeviceSelectedName(): String?
}
