package androidstudio.tools.missed.features.input.domain.usecase.sendevent

import kotlinx.coroutines.flow.Flow

interface SendEventUseCase {

    suspend fun invoke(event: String): Flow<Result<Unit>>
}
