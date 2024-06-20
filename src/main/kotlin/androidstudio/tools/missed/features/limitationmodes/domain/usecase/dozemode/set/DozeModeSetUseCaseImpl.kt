package androidstudio.tools.missed.features.limitationmodes.domain.usecase.dozemode.set

import androidstudio.tools.missed.manager.adb.command.DozeAdbCommands
import androidstudio.tools.missed.manager.device.DeviceManager
import kotlinx.coroutines.flow.flow

class DozeModeSetUseCaseImpl(private val deviceManager: DeviceManager) : DozeModeSetUseCase {

    override suspend fun invoke(isActive: Boolean) = flow<Result<Boolean>> {
        val adbCommand = if (isActive) {
            DozeAdbCommands.SetActive()
        } else {
            DozeAdbCommands.SetDeactive()
        }
        deviceManager.executeShellCommand(adbCommand).onSuccess {
            emit(Result.success(true))
        }.onFailure {
            emit(Result.failure(it))
        }
    }
}
