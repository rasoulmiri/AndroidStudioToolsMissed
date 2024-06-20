package androidstudio.tools.missed.features.limitationmodes.domain.usecase.standby.get

import kotlinx.coroutines.flow.Flow

interface StandbyGetUseCase {

    suspend fun invoke(): Flow<Result<Boolean>>
}
