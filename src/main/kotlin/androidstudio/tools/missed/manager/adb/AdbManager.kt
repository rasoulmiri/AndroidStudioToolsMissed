package androidstudio.tools.missed.manager.adb

import androidstudio.tools.missed.manager.adb.command.AdbCommand
import androidstudio.tools.missed.manager.device.model.DeviceInformation
import com.android.ddmlib.IDevice
import kotlinx.coroutines.flow.Flow

interface AdbManager {

    suspend fun initialAdb(): Result<Boolean>
    suspend fun getDevices(): Result<List<DeviceInformation>>
    fun deviceChangeListener(): Flow<IDevice?>
    suspend fun executeADBShellCommand(device: DeviceInformation?, command: AdbCommand): Result<String>
    suspend fun installApk(device: DeviceInformation?, packageFilePath: String): Result<String>

    suspend fun pullFile(device: DeviceInformation?, remoteFilepath: String, localFilePath: String): Result<String>

    suspend fun pushFile(device: DeviceInformation?, localFilePath: String, remoteFilepath: String): Result<String>
}
