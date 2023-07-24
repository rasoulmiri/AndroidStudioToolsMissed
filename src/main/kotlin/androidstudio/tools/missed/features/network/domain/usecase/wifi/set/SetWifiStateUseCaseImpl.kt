package androidstudio.tools.missed.features.network.domain.usecase.wifi.set

import androidstudio.tools.missed.manager.adb.command.NetworkAdbCommands
import androidstudio.tools.missed.manager.device.DeviceManager
import kotlinx.coroutines.flow.flow

class SetWifiStateUseCaseImpl(private val deviceManager: DeviceManager) : SetWifiStateUseCase {

    override suspend fun invoke(state: Boolean) = flow<Result<Boolean>> {
        val adbCommand = NetworkAdbCommands.SetWifiState(isOn = state)

        deviceManager.executeShellCommand(adbCommand).onSuccess {
            emit(Result.success(true))
        }.onFailure {
            emit(Result.failure(it))
        }
    }
}
