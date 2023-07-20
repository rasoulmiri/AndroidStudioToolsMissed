package androidstudio.tools.missed.features.battery.domain.usecase.resetbatteryconfig

import androidstudio.tools.missed.manager.adb.command.BatteryAdbCommands
import androidstudio.tools.missed.manager.device.DeviceManager
import kotlinx.coroutines.flow.flow

class ResetBatteryConfigUseCaseImpl(private val deviceManager: DeviceManager) : ResetBatteryConfigUseCase {

    override suspend fun invoke() = flow<Result<Boolean>> {
        val adbCommands = BatteryAdbCommands.ResetSetting()
        deviceManager.executeShellCommand(adbCommands).onSuccess {
            emit(Result.success(true))
        }.onFailure {
            emit(Result.failure(it))
        }
    }
}
