package androidstudio.tools.missed.features.network.domain.usecase.airplane.get

import androidstudio.tools.missed.manager.adb.command.NetworkAdbCommands
import androidstudio.tools.missed.manager.device.DeviceManager
import kotlinx.coroutines.flow.flow

class GetAirplaneStateUseCaseImpl(private val deviceManager: DeviceManager) : GetAirplaneStateUseCase {

    override suspend fun invoke() = flow<Result<Boolean>> {
        val adbCommand = NetworkAdbCommands.GetAirplaneModeState()
        deviceManager.executeShellCommand(adbCommand).onSuccess {
            emit(Result.success(it == adbCommand.successResultExpect))
        }.onFailure {
            emit(Result.failure(it))
        }
    }
}
