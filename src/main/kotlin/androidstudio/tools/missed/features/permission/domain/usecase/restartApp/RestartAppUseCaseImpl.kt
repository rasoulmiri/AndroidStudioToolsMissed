package androidstudio.tools.missed.features.permission.domain.usecase.restartApp

import androidstudio.tools.missed.manager.adb.command.ApplicationAdbCommands
import androidstudio.tools.missed.manager.device.DeviceManager
import kotlinx.coroutines.flow.flow

class RestartAppUseCaseImpl(val deviceManager: DeviceManager) : RestartAppUseCase {

    override suspend fun invoke() = flow<Result<String>> {
        val packageId = deviceManager.packageIdSelectedStateFlow.value.orEmpty()
        val adbCommandClose = ApplicationAdbCommands.Close(packageId)
        val adbCommandOpen = ApplicationAdbCommands.Open(packageId)
        deviceManager.executeShellCommand(
            adbCommandClose
        ).onSuccess {
            deviceManager.executeShellCommand(adbCommandOpen).onSuccess {
                emit(Result.success(it))
            }.onFailure {
                emit(Result.failure(it))
            }
        }.onFailure {
            emit(Result.failure(it))
        }
    }
}
