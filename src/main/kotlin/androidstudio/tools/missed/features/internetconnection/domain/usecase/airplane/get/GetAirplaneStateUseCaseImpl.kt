package androidstudio.tools.missed.features.internetconnection.domain.usecase.airplane.get

import androidstudio.tools.missed.manager.adb.command.InternetAdbConnectionCommands
import androidstudio.tools.missed.manager.device.DeviceManager
import kotlinx.coroutines.flow.flow

class GetAirplaneStateUseCaseImpl(private val deviceManager: DeviceManager) : GetAirplaneStateUseCase {

    override suspend fun invoke() = flow<Result<Boolean>> {
        val adbCommand = InternetAdbConnectionCommands.GetAirplaneModeState()
        deviceManager.executeShellCommand(adbCommand).onSuccess {
            emit(Result.success(it == adbCommand.successResultExpect))
        }.onFailure {
            emit(Result.failure(it))
        }
    }
}
