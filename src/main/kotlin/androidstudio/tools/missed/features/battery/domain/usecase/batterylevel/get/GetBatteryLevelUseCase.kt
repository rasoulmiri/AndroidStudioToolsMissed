package androidstudio.tools.missed.features.battery.domain.usecase.batterylevel.get

import kotlinx.coroutines.flow.Flow

interface GetBatteryLevelUseCase {

    suspend fun invoke(): Flow<Result<Int>>
}
