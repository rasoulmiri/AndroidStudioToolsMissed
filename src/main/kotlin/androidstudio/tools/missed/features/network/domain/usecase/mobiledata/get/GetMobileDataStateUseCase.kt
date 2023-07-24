package androidstudio.tools.missed.features.network.domain.usecase.mobiledata.get

import kotlinx.coroutines.flow.Flow

interface GetMobileDataStateUseCase {

    suspend fun invoke(): Flow<Result<Boolean>>
}
