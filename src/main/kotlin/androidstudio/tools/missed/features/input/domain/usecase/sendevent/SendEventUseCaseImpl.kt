package androidstudio.tools.missed.features.input.domain.usecase.sendevent

import androidstudio.tools.missed.manager.adb.command.AdbCommand
import androidstudio.tools.missed.manager.device.DeviceManager
import kotlinx.coroutines.flow.flow

class SendEventUseCaseImpl(private val deviceManager: DeviceManager) : SendEventUseCase {
    override suspend fun invoke(event: String) = flow<Result<Unit>> {
        deviceManager.executeShellCommand(AdbCommand.InputEvent(event = event))
            .onSuccess { emit(Result.success(Unit)) }
            .onFailure { emit(Result.failure(it)) }
    }
}
