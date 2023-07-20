package androidstudio.tools.missed.features.apk.domain.usecase.installapk

import kotlinx.coroutines.flow.Flow

interface InstallApkUseCase {

    suspend fun invoke(packageFilePath: String): Flow<Result<Boolean>>
}
