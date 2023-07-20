package androidstudio.tools.missed.features.internetconnection.domain.usecase.bluetooth.get

import kotlinx.coroutines.flow.Flow

interface GetBluetoothStateUseCase {

    suspend fun invoke(): Flow<Result<Boolean>>
}
