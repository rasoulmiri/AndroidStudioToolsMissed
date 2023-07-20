package androidstudio.tools.missed.features.dozeandstandby.domain.usecase.whitelist.get

import androidstudio.tools.missed.manager.adb.command.WhiteListAdbCommands
import androidstudio.tools.missed.manager.device.DeviceManager
import kotlinx.coroutines.flow.flow

class WhiteListGetUseCaseImpl(private val deviceManager: DeviceManager) : WhiteListGetUseCase {

    override suspend fun invoke() = flow<Result<ArrayList<String>>> {
        deviceManager.executeShellCommand(WhiteListAdbCommands.FetchAll()).onSuccess { result ->
            emit(Result.success(parseWhiteList(result)))
        }.onFailure {
            emit(Result.failure(it))
        }
    }

    private fun parseWhiteList(successResult: String): ArrayList<String> =
        successResult.split("\n").reversed() as ArrayList<String>
}
