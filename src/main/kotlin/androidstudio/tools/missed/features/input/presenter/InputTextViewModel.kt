package androidstudio.tools.missed.features.input.presenter

import androidstudio.tools.missed.base.ViewModel
import androidstudio.tools.missed.features.input.domain.usecase.cleartext.ClearTextUseCase
import androidstudio.tools.missed.features.input.domain.usecase.sendevent.SendEventUseCase
import androidstudio.tools.missed.features.input.domain.usecase.sendtext.SendTextUseCase
import androidstudio.tools.missed.features.input.model.EventKey
import androidstudio.tools.missed.manager.notification.model.BalloonNotificationModel
import androidstudio.tools.missed.manager.resource.ResourceManager
import androidstudio.tools.missed.utils.DELAY_MEDIUM
import com.intellij.notification.NotificationType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class InputTextViewModel(
    private val coroutineDispatcher: CoroutineDispatcher,
    private val resourceManager: ResourceManager,
    private val sendTextUseCase: SendTextUseCase,
    private val sendEventUseCase: SendEventUseCase,
    private val clearTextUseCase: ClearTextUseCase
) : ViewModel(coroutineDispatcher) {

    private val _messageSharedFlow = MutableSharedFlow<BalloonNotificationModel>()
    val messageSharedFlow: SharedFlow<BalloonNotificationModel> = _messageSharedFlow.asSharedFlow()

    fun sendTextToDevice(text: String) {
        viewModelScope.launch {
            if (text.isEmpty()) {
                _messageSharedFlow.emit(
                    BalloonNotificationModel(
                        content = resourceManager.string("inputTextIsEmptyError"),
                        type = NotificationType.INFORMATION
                    )
                )
                return@launch
            }

            sendTextUseCase.invoke(text).collect { result ->
                result.onSuccess {
                    _messageSharedFlow.emit(
                        BalloonNotificationModel(
                            content = resourceManager.string("successSendText", text),
                            type = NotificationType.INFORMATION
                        )
                    )
                }.onFailure {
                    val errorText = "${resourceManager.string("errorSendTextTitle")} ${it.message}"
                    _messageSharedFlow.emit(
                        BalloonNotificationModel(
                            title = resourceManager.string("errorSendTextTitle"),
                            content = errorText,
                            type = NotificationType.ERROR
                        )
                    )
                }
            }
        }
    }

    fun clearAndSendTextToDevice(text: String) {
        viewModelScope.launch {
            clearTextInEditText().join()
            delay(DELAY_MEDIUM)
            sendTextToDevice(text)
        }
    }

    fun sendEventToDevice(event: EventKey) {
        viewModelScope.launch {
            sendEventUseCase.invoke(event = event.value).collect { result ->
                result.onFailure {
                    _messageSharedFlow.emit(
                        BalloonNotificationModel(
                            title = resourceManager.string("errorSendEventTitle"),
                            content = it.message,
                            type = NotificationType.ERROR
                        )
                    )
                }
            }
        }
    }

    fun clearTextInEditText() = viewModelScope.launch {
        clearTextUseCase.invoke().collect { result ->
            result.onFailure {
                _messageSharedFlow.emit(
                    BalloonNotificationModel(
                        title = resourceManager.string("errorClearTextTitle"),
                        content = it.message,
                        type = NotificationType.ERROR
                    )
                )
            }
        }
    }
}
