package androidstudio.tools.missed.features.battery.presenter

import androidstudio.tools.missed.base.ViewModel
import androidstudio.tools.missed.features.battery.domain.usecase.batterylevel.get.GetBatteryLevelUseCase
import androidstudio.tools.missed.features.battery.domain.usecase.batterylevel.set.SetBatteryLevelUseCase
import androidstudio.tools.missed.features.battery.domain.usecase.chargerconnection.get.GetChargerConnectionUseCase
import androidstudio.tools.missed.features.battery.domain.usecase.chargerconnection.set.SetChargerConnectionUseCase
import androidstudio.tools.missed.features.battery.domain.usecase.powersaving.get.GetPowerSavingUseCase
import androidstudio.tools.missed.features.battery.domain.usecase.powersaving.set.SetPowerSavingUseCase
import androidstudio.tools.missed.features.battery.domain.usecase.resetbatteryconfig.ResetBatteryConfigUseCase
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
import kotlinx.coroutines.launch

class BatteryViewModel(
    private val coroutineDispatcher: CoroutineDispatcher,
    private val resourceManager: ResourceManager,
    private val getChargerConnectionUseCase: GetChargerConnectionUseCase,
    private val setChargerConnectionUseCase: SetChargerConnectionUseCase,
    private val getBatteryLevelUseCase: GetBatteryLevelUseCase,
    private val setBatteryLevelUseCase: SetBatteryLevelUseCase,
    private val getPowerSavingUseCase: GetPowerSavingUseCase,
    private val setPowerSavingUseCase: SetPowerSavingUseCase,
    private val resetBatteryConfigUseCase: ResetBatteryConfigUseCase
) : ViewModel(coroutineDispatcher) {

    private val _messageSharedFlow = MutableSharedFlow<BalloonNotificationModel>()
    val messageSharedFlow: SharedFlow<BalloonNotificationModel> = _messageSharedFlow.asSharedFlow()

    private val _chargerConnectionStateFlow = MutableStateFlow(false)
    val chargerConnectionStateFlow: StateFlow<Boolean> = _chargerConnectionStateFlow.asStateFlow()

    private val _batteryLevelStateFlow = MutableStateFlow(0)
    val batteryLevelStateFlow: StateFlow<Int> = _batteryLevelStateFlow.asStateFlow()

    private val _powerSavingModeActiveStateFlow = MutableStateFlow(false)
    val powerSavingModeActiveStateFlow: StateFlow<Boolean> = _powerSavingModeActiveStateFlow.asStateFlow()

    fun getBatteryState() {
        getChargerConnection()
        getBatteryLevel()
        getStatePowerSavingMode()
    }

    private fun getChargerConnection() {
        viewModelScope.launch {
            getChargerConnectionUseCase.invoke().collect { result ->
                result.onSuccess {
                    _chargerConnectionStateFlow.emit(it)
                }.onFailure {
                    _messageSharedFlow.emit(
                        BalloonNotificationModel(
                            title = resourceManager.string("ChargerConnectionGetFailed"),
                            content = it.message,
                            type = NotificationType.ERROR
                        )
                    )
                }
            }
        }
    }

    fun setStateChargerConnection(isConnect: Boolean) {
        viewModelScope.launch {
            setChargerConnectionUseCase.invoke(isConnect = isConnect).collect { result ->
                result.onSuccess {
                    _chargerConnectionStateFlow.emit(it)
                    delay(DELAY_MEDIUM)
                    getChargerConnection()
                    getBatteryLevel()
                }.onFailure {
                    _messageSharedFlow.emit(
                        BalloonNotificationModel(
                            title = resourceManager.string("ChargerConnectionSetFailed"),
                            content = it.message,
                            type = NotificationType.ERROR
                        )
                    )
                }
            }
        }
    }

    private fun getBatteryLevel() {
        viewModelScope.launch {
            getBatteryLevelUseCase.invoke().collect { result ->
                result.onSuccess {
                    _batteryLevelStateFlow.emit(it)
                }.onFailure {
                    _messageSharedFlow.emit(
                        BalloonNotificationModel(
                            title = resourceManager.string("batteryLevelGetFailed"),
                            content = it.message,
                            type = NotificationType.ERROR
                        )
                    )
                }
            }
        }
    }

    fun setBatteryLevel(batteryLevel: Int) = viewModelScope.launch {
        setBatteryLevelUseCase.invoke(batteryLevel = batteryLevel).collect { result ->
            result.onFailure {
                _messageSharedFlow.emit(
                    BalloonNotificationModel(
                        title = resourceManager.string("batteryLevelSetFailed"),
                        content = it.message,
                        type = NotificationType.ERROR
                    )
                )
            }
        }
    }

    private fun getStatePowerSavingMode() = viewModelScope.launch {
        getPowerSavingUseCase.invoke().collect { result ->
            result.onSuccess {
                _powerSavingModeActiveStateFlow.emit(it)
            }.onFailure {
                _messageSharedFlow.emit(
                    BalloonNotificationModel(
                        title = resourceManager.string("PowerSavingModeGetFailed"),
                        content = it.message,
                        type = NotificationType.ERROR
                    )
                )
            }
        }
    }

    fun setStatePowerSavingMode(isActive: Boolean) {
        viewModelScope.launch {
            setPowerSavingUseCase.invoke(isActive = isActive).collect { result ->
                result.onSuccess {
                    _powerSavingModeActiveStateFlow.emit(it)
                    delay(DELAY_MEDIUM)
                    getStatePowerSavingMode()
                }.onFailure {
                    _messageSharedFlow.emit(
                        BalloonNotificationModel(
                            title = resourceManager.string("PowerSavingModeSetFailed"),
                            content = it.message,
                            type = NotificationType.ERROR
                        )
                    )
                }
            }
        }
    }

    fun resetBatterySetting() {
        viewModelScope.launch {
            resetBatteryConfigUseCase.invoke().collect { result ->
                result.onSuccess {
                    _messageSharedFlow.emit(
                        BalloonNotificationModel(
                            content = resourceManager.string("batteryResetSuccessful"),
                            type = NotificationType.INFORMATION
                        )
                    )
                    delay(DELAY_MEDIUM)
                    getBatteryState()
                }.onFailure {
                    _messageSharedFlow.emit(
                        BalloonNotificationModel(
                            title = resourceManager.string("batteryResetFailed"),
                            content = it.message,
                            type = NotificationType.ERROR
                        )
                    )
                }
            }
        }
    }
}
