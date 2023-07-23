package androidstudio.tools.missed.features.battery.domain.usecase.chargerconnection.set

import androidstudio.tools.missed.manager.adb.command.BatteryAdbCommands
import androidstudio.tools.missed.manager.device.DeviceManager
import kotlinx.coroutines.flow.flow

class SetChargerConnectionUseCaseImpl(private val deviceManager: DeviceManager) : SetChargerConnectionUseCase {

    override suspend fun invoke(isConnect: Boolean) = flow<Result<Boolean>> {
        val adbCommand = if (isConnect) {
            BatteryAdbCommands.ChargerSetConnect()
        } else {
            BatteryAdbCommands.ChargerSetDisconnect()
        }
        deviceManager.executeShellCommand(adbCommand).onSuccess {
            emit(Result.success(true))
        }.onFailure {
            emit(Result.failure(it))
        }
    }
}
