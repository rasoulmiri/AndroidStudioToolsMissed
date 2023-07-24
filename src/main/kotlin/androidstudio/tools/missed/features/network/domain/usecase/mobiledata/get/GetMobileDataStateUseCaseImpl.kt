package androidstudio.tools.missed.features.network.domain.usecase.mobiledata.get

import androidstudio.tools.missed.manager.adb.command.NetworkAdbCommands
import androidstudio.tools.missed.manager.device.DeviceManager
import kotlinx.coroutines.flow.flow

class GetMobileDataStateUseCaseImpl(private val deviceManager: DeviceManager) : GetMobileDataStateUseCase {

    override suspend fun invoke() = flow<Result<Boolean>> {
        val adbCommand = NetworkAdbCommands.GetMobileDataState()

        deviceManager.executeShellCommand(adbCommand).onSuccess {
            emit(Result.success(it == adbCommand.successResultExpect))
        }.onFailure {
            emit(Result.failure(it))
        }
    }
}
