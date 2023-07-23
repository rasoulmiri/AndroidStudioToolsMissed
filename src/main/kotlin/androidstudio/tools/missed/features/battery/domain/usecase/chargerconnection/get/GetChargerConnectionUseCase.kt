package androidstudio.tools.missed.features.battery.domain.usecase.chargerconnection.get

import kotlinx.coroutines.flow.Flow

interface GetChargerConnectionUseCase {

    suspend fun invoke(): Flow<Result<Boolean>>
}
