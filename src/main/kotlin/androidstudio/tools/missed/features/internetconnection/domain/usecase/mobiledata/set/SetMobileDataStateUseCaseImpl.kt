package androidstudio.tools.missed.features.internetconnection.domain.usecase.mobiledata.set

import androidstudio.tools.missed.manager.adb.command.InternetAdbConnectionCommands
import androidstudio.tools.missed.manager.device.DeviceManager
import kotlinx.coroutines.flow.flow

class SetMobileDataStateUseCaseImpl(private val deviceManager: DeviceManager) : SetMobileDataStateUseCase {

    override suspend fun invoke(state: Boolean) = flow<Result<Boolean>> {
        val adbCommand = InternetAdbConnectionCommands.SetMobileDataState(isOn = state)

        deviceManager.executeShellCommand(adbCommand).onSuccess {
            emit(Result.success(true))
        }.onFailure {
            emit(Result.failure(it))
        }
    }
}
