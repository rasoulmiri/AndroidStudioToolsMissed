package androidstudio.tools.missed.features.battery.domain.usecase.batterylevel.get

import androidstudio.tools.missed.manager.adb.command.BatteryAdbCommands
import androidstudio.tools.missed.manager.device.DeviceManager
import kotlinx.coroutines.flow.flow
import org.jetbrains.annotations.VisibleForTesting

class GetBatteryLevelUseCaseImpl(private val deviceManager: DeviceManager) : GetBatteryLevelUseCase {

    override suspend fun invoke() = flow<Result<Int>> {
        val adbCommands = BatteryAdbCommands.GetBatteryLevel()
        deviceManager.executeShellCommand(adbCommands).onSuccess {
            val batteryLevel = parseBatteryLevelResult(it)
            emit(Result.success(batteryLevel))
        }.onFailure {
            emit(Result.failure(it))
        }
    }

    @VisibleForTesting
    fun parseBatteryLevelResult(result: String): Int {
        val lines = result.split("\n")

        for (line in lines) {
            if (line.contains("level:")) {
                return line.replace("level: ", "").trim().toIntOrNull() ?: 0
            }
        }
        return 0
    }
}
