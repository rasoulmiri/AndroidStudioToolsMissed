package androidstudio.tools.missed.manager.device

import androidstudio.tools.missed.manager.adb.AdbManager
import androidstudio.tools.missed.manager.adb.command.AdbCommand
import androidstudio.tools.missed.manager.device.model.DeviceInformation
import androidstudio.tools.missed.manager.resource.ResourceManager
import androidstudio.tools.missed.utils.DELAY_LONG
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DeviceManagerImpl(
    private val resourceManager: ResourceManager,
    private val adbManager: AdbManager
) : DeviceManager {

    private val _devicesStateFlow = MutableStateFlow<List<DeviceInformation>>(arrayListOf())
    override val devicesStateFlow: StateFlow<List<DeviceInformation>> = _devicesStateFlow.asStateFlow()

    private val _selectedDeviceStateFlow = MutableStateFlow<DeviceInformation?>(null)
    override val selectedDeviceStateFlow: StateFlow<DeviceInformation?> = _selectedDeviceStateFlow.asStateFlow()

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
        val resultGetDeviceFromAdbSuccess = getDevicesFromAdb()
        if (resultGetDeviceFromAdbSuccess.isFailure) {
            return Result.failure(
                resultInitialAdb.exceptionOrNull() ?: Throwable(resourceManager.string("getDevicesFromAdbError"))
            )
        }

        // Add listener for changing devices like connect or disconnect
        coroutineScope.launch {
            adbManager.deviceChangeListener().collectLatest {
                delay(DELAY_LONG)
                getDevicesFromAdb()
            }
        }
        return Result.success(true)
    }

    private suspend fun getDevicesFromAdb(): Result<Boolean> {
        clearData()

        val resultGetDevices = adbManager.getDevices()

        return if (resultGetDevices.isSuccess) {
            _devicesStateFlow.emit(resultGetDevices.getOrElse { arrayListOf() })
            _selectedDeviceStateFlow.emit(_devicesStateFlow.value.getOrNull(0))
            Result.success(true)
        } else {
            Result.failure(Throwable(resourceManager.string("getDevicesFromAdbError")))
        }
    }

    override suspend fun setSelectedDevice(device: DeviceInformation?) {
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

    override fun getDeviceSelectedName(): String =
        "${_selectedDeviceStateFlow.value?.brand} ${_selectedDeviceStateFlow.value?.model}"

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

    override suspend fun installApk(packageFilePath: String): Result<String> {
        return adbManager.installApk(
            device = _selectedDeviceStateFlow.value,
            packageFilePath = packageFilePath
        )
    }

    override suspend fun pullFile(remoteFilepath: String, localFilePath: String): Result<String> {
        return adbManager.pullFile(
            device = _selectedDeviceStateFlow.value,
            remoteFilepath = remoteFilepath,
            localFilePath = localFilePath
        )
    }
}
