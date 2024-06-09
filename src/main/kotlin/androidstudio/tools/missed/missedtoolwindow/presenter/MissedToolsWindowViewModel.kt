package androidstudio.tools.missed.missedtoolwindow.presenter

import androidstudio.tools.missed.base.ViewModel
import androidstudio.tools.missed.manager.device.DeviceManager
import androidstudio.tools.missed.manager.notification.model.BalloonNotificationModel
import androidstudio.tools.missed.missedtoolwindow.model.MissedToolsWindowStateUi
import com.intellij.notification.NotificationType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MissedToolsWindowViewModel(
    private val coroutineDispatcher: CoroutineDispatcher,
    private val deviceManager: DeviceManager
) : ViewModel(coroutineDispatcher) {

    private val _messageSharedFlow = MutableSharedFlow<BalloonNotificationModel>()
    val messageSharedFlow: SharedFlow<BalloonNotificationModel> = _messageSharedFlow.asSharedFlow()

    private val _uiStateFlow = MutableStateFlow<MissedToolsWindowStateUi>(
        MissedToolsWindowStateUi.Loading
    )
    val uiStateFlow: StateFlow<MissedToolsWindowStateUi> = _uiStateFlow.asStateFlow()

    private var initialJob: Job? = null
    private var deviceListenerJob: Job? = null

    fun initial() {
        viewModelScope.coroutineContext.cancelChildren()
        initialJob?.cancel()
        initialJob = viewModelScope.launch {
            _uiStateFlow.emit(MissedToolsWindowStateUi.Loading)

            viewModelScope.async {
                deviceManager.configure(viewModelScope)
            }.await().onSuccess { isSuccessConfiguration ->
                _uiStateFlow.emit(
                    if (isSuccessConfiguration) {
                        initialDeviceListener()
                        if (deviceManager.devicesStateFlow.value.isNotEmpty()) {
                            MissedToolsWindowStateUi.Success
                        } else {
                            MissedToolsWindowStateUi.NeedToConnectDevice
                        }
                    } else {
                        MissedToolsWindowStateUi.Error
                    }
                )
            }.onFailure {
                _messageSharedFlow.emit(
                    BalloonNotificationModel(content = it.message, type = NotificationType.ERROR)
                )
                _uiStateFlow.emit(MissedToolsWindowStateUi.Error)
            }
        }
    }

    private fun initialDeviceListener() {
        deviceListenerJob?.cancel()
        deviceListenerJob = viewModelScope.launch {
            deviceManager.devicesStateFlow.collectLatest {
                if (it.isEmpty()) {
                    _uiStateFlow.emit(MissedToolsWindowStateUi.NeedToConnectDevice)
                } else {
                    _uiStateFlow.emit(MissedToolsWindowStateUi.Success)
                }
            }
        }
    }
}
