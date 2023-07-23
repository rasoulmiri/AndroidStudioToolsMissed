package androidstudio.tools.missed.features.dozeandstandby.domain.usecase.whitelist.get

import kotlinx.coroutines.flow.Flow

interface WhiteListGetUseCase {

    suspend fun invoke(): Flow<Result<ArrayList<String>>>
}
