package androidstudio.tools.missed.features.internetconnection.domain.usecase.bluetooth.get

import androidstudio.tools.missed.manager.adb.command.InternetAdbConnectionCommands
import androidstudio.tools.missed.manager.device.DeviceManager
import kotlinx.coroutines.flow.flow

class GetBluetoothStateUseCaseImpl(private val deviceManager: DeviceManager) : GetBluetoothStateUseCase {

    override suspend fun invoke() = flow<Result<Boolean>> {
        val adbCommand = InternetAdbConnectionCommands.GetBluetoothState()

        deviceManager.executeShellCommand(adbCommand).onSuccess {
            emit(Result.success(it == adbCommand.successResultExpect))
        }.onFailure {
            emit(Result.failure(it))
        }
    }
}
