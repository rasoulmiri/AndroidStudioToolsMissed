package androidstudio.tools.missed.features.dozeandstandby.presenter

import androidstudio.tools.missed.base.ViewModel
import androidstudio.tools.missed.features.dozeandstandby.domain.usecase.dozemode.get.DozeModeGetUseCase
import androidstudio.tools.missed.features.dozeandstandby.domain.usecase.dozemode.set.DozeModeSetUseCase
import androidstudio.tools.missed.features.dozeandstandby.domain.usecase.standby.get.StandbyGetUseCase
import androidstudio.tools.missed.features.dozeandstandby.domain.usecase.standby.set.StandbySetUseCase
import androidstudio.tools.missed.features.dozeandstandby.domain.usecase.whitelist.add.WhiteListAddUseCase
import androidstudio.tools.missed.features.dozeandstandby.domain.usecase.whitelist.get.WhiteListGetUseCase
import androidstudio.tools.missed.features.dozeandstandby.domain.usecase.whitelist.remove.WhiteListRemoveUseCase
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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DozeAndStandbyViewModel(
    private val coroutineDispatcher: CoroutineDispatcher,
    private val resourceManager: ResourceManager,
    private val deviceManager: DeviceManager,
    private val dozeModeGetUseCase: DozeModeGetUseCase,
    private val dozeModeSetUseCase: DozeModeSetUseCase,
    private val standbyGetUseCase: StandbyGetUseCase,
    private val standbySetUseCase: StandbySetUseCase,
    private val whiteListGetUseCase: WhiteListGetUseCase,
    private val whiteListAddUseCase: WhiteListAddUseCase,
    private val whiteListRemoveUseCase: WhiteListRemoveUseCase
) : ViewModel(coroutineDispatcher) {

    private val _messageSharedFlow = MutableSharedFlow<BalloonNotificationModel>()
    val messageSharedFlow: SharedFlow<BalloonNotificationModel> = _messageSharedFlow.asSharedFlow()

    val packageIdSelectedStateFlow: StateFlow<String?> = deviceManager.packageIdSelectedStateFlow.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        ""
    )

    private val _dozeStateStateFlow = MutableStateFlow(false)
    val dozeStateStateFlow: StateFlow<Boolean> = _dozeStateStateFlow.asStateFlow()

    private val _dozeStateFlow = MutableStateFlow("")
    val dozeStateFlow: StateFlow<String> = _dozeStateFlow.asStateFlow()

    private val _standbyStateStateFlow = MutableStateFlow(false)
    val standbyStateStateFlow: StateFlow<Boolean> = _standbyStateStateFlow.asStateFlow()

    val whiteListPackageIds = ArrayList<String>()
    private val _packageIdInWhiteListStateFlow = MutableStateFlow(false)
    val packageIdInWhiteListStateFlow: StateFlow<Boolean> = _packageIdInWhiteListStateFlow.asStateFlow()

    fun getStateDozeMode() = viewModelScope.launch {
        dozeModeGetUseCase.invoke().collect { result ->
            result.onSuccess {
                _dozeStateStateFlow.emit(it.isActive)
                _dozeStateFlow.emit(it.state)
            }.onFailure {
                _messageSharedFlow.emit(
                    BalloonNotificationModel(
                        title = resourceManager.string("dozeModeGetFailedTitle"),
                        content = it.message,
                        type = NotificationType.ERROR
                    )
                )
            }
        }
    }

    fun setStateDozeMode(isActive: Boolean) = viewModelScope.launch {
        dozeModeSetUseCase.invoke(isActive = isActive).collect { result ->
            result.onSuccess {
                _dozeStateStateFlow.emit(it)
                delay(DELAY_MEDIUM)
                getStateDozeMode()
            }.onFailure {
                _messageSharedFlow.emit(
                    BalloonNotificationModel(
                        title = resourceManager.string("dozeModeSetFailedTitle"),
                        content = it.message,
                        type = NotificationType.ERROR
                    )
                )
            }
        }
    }

    fun getStateStandbyMode() = viewModelScope.launch {
        standbyGetUseCase.invoke().collect { result ->
            result.onSuccess {
                _standbyStateStateFlow.emit(it)
            }.onFailure {
                _messageSharedFlow.emit(
                    BalloonNotificationModel(
                        title = resourceManager.string("standbyGetFailedTitle"),
                        content = it.message,
                        type = NotificationType.ERROR
                    )
                )
            }
        }
    }

    fun setStateStandbyMode(isActive: Boolean) = viewModelScope.launch {
        standbySetUseCase.invoke(isActive = isActive).collect { result ->
            result.onSuccess {
                _standbyStateStateFlow.emit(it)
                delay(DELAY_MEDIUM)
                getStateStandbyMode()
            }.onFailure {
                _messageSharedFlow.emit(
                    BalloonNotificationModel(
                        title = resourceManager.string("standbySetFailedTitle"),
                        content = it.message,
                        type = NotificationType.ERROR
                    )
                )
            }
        }
    }

    fun fetchAllWhiteList() = viewModelScope.launch {
        whiteListGetUseCase.invoke().collect { result ->
            result.onSuccess { packages ->
                whiteListPackageIds.clear()
                whiteListPackageIds.addAll(packages)
                _packageIdInWhiteListStateFlow.emit(
                    packages.any {
                        it.contains(packageIdSelectedStateFlow.value.orEmpty())
                    }
                )
            }.onFailure {
                _messageSharedFlow.emit(
                    BalloonNotificationModel(
                        title = resourceManager.string("whiteListFetchAllFailedTitle"),
                        content = it.message,
                        type = NotificationType.ERROR
                    )
                )
            }
        }
    }

    fun addToWhiteList() = viewModelScope.launch {
        whiteListAddUseCase.invoke().collect { result ->
            result.onSuccess {
                fetchAllWhiteList()
            }.onFailure {
                _messageSharedFlow.emit(
                    BalloonNotificationModel(
                        title = resourceManager.string("whiteListAddFailedTitle"),
                        content = it.message,
                        type = NotificationType.ERROR
                    )
                )
            }
        }
    }

    fun removeFromWhiteList() = viewModelScope.launch {
        whiteListRemoveUseCase.invoke().collect { result ->
            result.onSuccess {
                fetchAllWhiteList()
            }.onFailure {
                _messageSharedFlow.emit(
                    BalloonNotificationModel(
                        title = resourceManager.string("whiteListRemoveFailedTitle"),
                        content = it.message,
                        type = NotificationType.ERROR
                    )
                )
            }
        }
    }
}
