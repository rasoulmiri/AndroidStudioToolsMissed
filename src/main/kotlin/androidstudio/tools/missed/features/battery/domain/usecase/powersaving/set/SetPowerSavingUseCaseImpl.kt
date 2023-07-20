package androidstudio.tools.missed.features.battery.domain.usecase.powersaving.set

import androidstudio.tools.missed.manager.adb.command.BatteryAdbCommands
import androidstudio.tools.missed.manager.device.DeviceManager
import kotlinx.coroutines.flow.flow

class SetPowerSavingUseCaseImpl(private val deviceManager: DeviceManager) : SetPowerSavingUseCase {

    override suspend fun invoke(isActive: Boolean) = flow<Result<Boolean>> {
        val adbCommand = if (isActive) {
            BatteryAdbCommands.PowerSavingModeSetOn()
        } else {
            BatteryAdbCommands.PowerSavingModeSetOff()
        }
        deviceManager.executeShellCommand(adbCommand).onSuccess {
            emit(Result.success(true))
        }.onFailure {
            emit(Result.failure(it))
        }
    }
}
