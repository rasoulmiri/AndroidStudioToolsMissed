package androidstudio.tools.missed.features.battery.domain.usecase.powersaving.set

import kotlinx.coroutines.flow.Flow

interface SetPowerSavingUseCase {

    suspend fun invoke(isActive: Boolean): Flow<Result<Boolean>>
}
