package androidstudio.tools.missed.features.dozeandstandby.domain.usecase.whitelist.remove

import androidstudio.tools.missed.manager.adb.command.WhiteListAdbCommands
import androidstudio.tools.missed.manager.device.DeviceManager
import kotlinx.coroutines.flow.flow

class WhiteListRemoveUseCaseImpl(private val deviceManager: DeviceManager) : WhiteListRemoveUseCase {

    override suspend fun invoke() = flow<Result<Boolean>> {
        val packageId = deviceManager.packageIdSelectedStateFlow.value.orEmpty()

        deviceManager.executeShellCommand(
            WhiteListAdbCommands.Remove(packageId = packageId)
        ).onSuccess {
            emit(Result.success(true))
        }.onFailure {
            emit(Result.failure(it))
        }
    }
}
