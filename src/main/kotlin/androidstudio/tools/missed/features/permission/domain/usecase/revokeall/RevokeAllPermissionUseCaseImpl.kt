package androidstudio.tools.missed.features.permission.domain.usecase.revokeall

import androidstudio.tools.missed.features.permission.domain.usecase.entity.PermissionStateModel
import androidstudio.tools.missed.features.permission.domain.usecase.fetchall.FetchAllPermissionsUseCase
import androidstudio.tools.missed.features.permission.domain.usecase.revoke.RevokePermissionUseCase
import androidstudio.tools.missed.manager.device.DeviceManager
import androidstudio.tools.missed.manager.resource.ResourceManager
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow

class RevokeAllPermissionUseCaseImpl(
    private val resourceManager: ResourceManager,
    private val deviceManager: DeviceManager,
    private val fetchAllPermissionsUseCase: FetchAllPermissionsUseCase,
    private val revokePermissionUseCase: RevokePermissionUseCase
) : RevokeAllPermissionUseCase {

    override suspend fun invoke() = flow<Result<String>> {
        fetchAllPermissionsUseCase.invoke().collect { result ->
            result.onSuccess { permissions ->
                val allRevoked = revokeAllPermissions(permissions)
                if (allRevoked) {
                    val packageId = deviceManager.packageIdSelectedStateFlow.value.orEmpty()
                    emit(Result.success(resourceManager.string("permissionsRevokeAllSuccessTitle", packageId)))
                } else {
                    emit(Result.failure(Throwable(resourceManager.string("permissionsRevokeAllErrorTitle"))))
                }
            }.onFailure {
                emit(Result.failure(it))
            }
        }
    }

    private suspend fun revokeAllPermissions(permissions: List<PermissionStateModel>): Boolean {
        var allRevoked = true
        permissions.forEach { permission ->
            revokePermissionUseCase.invoke(permission).collect { revokeResult ->
                revokeResult.onFailure {
                    allRevoked = false
                }
            }
        }
        return allRevoked
    }
}
