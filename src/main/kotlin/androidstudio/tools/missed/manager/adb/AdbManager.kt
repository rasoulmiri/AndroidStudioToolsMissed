package androidstudio.tools.missed.manager.adb

import androidstudio.tools.missed.manager.adb.command.AdbCommand
import androidstudio.tools.missed.manager.device.model.Device
import com.android.ddmlib.IDevice
import kotlinx.coroutines.flow.Flow

interface AdbManager {

    suspend fun initialAdb(): Result<Boolean>
    suspend fun getDevices(): Result<List<Device>>
    fun deviceChangeListener(): Flow<IDevice?>
    suspend fun executeADBCommand(device: Device?, command: AdbCommand): Result<String>
    suspend fun executeADBShellCommand(device: Device?, command: AdbCommand): Result<String>
}
