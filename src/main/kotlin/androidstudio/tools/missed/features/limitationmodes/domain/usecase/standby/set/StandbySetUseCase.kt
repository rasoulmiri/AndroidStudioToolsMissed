package androidstudio.tools.missed.features.limitationmodes.domain.usecase.standby.set

import kotlinx.coroutines.flow.Flow

interface StandbySetUseCase {

    suspend fun invoke(isActive: Boolean): Flow<Result<Boolean>>
}
