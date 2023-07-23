package androidstudio.tools.missed.features.internetconnection.domain.usecase.airplane.set

import kotlinx.coroutines.flow.Flow

interface SetAirplaneStateUseCase {

    suspend fun invoke(state: Boolean): Flow<Result<Boolean>>
}
