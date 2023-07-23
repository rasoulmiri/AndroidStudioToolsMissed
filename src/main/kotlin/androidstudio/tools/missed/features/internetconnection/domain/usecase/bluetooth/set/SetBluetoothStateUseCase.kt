package androidstudio.tools.missed.features.internetconnection.domain.usecase.bluetooth.set

import kotlinx.coroutines.flow.Flow

interface SetBluetoothStateUseCase {

    suspend fun invoke(state: Boolean): Flow<Result<Boolean>>
}
