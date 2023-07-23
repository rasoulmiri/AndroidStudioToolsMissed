package androidstudio.tools.missed.features.apk.domain.usecase.installapk

import androidstudio.tools.missed.manager.device.DeviceManager
import kotlinx.coroutines.flow.flow

class InstallApkUseCaseImpl(
    private val deviceManager: DeviceManager
) : InstallApkUseCase {

    override suspend fun invoke(packageFilePath: String) = flow<Result<Boolean>> {
        deviceManager.installApk(packageFilePath = packageFilePath).onSuccess {
            emit(Result.success(true))
        }.onFailure {
            emit(Result.failure(it))
        }
    }
}
