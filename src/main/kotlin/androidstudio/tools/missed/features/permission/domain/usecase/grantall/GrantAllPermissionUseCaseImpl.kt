package androidstudio.tools.missed.features.permission.domain.usecase.grantall

import androidstudio.tools.missed.features.permission.domain.usecase.entity.PermissionStateModel
import androidstudio.tools.missed.features.permission.domain.usecase.fetchall.FetchAllPermissionsUseCase
import androidstudio.tools.missed.features.permission.domain.usecase.grant.GrantPermissionUseCase
import androidstudio.tools.missed.manager.device.DeviceManager
import androidstudio.tools.missed.manager.resource.ResourceManager
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow

class GrantAllPermissionUseCaseImpl(
    private val resourceManager: ResourceManager,
    private val deviceManager: DeviceManager,
    private val fetchAllPermissionsUseCase: FetchAllPermissionsUseCase,
    private val grantPermissionUseCase: GrantPermissionUseCase
) : GrantAllPermissionUseCase {

    override suspend fun invoke() = flow<Result<String>> {
        val packageId = deviceManager.packageIdSelectedStateFlow.value.orEmpty()
        fetchAllPermissionsUseCase.invoke().collect { result ->
            result.onSuccess { permissions ->
                val allGranted = grantAllPermissions(permissions)
                if (allGranted) {
                    emit(Result.success(resourceManager.string("permissionsGrantAllSuccessTitle", packageId)))
                } else {
                    emit(Result.failure(Throwable(resourceManager.string("permissionsGrantAllErrorTitle"))))
                }
            }.onFailure {
                emit(Result.failure(it))
            }
        }
    }

    private suspend fun grantAllPermissions(permissions: List<PermissionStateModel>): Boolean {
        var allGranted = true
        permissions.forEach { permission ->
            grantPermissionUseCase.invoke(permission).collect { grantResult ->
                grantResult.onFailure {
                    allGranted = false
                }
            }
        }
        return allGranted
    }
}
