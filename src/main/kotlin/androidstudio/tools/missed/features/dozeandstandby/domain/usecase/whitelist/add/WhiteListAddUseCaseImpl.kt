package androidstudio.tools.missed.features.dozeandstandby.domain.usecase.whitelist.add

import androidstudio.tools.missed.manager.adb.command.WhiteListAdbCommands
import androidstudio.tools.missed.manager.device.DeviceManager
import kotlinx.coroutines.flow.flow

class WhiteListAddUseCaseImpl(private val deviceManager: DeviceManager) : WhiteListAddUseCase {

    override suspend fun invoke() = flow<Result<Boolean>> {
        val packageId = deviceManager.packageIdSelectedStateFlow.value.orEmpty()
        deviceManager.executeShellCommand(
            WhiteListAdbCommands.Add(packageId)
        ).onSuccess {
            emit(Result.success(true))
        }.onFailure {
            emit(Result.failure(it))
        }
    }
}
