package androidstudio.tools.missed.features.permission.domain.usecase.revokeall

import kotlinx.coroutines.flow.Flow

interface RevokeAllPermissionUseCase {

    suspend fun invoke(): Flow<Result<String>>
}
