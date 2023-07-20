package androidstudio.tools.missed.features.internetconnection.domain.usecase.wifi.set

import androidstudio.tools.missed.manager.adb.command.InternetAdbConnectionCommands
import androidstudio.tools.missed.manager.device.DeviceManager
import kotlinx.coroutines.flow.flow

class SetWifiStateUseCaseImpl(private val deviceManager: DeviceManager) : SetWifiStateUseCase {

    override suspend fun invoke(state: Boolean) = flow<Result<Boolean>> {
        val adbCommand = InternetAdbConnectionCommands.SetWifiState(isOn = state)

        deviceManager.executeShellCommand(adbCommand).onSuccess {
            emit(Result.success(true))
        }.onFailure {
            emit(Result.failure(it))
        }
    }
}
