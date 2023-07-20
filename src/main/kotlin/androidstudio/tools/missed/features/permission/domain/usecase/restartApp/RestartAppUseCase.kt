package androidstudio.tools.missed.features.permission.domain.usecase.restartApp

import kotlinx.coroutines.flow.Flow

interface RestartAppUseCase {

    suspend fun invoke(): Flow<Result<String>>
}
