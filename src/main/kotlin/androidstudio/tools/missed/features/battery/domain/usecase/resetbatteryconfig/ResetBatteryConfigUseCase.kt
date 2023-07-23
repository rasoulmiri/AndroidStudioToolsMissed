package androidstudio.tools.missed.features.battery.domain.usecase.resetbatteryconfig

import kotlinx.coroutines.flow.Flow

interface ResetBatteryConfigUseCase {

    suspend fun invoke(): Flow<Result<Boolean>>
}
