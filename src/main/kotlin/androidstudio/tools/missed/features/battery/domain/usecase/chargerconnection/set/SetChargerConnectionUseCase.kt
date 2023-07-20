package androidstudio.tools.missed.features.battery.domain.usecase.chargerconnection.set

import kotlinx.coroutines.flow.Flow

interface SetChargerConnectionUseCase {

    suspend fun invoke(isConnect: Boolean): Flow<Result<Boolean>>
}
