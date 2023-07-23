package androidstudio.tools.missed.features.permission.domain.usecase.grant

import androidstudio.tools.missed.features.permission.domain.usecase.entity.PermissionStateModel
import kotlinx.coroutines.flow.Flow

interface GrantPermissionUseCase {

    suspend fun invoke(permission: PermissionStateModel): Flow<Result<String>>
}
