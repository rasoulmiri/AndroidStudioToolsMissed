package androidstudio.tools.missed.features.permission.domain.usecase.revoke

import androidstudio.tools.missed.features.permission.domain.usecase.entity.PermissionStateModel
import kotlinx.coroutines.flow.Flow

interface RevokePermissionUseCase {

    suspend fun invoke(permission: PermissionStateModel): Flow<Result<String>>
}
