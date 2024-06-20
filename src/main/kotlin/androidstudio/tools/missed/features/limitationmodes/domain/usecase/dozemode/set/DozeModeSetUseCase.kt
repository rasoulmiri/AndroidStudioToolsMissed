package androidstudio.tools.missed.features.limitationmodes.domain.usecase.dozemode.set

import kotlinx.coroutines.flow.Flow

interface DozeModeSetUseCase {

    suspend fun invoke(isActive: Boolean): Flow<Result<Boolean>>
}
