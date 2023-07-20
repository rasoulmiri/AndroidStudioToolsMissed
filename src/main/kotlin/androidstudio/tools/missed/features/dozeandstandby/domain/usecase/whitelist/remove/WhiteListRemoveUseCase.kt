package androidstudio.tools.missed.features.dozeandstandby.domain.usecase.whitelist.remove

import kotlinx.coroutines.flow.Flow

interface WhiteListRemoveUseCase {

    suspend fun invoke(): Flow<Result<Boolean>>
}
