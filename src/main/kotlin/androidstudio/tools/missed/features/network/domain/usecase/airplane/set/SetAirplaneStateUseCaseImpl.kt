package androidstudio.tools.missed.features.network.domain.usecase.airplane.set

import androidstudio.tools.missed.manager.adb.command.NetworkAdbCommands
import androidstudio.tools.missed.manager.device.DeviceManager
import kotlinx.coroutines.flow.flow

class SetAirplaneStateUseCaseImpl(private val deviceManager: DeviceManager) : SetAirplaneStateUseCase {

    override suspend fun invoke(state: Boolean) = flow<Result<Boolean>> {
        val adbCommand = NetworkAdbCommands.SetAirplaneModeState(isOn = state)

        deviceManager.executeShellCommand(adbCommand).onSuccess {
            emit(Result.success(true))
        }.onFailure {
            emit(Result.failure(it))
        }
    }
}
