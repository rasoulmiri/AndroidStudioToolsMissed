package androidstudio.tools.missed.features.input.domain.usecase.sendtext

import androidstudio.tools.missed.manager.adb.command.AdbCommand
import androidstudio.tools.missed.manager.device.DeviceManager
import kotlinx.coroutines.flow.flow
import org.jetbrains.annotations.VisibleForTesting

class SendTextUseCaseImpl(
    private val deviceManager: DeviceManager
) : SendTextUseCase {

    override suspend fun invoke(text: String) = flow<Result<Unit>> {
        deviceManager.executeShellCommand(AdbCommand.InputText(message = addDoubleBackSlashForEspecialCharacters(text)))
            .onSuccess {
                emit(Result.success(Unit))
            }.onFailure {
                emit(Result.failure(it))
            }
    }

    @VisibleForTesting
    fun addDoubleBackSlashForEspecialCharacters(text: String): String {
        return text.replace("|", "\\|")
            .replace("\"", "\\\"")
            .replace("\'", "\\\'")
            .replace("<", "\\<")
            .replace(">", "\\>")
            .replace(";", "\\;")
            .replace("?", "\\?")
            .replace("`", "\\`")
            .replace("&", "\\&")
            .replace("*", "\\*")
            .replace("(", "\\(")
            .replace(")", "\\)")
            .replace("~", "\\~")
            .replace(" ", "\\ ")
    }
}
