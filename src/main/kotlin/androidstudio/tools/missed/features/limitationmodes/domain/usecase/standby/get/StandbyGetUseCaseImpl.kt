package androidstudio.tools.missed.features.limitationmodes.domain.usecase.standby.get

import androidstudio.tools.missed.manager.adb.command.StandbyAdbCommands
import androidstudio.tools.missed.manager.device.DeviceManager
import kotlinx.coroutines.flow.flow

class StandbyGetUseCaseImpl(private val deviceManager: DeviceManager) : StandbyGetUseCase {

    override suspend fun invoke() = flow<Result<Boolean>> {
        val packageId = deviceManager.packageIdSelectedStateFlow.value.orEmpty()
        deviceManager.executeShellCommand(
            StandbyAdbCommands.GetState(packageId)
        ).onSuccess { result ->
            emit(Result.success(parseStandbyState(result)))
        }.onFailure {
            emit(Result.failure(it))
        }
    }

    private fun parseStandbyState(successResult: String): Boolean = successResult.contains("Idle=true")
}
