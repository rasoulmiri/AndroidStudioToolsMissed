package androidstudio.tools.missed.features.dozeandstandby.domain.usecase.dozemode.get

import androidstudio.tools.missed.features.dozeandstandby.domain.usecase.dozemode.get.entity.DozeModeGetStateModel
import androidstudio.tools.missed.manager.adb.command.DozeAdbCommands
import androidstudio.tools.missed.manager.device.DeviceManager
import kotlinx.coroutines.flow.flow

class DozeModeGetUseCaseImpl(private val deviceManager: DeviceManager) : DozeModeGetUseCase {

    override suspend fun invoke() = flow<Result<DozeModeGetStateModel>> {
        deviceManager.executeShellCommand(DozeAdbCommands.GetState()).onSuccess {
            emit(Result.success(createDozeModeGetStateModel(it)))
        }.onFailure {
            emit(Result.failure(it))
        }
    }

    private fun createDozeModeGetStateModel(output: String): DozeModeGetStateModel {
        val isActive = output.contains("Stepped to deep: IDLE") || output.contains("Stepped to deep: IDLE_MAINTENANCE")
        val state = output.replace("Stepped to deep: ", "")
        return DozeModeGetStateModel(isActive, state)
    }
}
