package androidstudio.tools.missed.features.deviceAndpackageid.presenter

import androidstudio.tools.missed.base.ViewModel
import androidstudio.tools.missed.features.deviceAndpackageid.domain.usecase.GetPackageIdsInstalledInDeviceUseCase
import androidstudio.tools.missed.manager.device.DeviceManager
import androidstudio.tools.missed.manager.device.model.Device
import androidstudio.tools.missed.manager.notification.model.BalloonNotificationModel
import androidstudio.tools.missed.manager.resource.ResourceManager
import com.intellij.notification.NotificationType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DevicesAndPackageIdsViewModel(
    private val coroutineDispatcher: CoroutineDispatcher,
    private val resourceManager: ResourceManager,
    private val deviceManager: DeviceManager,
    private val getPackageIdsInstalledInDeviceUseCase: GetPackageIdsInstalledInDeviceUseCase
) : ViewModel(coroutineDispatcher) {

    private val _messageSharedFlow = MutableSharedFlow<BalloonNotificationModel>()
    val messageSharedFlow: SharedFlow<BalloonNotificationModel> = _messageSharedFlow.asSharedFlow()

    private val _devicesStateFlow = MutableStateFlow<List<Device>>(arrayListOf())
    val devicesStateFlow: StateFlow<List<Device>> = _devicesStateFlow.asStateFlow()

    private val _packageIdsStateFlow = MutableStateFlow<ArrayList<String>>(arrayListOf())
    val packageIdsStateFlow: StateFlow<ArrayList<String>> = _packageIdsStateFlow.asStateFlow()

    private var isSelectedShowAllPackageIds: Boolean = false

    init {

        viewModelScope.launch {
            deviceManager.devicesStateFlow.collectLatest {
                _devicesStateFlow.emit(it)
            }
        }

        viewModelScope.launch {
            deviceManager.selectedDeviceStateFlow.collectLatest {
                if (it != null) {
                    updatePackageIds()
                } else {
                    _packageIdsStateFlow.emit(arrayListOf())
                }
            }
        }
    }

    private fun updatePackageIds() = viewModelScope.launch {
        getPackageIdsInstalledInDeviceUseCase.invoke(isSelectedShowAllPackageIds = isSelectedShowAllPackageIds)
            .collect { result ->
                result.onSuccess {
                    _packageIdsStateFlow.emit(it)
                    deviceManager.setSelectedPackageId(_packageIdsStateFlow.value.getOrNull(0))
                }.onFailure {
                    _messageSharedFlow.emit(
                        BalloonNotificationModel(
                            title = resourceManager.string("failGetPackageIdsFromDevice"),
                            content = it.message,
                            type = NotificationType.ERROR
                        )
                    )
                }
            }
    }

    fun setSelectedDevice(device: Device?) {
        viewModelScope.launch {
            deviceManager.setSelectedDevice(device)
        }
    }

    fun selectedPackageId(packageId: String) {
        viewModelScope.launch {
            deviceManager.setSelectedPackageId(packageId)
        }
    }

    fun showAllPackageIdsChangeEvent(isChecked: Boolean) {
        isSelectedShowAllPackageIds = isChecked
        updatePackageIds()
    }
}
