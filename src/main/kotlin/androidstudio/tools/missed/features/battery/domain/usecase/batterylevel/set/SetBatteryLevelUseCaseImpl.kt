package androidstudio.tools.missed.features.battery.domain.usecase.batterylevel.set

import androidstudio.tools.missed.manager.adb.command.BatteryAdbCommands
import androidstudio.tools.missed.manager.device.DeviceManager
import kotlinx.coroutines.flow.flow

class SetBatteryLevelUseCaseImpl(private val deviceManager: DeviceManager) : SetBatteryLevelUseCase {

    override suspend fun invoke(batteryLevel: Int) = flow<Result<Boolean>> {
        val adbCommands = BatteryAdbCommands.SetLevel(batteryLevel)
        deviceManager.executeShellCommand(adbCommands).onSuccess {
            emit(Result.success(true))
        }.onFailure {
            emit(Result.failure(it))
        }
    }
}
