package androidstudio.tools.missed.features.battery.domain.usecase.chargerconnection.get

import androidstudio.tools.missed.manager.adb.command.BatteryAdbCommands
import androidstudio.tools.missed.manager.device.DeviceManager
import kotlinx.coroutines.flow.flow

class GetChargerConnectionUseCaseImpl(private val deviceManager: DeviceManager) : GetChargerConnectionUseCase {

    override suspend fun invoke() = flow<Result<Boolean>> {
        val adbCommands = BatteryAdbCommands.GetBatteryLevel()
        deviceManager.executeShellCommand(adbCommands).onSuccess {
            val chargerConnection = parseBatteryLevelResult(it)
            emit(Result.success(chargerConnection))
        }.onFailure {
            emit(Result.failure(it))
        }
    }

    private fun parseBatteryLevelResult(result: String): Boolean {
        val lines = result.split("\n")

        val acPowered = lines.any { line -> line.contains("AC powered:") && line.contains("true") }
        val usbPowered = lines.any { line -> line.contains("USB powered:") && line.contains("true") }
        val wirelessPowered = lines.any { line -> line.contains("Wireless powered:") && line.contains("true") }

        return acPowered || usbPowered || wirelessPowered
    }
}
