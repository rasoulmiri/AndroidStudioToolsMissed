package androidstudio.tools.missed.features.customcommand.presenter

import androidstudio.tools.missed.base.ViewModel
import androidstudio.tools.missed.features.customcommand.domain.CustomCommandRepository
import androidstudio.tools.missed.features.customcommand.domain.CustomCommandUseCase
import androidstudio.tools.missed.features.customcommand.model.CustomCommand
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
import kotlinx.coroutines.launch

class CustomCommandViewModel(
    private val coroutineDispatcher: CoroutineDispatcher,
    private val resourceManager: ResourceManager,
    private val customCommandUseCase: CustomCommandUseCase,
    private val repository: CustomCommandRepository
) : ViewModel(coroutineDispatcher) {

    private val _messageSharedFlow = MutableSharedFlow<BalloonNotificationModel>()
    val messageSharedFlow: SharedFlow<BalloonNotificationModel> = _messageSharedFlow.asSharedFlow()

    private val _customCommandsStateFlow = MutableStateFlow((emptyList<CustomCommand>()))
    val customCommandsStateFlow: StateFlow<List<CustomCommand>> = _customCommandsStateFlow.asStateFlow()

    init {
        updateData()
    }

    fun updateData() {
        viewModelScope.launch {
            _customCommandsStateFlow.emit(repository.loadAll())
        }
    }

    fun executeCommand(customCommand: CustomCommand) {
        viewModelScope.launch {
            customCommandUseCase.invoke(customCommand).collect { result ->
                result.onSuccess {
                    _messageSharedFlow.emit(
                        BalloonNotificationModel(
                            title = customCommand.name ?: "",
                            content = it,
                            type = NotificationType.INFORMATION,
                            fadeoutTime = 3000L
                        )
                    )
                }.onFailure {
                    _messageSharedFlow.emit(
                        BalloonNotificationModel(
                            title = resourceManager.string("customCommandErrorCustomTitle", customCommand.name ?: ""),
                            content = it.message,
                            type = NotificationType.ERROR
                        )
                    )
                }
            }
        }
    }

    fun deleteById(id: Int?) {
        id?.let {
            repository.deleteById(id)
            updateData()
        }
    }
}
