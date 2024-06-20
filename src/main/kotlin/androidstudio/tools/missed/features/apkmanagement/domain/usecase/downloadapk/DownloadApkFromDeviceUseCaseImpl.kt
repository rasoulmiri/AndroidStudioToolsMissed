package androidstudio.tools.missed.features.apkmanagement.domain.usecase.downloadapk

import androidstudio.tools.missed.manager.adb.command.AdbCommand
import androidstudio.tools.missed.manager.device.DeviceManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DownloadApkFromDeviceUseCaseImpl(
    private val deviceManager: DeviceManager
) : DownloadApkFromDeviceUseCase {

    @Suppress("TooGenericExceptionCaught")
    override suspend fun invoke(saveDirectory: String, packageId: String): Flow<Result<Boolean>> = flow {
        try {
            val apkPathOnDevice = getApkPathOnDevice(packageId)
            copyApkToDeviceStorage(packageId, apkPathOnDevice)
            pullApkToDevice(saveDirectory, packageId)
            emit(Result.success(true))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    private suspend fun getApkPathOnDevice(packageId: String): String {
        val adbCommandGetPathBaseApkInDevice = AdbCommand.GetPathBaseApkInDevice(packageId = packageId)
        var path = ""
        deviceManager.executeShellCommand(adbCommandGetPathBaseApkInDevice).onSuccess {
            path = it.replace("package:", "")
        }.onFailure { error ->
            throw error
        }
        return path
    }

    private suspend fun copyApkToDeviceStorage(packageId: String, apkPathOnDevice: String) {
        val adbCommandCopyFile = AdbCommand.CopyFile(
            pathSource = apkPathOnDevice,
            pathDestination = "/sdcard/$packageId.apk"
        )
        deviceManager.executeShellCommand(adbCommandCopyFile).onFailure {
            throw it
        }
    }

    private suspend fun pullApkToDevice(saveDirectory: String, packageId: String) {
        deviceManager.pullFile(
            "/sdcard/$packageId.apk",
            "$saveDirectory/$packageId.apk"
        ).onFailure {
            throw it
        }
    }
}
