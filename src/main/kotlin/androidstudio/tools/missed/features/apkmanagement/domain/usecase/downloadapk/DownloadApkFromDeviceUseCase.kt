package androidstudio.tools.missed.features.apkmanagement.domain.usecase.downloadapk

import kotlinx.coroutines.flow.Flow

interface DownloadApkFromDeviceUseCase {

    suspend fun invoke(saveDirectory: String, packageId: String): Flow<Result<Boolean>>
}
