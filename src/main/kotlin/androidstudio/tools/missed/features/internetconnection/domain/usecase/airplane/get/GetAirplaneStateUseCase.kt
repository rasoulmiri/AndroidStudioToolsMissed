package androidstudio.tools.missed.features.internetconnection.domain.usecase.airplane.get

import kotlinx.coroutines.flow.Flow

interface GetAirplaneStateUseCase {

    suspend fun invoke(): Flow<Result<Boolean>>
}
