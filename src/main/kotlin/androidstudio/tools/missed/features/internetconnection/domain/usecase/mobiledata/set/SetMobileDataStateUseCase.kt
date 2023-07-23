package androidstudio.tools.missed.features.internetconnection.domain.usecase.mobiledata.set

import kotlinx.coroutines.flow.Flow

interface SetMobileDataStateUseCase {

    suspend fun invoke(state: Boolean): Flow<Result<Boolean>>
}
