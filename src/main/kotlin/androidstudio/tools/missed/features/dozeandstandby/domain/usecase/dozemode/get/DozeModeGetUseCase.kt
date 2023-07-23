package androidstudio.tools.missed.features.dozeandstandby.domain.usecase.dozemode.get

import androidstudio.tools.missed.features.dozeandstandby.domain.usecase.dozemode.get.entity.DozeModeGetStateModel
import kotlinx.coroutines.flow.Flow

interface DozeModeGetUseCase {

    suspend fun invoke(): Flow<Result<DozeModeGetStateModel>>
}
