package androidstudio.tools.missed.features.dozeandstandby.domain.usecase.standby.get

import kotlinx.coroutines.flow.Flow

interface StandbyGetUseCase {

    suspend fun invoke(): Flow<Result<Boolean>>
}
