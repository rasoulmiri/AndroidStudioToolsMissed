package androidstudio.tools.missed.features.internetconnection.domain.usecase.wifi.get

import kotlinx.coroutines.flow.Flow

interface GetWifiStateUseCase {

    suspend fun invoke(): Flow<Result<Boolean>>
}
