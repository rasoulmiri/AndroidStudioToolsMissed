package androidstudio.tools.missed.features.network.domain.usecase.bluetooth.set

import androidstudio.tools.missed.manager.adb.command.NetworkAdbCommands
import androidstudio.tools.missed.manager.device.DeviceManager
import kotlinx.coroutines.flow.flow

class SetBluetoothStateUseCaseImpl(private val deviceManager: DeviceManager) : SetBluetoothStateUseCase {

    override suspend fun invoke(state: Boolean) = flow<Result<Boolean>> {
        deviceManager.executeShellCommand(NetworkAdbCommands.SetBluetoothState(isOn = state)).onSuccess {
            emit(Result.success(true))
        }.onFailure {
            emit(Result.failure(it))
        }
    }
}
