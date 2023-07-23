package androidstudio.tools.missed.features.input.domain.usecase.cleartext

import androidstudio.tools.missed.manager.adb.command.AdbCommand
import androidstudio.tools.missed.manager.device.DeviceManager
import kotlinx.coroutines.flow.flow

class ClearTextUseCaseImpl(private val deviceManager: DeviceManager) : ClearTextUseCase {

    override suspend fun invoke() = flow<Result<Unit>> {
        deviceManager.executeShellCommand(AdbCommand.ClearEditText())
            .onSuccess { emit(Result.success(Unit)) }
            .onFailure { emit(Result.failure(it)) }
    }
}
