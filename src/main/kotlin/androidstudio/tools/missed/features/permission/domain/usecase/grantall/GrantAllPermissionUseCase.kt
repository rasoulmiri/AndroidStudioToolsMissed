package androidstudio.tools.missed.features.permission.domain.usecase.grantall

import kotlinx.coroutines.flow.Flow

interface GrantAllPermissionUseCase {

    suspend fun invoke(): Flow<Result<String>>
}
