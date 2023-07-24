package androidstudio.tools.missed.features.network.domain.usecase.bluetooth.get

import androidstudio.tools.missed.manager.adb.command.NetworkAdbCommands
import androidstudio.tools.missed.manager.device.DeviceManager
import kotlinx.coroutines.flow.flow

class GetBluetoothStateUseCaseImpl(private val deviceManager: DeviceManager) : GetBluetoothStateUseCase {

    override suspend fun invoke() = flow<Result<Boolean>> {
        val adbCommand = NetworkAdbCommands.GetBluetoothState()

        deviceManager.executeShellCommand(adbCommand).onSuccess {
            emit(Result.success(it == adbCommand.successResultExpect))
        }.onFailure {
            emit(Result.failure(it))
        }
    }
}
