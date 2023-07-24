package androidstudio.tools.missed.features.network.domain.usecase.wifi.set

import kotlinx.coroutines.flow.Flow

interface SetWifiStateUseCase {

    suspend fun invoke(state: Boolean): Flow<Result<Boolean>>
}
