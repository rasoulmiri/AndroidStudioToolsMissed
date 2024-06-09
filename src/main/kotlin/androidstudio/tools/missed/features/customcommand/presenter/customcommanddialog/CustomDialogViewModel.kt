package androidstudio.tools.missed.features.customcommand.presenter.customcommanddialog

import androidstudio.tools.missed.base.ViewModel
import androidstudio.tools.missed.features.customcommand.domain.CustomCommandRepository
import androidstudio.tools.missed.features.customcommand.domain.CustomCommandUseCase
import androidstudio.tools.missed.features.customcommand.model.CustomCommand
import androidstudio.tools.missed.manager.resource.ResourceManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class CustomDialogViewModel(
    private val coroutineDispatcher: CoroutineDispatcher,
    private val resourceManager: ResourceManager,
    private val customCommandUseCase: CustomCommandUseCase,
    private val repository: CustomCommandRepository
) : ViewModel(coroutineDispatcher) {

    private val _commandStateFlow = MutableStateFlow(CustomCommand.EMPTY)
    val commandStateFlow: StateFlow<CustomCommand> = _commandStateFlow.asStateFlow()

    private val _resultStateFlow = MutableStateFlow("")
    val resultStateFlow: StateFlow<String> = _resultStateFlow.asStateFlow()

    fun setup(customCommand: CustomCommand) {
        viewModelScope.launch {
            _commandStateFlow.emit(customCommand)
        }
    }

    fun executeCommand(name: String?, description: String?, command: String?) {
        val newCustomCommand = CustomCommand(
            id = getInputId(),
            index = getInputIndex(),
            name = name,
            description = description,
            command = command
        )

        viewModelScope.launch {
            customCommandUseCase.invoke(newCustomCommand).collect { result ->
                result.onSuccess {
                    _resultStateFlow.emit(it)
                }.onFailure {
                    _resultStateFlow.emit(it.message ?: resourceManager.string("errorGeneral"))
                }
            }
        }
    }

    fun save(name: String?, description: String?, command: String?) {
        val newCustomCommand = CustomCommand(
            id = getInputId(),
            index = getInputIndex(),
            name = name?.trim(),
            description = description?.trim(),
            command = command?.trim()
        )
        repository.save(newCustomCommand)
    }

    private fun getInputId(): Int = _commandStateFlow.value.id ?: repository.generateUniqueId()
    private fun getInputIndex(): Int = _commandStateFlow.value.index ?: repository.getNextIndex()
}
