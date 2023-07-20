package androidstudio.tools.missed.features.internetconnection.domain.usecase.mobiledata.get

import androidstudio.tools.missed.manager.adb.command.InternetAdbConnectionCommands
import androidstudio.tools.missed.manager.device.DeviceManager
import kotlinx.coroutines.flow.flow

class GetMobileDataStateUseCaseImpl(private val deviceManager: DeviceManager) : GetMobileDataStateUseCase {

    override suspend fun invoke() = flow<Result<Boolean>> {
        val adbCommand = InternetAdbConnectionCommands.GetMobileDataState()

        deviceManager.executeShellCommand(adbCommand).onSuccess {
            emit(Result.success(it == adbCommand.successResultExpect))
        }.onFailure {
            emit(Result.failure(it))
        }
    }
}
