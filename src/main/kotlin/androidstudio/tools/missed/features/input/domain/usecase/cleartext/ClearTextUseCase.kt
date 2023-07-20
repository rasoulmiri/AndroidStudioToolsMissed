package androidstudio.tools.missed.features.input.domain.usecase.cleartext

import kotlinx.coroutines.flow.Flow

interface ClearTextUseCase {

    suspend fun invoke(): Flow<Result<Unit>>
}
