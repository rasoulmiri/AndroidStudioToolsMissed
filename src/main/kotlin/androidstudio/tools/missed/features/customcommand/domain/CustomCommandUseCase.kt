package androidstudio.tools.missed.features.customcommand.domain

import androidstudio.tools.missed.features.customcommand.model.CustomCommand
import kotlinx.coroutines.flow.Flow

interface CustomCommandUseCase {

    suspend fun invoke(customCommand: CustomCommand): Flow<Result<String>>
}
