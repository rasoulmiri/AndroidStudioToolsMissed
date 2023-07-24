package androidstudio.tools.missed.features.network.presenter

import androidstudio.tools.missed.base.ViewModel
import androidstudio.tools.missed.features.network.domain.usecase.airplane.get.GetAirplaneStateUseCase
import androidstudio.tools.missed.features.network.domain.usecase.airplane.set.SetAirplaneStateUseCase
import androidstudio.tools.missed.features.network.domain.usecase.bluetooth.get.GetBluetoothStateUseCase
import androidstudio.tools.missed.features.network.domain.usecase.bluetooth.set.SetBluetoothStateUseCase
import androidstudio.tools.missed.features.network.domain.usecase.mobiledata.get.GetMobileDataStateUseCase
import androidstudio.tools.missed.features.network.domain.usecase.mobiledata.set.SetMobileDataStateUseCase
import androidstudio.tools.missed.features.network.domain.usecase.wifi.get.GetWifiStateUseCase
import androidstudio.tools.missed.features.network.domain.usecase.wifi.set.SetWifiStateUseCase
import androidstudio.tools.missed.manager.device.DeviceManager
import androidstudio.tools.missed.manager.notification.model.BalloonNotificationModel
import androidstudio.tools.missed.manager.resource.ResourceManager
import androidstudio.tools.missed.utils.DELAY_MEDIUM
import com.intellij.notification.NotificationType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class NetworkViewModel(
    private val coroutineDispatcher: CoroutineDispatcher,
    private val resourceManager: ResourceManager,
    private val deviceManager: DeviceManager,
    private val getAirplaneStateUseCase: GetAirplaneStateUseCase,
    private val setAirplaneStateUseCase: SetAirplaneStateUseCase,
    private val getMobileDataStateUseCase: GetMobileDataStateUseCase,
    private val setMobileDataStateUseCase: SetMobileDataStateUseCase,
    private val getWifiStateUseCase: GetWifiStateUseCase,
    private val setWifiStateUseCase: SetWifiStateUseCase,
    private val getBluetoothStateUseCase: GetBluetoothStateUseCase,
    private val setBluetoothStateUseCase: SetBluetoothStateUseCase
) : ViewModel(coroutineDispatcher) {

    private val _messageSharedFlow = MutableSharedFlow<BalloonNotificationModel>()
    val messageSharedFlow: SharedFlow<BalloonNotificationModel> = _messageSharedFlow.asSharedFlow()

    private val _airplaneModeStateFlow = MutableStateFlow(false)
    val airplaneModeStateFlow: StateFlow<Boolean> = _airplaneModeStateFlow.asStateFlow()

    private val _mobileDataStateFlow = MutableStateFlow(false)
    val mobileDataStateFlow: StateFlow<Boolean> = _mobileDataStateFlow.asStateFlow()

    private val _wifiStateFlow = MutableStateFlow(false)
    val wifiStateFlow: StateFlow<Boolean> = _wifiStateFlow.asStateFlow()

    private val _bluetoothStateFlow = MutableStateFlow(false)
    val bluetoothStateFlow: StateFlow<Boolean> = _bluetoothStateFlow.asStateFlow()

    fun updateStates() {
        viewModelScope.launch {
            deviceManager.selectedDeviceStateFlow.collectLatest {
                getAllState()
            }
        }
    }

    private fun getAllState() {
        getAirplaneModeState()
        getMobileDataState()
        getWifiState()
        getBluetoothState()
    }

    private fun getAirplaneModeState() = viewModelScope.launch {
        getAirplaneStateUseCase.invoke().collect { result ->
            result.onSuccess {
                _airplaneModeStateFlow.emit(it)
            }.onFailure {
                _messageSharedFlow.emit(
                    BalloonNotificationModel(
                        title = resourceManager.string("airplaneModeGetErrorTitle"),
                        content = it.message,
                        type = NotificationType.ERROR
                    )
                )
            }
        }
    }

    fun setStateAirplaneMode(isOn: Boolean) = viewModelScope.launch {
        setAirplaneStateUseCase.invoke(isOn).collect { result ->
            result.onSuccess {
                _airplaneModeStateFlow.emit(isOn)
                delay(DELAY_MEDIUM)
                getAirplaneModeState()
            }.onFailure {
                _messageSharedFlow.emit(
                    BalloonNotificationModel(
                        title = resourceManager.string("airplaneModeSetErrorTitle"),
                        content = it.message,
                        type = NotificationType.ERROR
                    )
                )
            }
        }
    }

    private fun getMobileDataState() = viewModelScope.launch {
        getMobileDataStateUseCase.invoke().collect { result ->
            result.onSuccess {
                _mobileDataStateFlow.emit(it)
            }.onFailure {
                _messageSharedFlow.emit(
                    BalloonNotificationModel(
                        title = resourceManager.string("mobileDataConnectionGetErrorTitle"),
                        content = it.message,
                        type = NotificationType.ERROR
                    )
                )
            }
        }
    }

    fun setStateMobileData(isOn: Boolean) = viewModelScope.launch {
        setMobileDataStateUseCase.invoke(isOn).collect { result ->
            result.onSuccess {
                _mobileDataStateFlow.emit(isOn)
                delay(DELAY_MEDIUM)
                getMobileDataState()
            }.onFailure {
                _messageSharedFlow.emit(
                    BalloonNotificationModel(
                        title = resourceManager.string("mobileDataConnectionSetErrorTitle"),
                        content = it.message,
                        type = NotificationType.ERROR
                    )
                )
            }
        }
    }

    private fun getWifiState() = viewModelScope.launch {
        getWifiStateUseCase.invoke().collect { result ->
            result.onSuccess {
                _wifiStateFlow.emit(it)
            }.onFailure {
                _messageSharedFlow.emit(
                    BalloonNotificationModel(
                        title = resourceManager.string("wifiConnectionGetErrorTitle"),
                        content = it.message,
                        type = NotificationType.ERROR
                    )
                )
            }
        }
    }

    fun setStateWifi(isOn: Boolean) = viewModelScope.launch {
        setWifiStateUseCase.invoke(isOn).collect { result ->
            result.onSuccess {
                _wifiStateFlow.emit(isOn)
                delay(DELAY_MEDIUM)
                getWifiState()
            }.onFailure {
                _messageSharedFlow.emit(
                    BalloonNotificationModel(
                        title = resourceManager.string("wifiConnectionSetErrorTitle"),
                        content = it.message,
                        type = NotificationType.ERROR
                    )
                )
            }
        }
    }

    private fun getBluetoothState() = viewModelScope.launch {
        getBluetoothStateUseCase.invoke().collect { result ->
            result.onSuccess {
                _bluetoothStateFlow.emit(false)
            }.onFailure {
                _messageSharedFlow.emit(
                    BalloonNotificationModel(
                        title = resourceManager.string("bluetoothConnectionGetErrorTitle"),
                        content = it.message,
                        type = NotificationType.ERROR
                    )
                )
            }
        }
    }

    fun setStateBluetooth(isOn: Boolean) = viewModelScope.launch {
        setBluetoothStateUseCase.invoke(isOn).collect { result ->
            result.onSuccess {
                _bluetoothStateFlow.emit(isOn)
                delay(DELAY_MEDIUM)
                getBluetoothState()
            }.onFailure {
                _messageSharedFlow.emit(
                    BalloonNotificationModel(
                        title = resourceManager.string("bluetoothConnectionSetErrorTitle"),
                        content = it.message,
                        type = NotificationType.ERROR
                    )
                )
            }
        }
    }
}
