package androidstudio.tools.missed.features.limitationmodes.domain.usecase.standby.set

import androidstudio.tools.missed.manager.adb.command.StandbyAdbCommands
import androidstudio.tools.missed.manager.device.DeviceManager
import kotlinx.coroutines.flow.flow

class StandbySetUseCaseImpl(private val deviceManager: DeviceManager) : StandbySetUseCase {

    override suspend fun invoke(isActive: Boolean) = flow<Result<Boolean>> {
        val packageId = deviceManager.packageIdSelectedStateFlow.value.orEmpty()
        val adbCommand = if (isActive) {
            StandbyAdbCommands.SetActive(packageId)
        } else {
            StandbyAdbCommands.SetDeactive(packageId)
        }
        deviceManager.executeShellCommand(adbCommand).onSuccess {
            emit(Result.success(true))
        }.onFailure {
            emit(Result.failure(it))
        }
    }
}
