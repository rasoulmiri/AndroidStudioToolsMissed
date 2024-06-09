package androidstudio.tools.missed.manager.adb

import androidstudio.tools.missed.features.customcommand.model.CustomCommand
import androidstudio.tools.missed.manager.adb.command.AdbCommand
import androidstudio.tools.missed.manager.adb.command.DeviceAdbCommands
import androidstudio.tools.missed.manager.adb.command.SuccessResultEnum
import androidstudio.tools.missed.manager.adb.logger.AdbLogger
import androidstudio.tools.missed.manager.device.model.Device
import androidstudio.tools.missed.manager.resource.ResourceManager
import com.intellij.openapi.project.ProjectManager
import org.gradle.internal.impldep.org.jetbrains.annotations.VisibleForTesting
import org.jetbrains.android.sdk.AndroidSdkUtils
import org.jetbrains.kotlin.tools.projectWizard.core.asSuccess
import org.jetbrains.kotlin.util.capitalizeDecapitalize.toUpperCaseAsciiOnly
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.Result.Companion.failure
import kotlin.Result.Companion.success
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class AdbManagerImpl(
    private val resourceManager: ResourceManager,
    private val adbLogger: AdbLogger
) : AdbManager {

    private var adbPath: String? = null
    private var adbIsConnect: Boolean = false

    @Suppress("TooGenericExceptionCaught")
    override suspend fun initialAdb(): Result<Boolean> {
        val project = ProjectManager.getInstance().openProjects.getOrNull(0)
        adbPath = AndroidSdkUtils.findAdb(project).adbPath?.absolutePath
        return if (adbPath == null || adbPath?.isEmpty() == true) {
            adbIsConnect = false
            adbLogger.println(resourceManager.string("adbConnectionIssue"))
            failure(Throwable(resourceManager.string("adbConnectionIssue")))
        } else {
            adbIsConnect = true
            success(true)
        }
    }

    override suspend fun getDevices(): Result<List<Device>> {
        return if (adbIsConnect) {
            val result = runtimeExec("$adbPath devices")
            when {
                result.isSuccess -> {
                    val lines = result.getOrNull()?.trim()?.lines()?.drop(1) ?: emptyList()
                    // sample lines
                    // ce0516056bcc220e05	device
                    // emulator-5554	device
                    if (lines.isNotEmpty()) {
                        success(prepareDevicesModels(lines))
                    } else {
                        failure(Throwable(resourceManager.string("connectAnAndroidDevice")))
                    }
                }

                else -> failure(
                    Throwable(
                        result.exceptionOrNull()?.message ?: resourceManager.string("connectAnAndroidDevice")
                    )
                )
            }
        } else {
            failure(Throwable(resourceManager.string("adbIsNotInitialize")))
        }
    }

    private suspend fun prepareDevicesModels(lines: List<String>): List<Device> {
        // sample lines
        // ce0516056bcc220e05	device
        // emulator-5554	device
        val devices = mutableListOf<Device>()
        lines.forEach { line ->
            val id = line.split("\t")[0]
            val device = Device(name = "None", id = id)
            device.name = getNameDevice(device)
            devices.add(device)
        }
        return devices
    }

    private suspend fun getNameDevice(device: Device): String {
        if (device.id.startsWith("emulator")) {
            val name =
                executeADBCommand(device, DeviceAdbCommands.EmulatorName()).asSuccess().value.getOrNull()?.lines()
                    ?.getOrNull(0)
                    ?.replace("_", " ")
            return "$name (${device.id})"
        } else {
            val brand = executeADBShellCommand(device, DeviceAdbCommands.Brand())
                .asSuccess().value.getOrNull().toString()
                .replace("[ro.product.brand]: [", "")
                .replace("]", "").toUpperCaseAsciiOnly()

            val model = executeADBShellCommand(device, DeviceAdbCommands.Model())
                .asSuccess().value.getOrNull().toString()
                .replace("[ro.product.model]: [", "")
                .replace("]", "")
            return "$brand - $model (${device.id})"
        }
    }

    override suspend fun executeADBCommand(device: Device?, command: AdbCommand): Result<String> {
        return suspendCoroutine { continuation ->
            runtimeExec("$adbPath -s ${device?.id} ${command.command}").onSuccess { result ->
                val message = result.trim()
                if (command.successResult == SuccessResultEnum.EMPTY && message.isEmpty()) {
                    continuation.resume(success(message))
                } else if (command.successResult == SuccessResultEnum.NOT_EMPTY && message.isNotEmpty()) {
                    continuation.resume(success(message))
                } else if (command.successResult == SuccessResultEnum.EMPTY_OR_NOT_EMPTY) {
                    continuation.resume(success(message))
                } else {
                    continuation.resume(failure(Throwable(message)))
                }
            }.onFailure {
                continuation.resume(failure(Throwable(it.message ?: resourceManager.string("errorGeneral"))))
            }
        }
    }

    override suspend fun executeADBShellCommand(device: Device?, command: AdbCommand): Result<String> {
        return suspendCoroutine { continuation ->
            runtimeExec("$adbPath -s ${device?.id} shell ${command.command}").onSuccess { result ->
                val message = result.trim()
                if (command.successResult == SuccessResultEnum.EMPTY && message.isEmpty()) {
                    continuation.resume(success(message))
                } else if (command.successResult == SuccessResultEnum.NOT_EMPTY && message.isNotEmpty()) {
                    continuation.resume(success(message))
                } else if (command.successResult == SuccessResultEnum.EMPTY_OR_NOT_EMPTY) {
                    continuation.resume(success(message))
                } else {
                    continuation.resume(failure(Throwable(message)))
                }
            }.onFailure {
                continuation.resume(failure(Throwable(it.message ?: resourceManager.string("errorGeneral"))))
            }
        }
    }

    override suspend fun executeCustomCommand(
        device: Device?,
        packageId: String?,
        command: CustomCommand
    ): Result<String> {
        return suspendCoroutine { continuation ->

            val newCommand =
                command.command
                    ?.replace("\$ADB", "$adbPath -s ${device?.id}")
                    ?.replace("\$APP_ID", "$packageId")
                    ?: ""

            println("$newCommand\n==========================\n")
            runtimeExec(newCommand).onSuccess { result ->
                val message = result.trim()
                println(message)
                continuation.resume(success("$newCommand\n==========================\n$message"))
            }.onFailure {
                continuation.resume(failure(Throwable(it.message ?: resourceManager.string("errorGeneral"))))
            }
        }
    }

    @VisibleForTesting
    @Suppress("TooGenericExceptionCaught")
    fun runtimeExec(command: String): Result<String> {
        try {
            // Run the command
            val process = Runtime.getRuntime().exec(command)
            val bufferedReader = BufferedReader(InputStreamReader(process.inputStream))

            // Grab the result
            var line: String?
            val result = StringBuilder()
            do {
                line = bufferedReader.readLine()
                line?.let {
                    result.appendLine(line)
                }
            } while (line != null)

            // Check if the process exited with an error
            val exitCode = process.waitFor()
            if (exitCode != 0) {
                val errorReader = BufferedReader(InputStreamReader(process.errorStream))
                val error = StringBuilder()
                var errorLine: String?
                errorLine = errorReader.readLine()
                while (errorLine != null) {
                    error.append(errorLine + "\n")
                    errorLine = errorReader.readLine()
                }
                return failure(Throwable(error.toString()))
            }
            return success(result.toString())
        } catch (e: Exception) {
            return failure(Throwable(e.message ?: resourceManager.string("errorGeneral")))
        }
    }
}
