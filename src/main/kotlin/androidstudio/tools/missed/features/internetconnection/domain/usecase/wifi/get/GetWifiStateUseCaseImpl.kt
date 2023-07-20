package androidstudio.tools.missed.features.internetconnection.domain.usecase.wifi.get

import androidstudio.tools.missed.manager.adb.command.InternetAdbConnectionCommands
import androidstudio.tools.missed.manager.device.DeviceManager
import kotlinx.coroutines.flow.flow

class GetWifiStateUseCaseImpl(private val deviceManager: DeviceManager) : GetWifiStateUseCase {

    override suspend fun invoke() = flow<Result<Boolean>> {
        val adbCommand = InternetAdbConnectionCommands.GetWifiState()

        deviceManager.executeShellCommand(adbCommand).onSuccess {
            emit(Result.success(it == adbCommand.successResultExpect))
        }.onFailure {
            emit(Result.failure(it))
        }
    }
}
