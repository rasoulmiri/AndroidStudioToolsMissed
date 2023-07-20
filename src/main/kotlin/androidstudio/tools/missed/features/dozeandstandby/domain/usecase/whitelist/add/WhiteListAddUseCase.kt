package androidstudio.tools.missed.features.dozeandstandby.domain.usecase.whitelist.add

import kotlinx.coroutines.flow.Flow

interface WhiteListAddUseCase {

    suspend fun invoke(): Flow<Result<Boolean>>
}
