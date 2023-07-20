package androidstudio.tools.missed.features.input.domain.usecase.sendtext

import kotlinx.coroutines.flow.Flow

interface SendTextUseCase {

    suspend fun invoke(text: String): Flow<Result<Unit>>
}
