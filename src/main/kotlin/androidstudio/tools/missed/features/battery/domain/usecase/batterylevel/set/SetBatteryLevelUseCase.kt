package androidstudio.tools.missed.features.battery.domain.usecase.batterylevel.set

import kotlinx.coroutines.flow.Flow

interface SetBatteryLevelUseCase {

    suspend fun invoke(batteryLevel: Int): Flow<Result<Boolean>>
}
