package androidstudio.tools.missed.features.deviceAndpackageid.domain.usecase

import androidstudio.tools.missed.manager.adb.command.DeviceAdbCommands
import androidstudio.tools.missed.manager.device.DeviceManager
import kotlinx.coroutines.flow.flow
import org.jetbrains.annotations.VisibleForTesting

class GetPackageIdsInstalledInDeviceUseCaseImpl(private val deviceManager: DeviceManager) :
    GetPackageIdsInstalledInDeviceUseCase {

    override suspend fun invoke(isSelectedShowAllPackageIds: Boolean) = flow<Result<ArrayList<String>>> {
        val adbCommand = if (isSelectedShowAllPackageIds) {
            DeviceAdbCommands.AllPackageIdsInstalled()
        } else {
            DeviceAdbCommands.AllPackageIdsUserInstalled()
        }

        deviceManager.executeShellCommand(adbCommand).onSuccess { result ->
            emit(Result.success(convertStringPackageIdsToArrayListOfPackageIds(result)))
        }.onFailure {
            emit(Result.failure(it))
        }
    }

    @VisibleForTesting
    fun convertStringPackageIdsToArrayListOfPackageIds(lines: String): ArrayList<String> {
        return lines.split("\n")
            .map { it.trim() }
            .sortedBy { it }
            .filter {
                it.isNotEmpty()
            }.toMutableList() as ArrayList<String>
    }
}
