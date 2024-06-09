package androidstudio.tools.missed.features.customcommand.domain

import androidstudio.tools.missed.features.customcommand.model.CustomCommand
import androidstudio.tools.missed.manager.device.DeviceManager
import kotlinx.coroutines.flow.flow

class CustomCommandUseCaseImpl(
    private val deviceManager: DeviceManager
) : CustomCommandUseCase {

    override suspend fun invoke(customCommand: CustomCommand) = flow<Result<String>> {
        deviceManager.executeCustomCommand(customCommand)
            .onSuccess {
                emit(Result.success(it))
            }.onFailure {
                emit(Result.failure(it))
            }
    }
}
