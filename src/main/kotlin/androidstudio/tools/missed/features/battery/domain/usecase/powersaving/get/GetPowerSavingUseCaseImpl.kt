package androidstudio.tools.missed.features.battery.domain.usecase.powersaving.get

import androidstudio.tools.missed.manager.adb.command.BatteryAdbCommands
import androidstudio.tools.missed.manager.device.DeviceManager
import kotlinx.coroutines.flow.flow

class GetPowerSavingUseCaseImpl(private val deviceManager: DeviceManager) : GetPowerSavingUseCase {

    override suspend fun invoke() = flow<Result<Boolean>> {
        val adbCommands = BatteryAdbCommands.PowerSavingModeGetState()
        deviceManager.executeShellCommand(adbCommands).onSuccess {
            emit(Result.success(it == "1"))
        }.onFailure {
            emit(Result.failure(it))
        }
    }
}
