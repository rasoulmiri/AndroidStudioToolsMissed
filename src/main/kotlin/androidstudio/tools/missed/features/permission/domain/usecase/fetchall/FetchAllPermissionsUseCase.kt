package androidstudio.tools.missed.features.permission.domain.usecase.fetchall

import androidstudio.tools.missed.features.permission.domain.usecase.entity.PermissionStateModel
import kotlinx.coroutines.flow.Flow

interface FetchAllPermissionsUseCase {

    suspend fun invoke(): Flow<Result<ArrayList<PermissionStateModel>>>
}
