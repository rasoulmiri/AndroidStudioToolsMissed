package androidstudio.tools.missed.features.battery.domain.usecase.powersaving.get

import kotlinx.coroutines.flow.Flow

interface GetPowerSavingUseCase {

    suspend fun invoke(): Flow<Result<Boolean>>
}
