package androidstudio.tools.missed.manager.device

import androidstudio.tools.missed.features.customcommand.model.CustomCommand
import androidstudio.tools.missed.manager.adb.AdbManager
import androidstudio.tools.missed.manager.adb.command.AdbCommand
import androidstudio.tools.missed.manager.adb.command.ApplicationAdbCommands
import androidstudio.tools.missed.manager.adb.command.FileAdbCommands
import androidstudio.tools.missed.manager.device.model.Device
import androidstudio.tools.missed.manager.resource.ResourceManager
import androidstudio.tools.missed.utils.DELAY_LONG
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DeviceManagerImpl(
    private val resourceManager: ResourceManager,
    private val adbManager: AdbManager
) : DeviceManager {

    private val _devicesStateFlow = MutableStateFlow<List<Device>>(arrayListOf())
    override val devicesStateFlow: StateFlow<List<Device>> = _devicesStateFlow.asStateFlow()

    private val _selectedDeviceStateFlow = MutableStateFlow<Device?>(null)
    override val selectedDeviceStateFlow: StateFlow<Device?> = _selectedDeviceStateFlow.asStateFlow()

    private val _packageIdSelectedStateFlow = MutableStateFlow<String?>(null)
    override val packageIdSelectedStateFlow: StateFlow<String?> = _packageIdSelectedStateFlow.asStateFlow()

    override suspend fun configure(coroutineScope: CoroutineScope): Result<Boolean> {
        // Initial ADB
        val resultInitialAdb = adbManager.initialAdb()
        if (resultInitialAdb.isFailure) {
            return Result.failure(
                resultInitialAdb.exceptionOrNull() ?: Throwable(resourceManager.string("adbIsNotInitialize"))
            )
        }

        // Get devices from ADB
        val resultGetDevice = getDevicesFromAdb()
        if (resultGetDevice.isFailure) {
            return Result.failure(
                resultGetDevice.exceptionOrNull() ?: Throwable(resourceManager.string("getDevicesFromAdbError"))
            )
        }

        // Add listener for changing devices like connect or disconnect
        coroutineScope.launch {
            while (true) {
                delay(DELAY_LONG)
                val newDevices = adbManager.getDevices().getOrNull()
                val idsNew = newDevices?.map { it.id }?.toSet()
                val idsOld = _devicesStateFlow.value.map { it.id }.toSet()
                if (idsNew != idsOld) {
                    getDevicesFromAdb()
                }
            }
        }
        return Result.success(true)
    }

    private suspend fun getDevicesFromAdb(): Result<Boolean> {
        val resultGetDevices = adbManager.getDevices()

        return if (resultGetDevices.isSuccess) {
            val devices = resultGetDevices.getOrNull()?.sortedBy { it.name }
            clearData()
            _devicesStateFlow.emit(devices ?: emptyList())
            _selectedDeviceStateFlow.emit(_devicesStateFlow.value.getOrNull(0))
            Result.success(true)
        } else {
            Result.failure(
                resultGetDevices.exceptionOrNull() ?: Throwable(resourceManager.string("getDevicesFromAdbError"))
            )
        }
    }

    override suspend fun setSelectedDevice(device: Device?) {
        _selectedDeviceStateFlow.emit(device)
    }

    override suspend fun setSelectedPackageId(packageId: String?) {
        _packageIdSelectedStateFlow.emit(packageId)
    }

    private suspend fun clearData() {
        _devicesStateFlow.emit(arrayListOf())
        _selectedDeviceStateFlow.emit(null)
        _packageIdSelectedStateFlow.emit(null)
    }

    override fun getDeviceSelectedName(): String? = _selectedDeviceStateFlow.value?.name

    override suspend fun executeShellCommand(adbCommand: AdbCommand): Result<String> {
        if (adbCommand.isNeedDevice && (_selectedDeviceStateFlow.value == null || _devicesStateFlow.value.isEmpty())) {
            val errorMessage = resourceManager.string("selectADevice")
            println(errorMessage)
            return Result.failure(Throwable(errorMessage))
        }

        if (adbCommand.isNeedPackageId && _packageIdSelectedStateFlow.value == null) {
            val errorMessage = resourceManager.string("selectAnApplication")
            println(errorMessage)
            return Result.failure(Throwable(errorMessage))
        }

        return adbManager.executeADBShellCommand(
            device = _selectedDeviceStateFlow.value,
            command = adbCommand
        )
    }

    override suspend fun executeCustomCommand(customCommand: CustomCommand): Result<String> {
        return adbManager.executeCustomCommand(
            device = _selectedDeviceStateFlow.value,
            packageId = _packageIdSelectedStateFlow.value,
            command = customCommand
        )
    }

    override suspend fun installApk(packageFilePath: String): Result<String> {
        return adbManager.executeADBCommand(
            device = _selectedDeviceStateFlow.value,
            command = ApplicationAdbCommands.Install(packageFilePath)
        )
    }

    override suspend fun pullFile(remoteFilepath: String, localFilePath: String): Result<String> {
        return adbManager.executeADBCommand(
            device = _selectedDeviceStateFlow.value,
            command = FileAdbCommands.PullFile(remoteFilepath, localFilePath)
        )
    }
}
