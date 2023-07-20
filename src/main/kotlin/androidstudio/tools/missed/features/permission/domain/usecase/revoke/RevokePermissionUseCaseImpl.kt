package androidstudio.tools.missed.features.permission.domain.usecase.revoke

import androidstudio.tools.missed.features.permission.domain.usecase.entity.PermissionStateModel
import androidstudio.tools.missed.manager.adb.command.PermissionAdbCommands
import androidstudio.tools.missed.manager.device.DeviceManager
import androidstudio.tools.missed.manager.resource.ResourceManager
import kotlinx.coroutines.flow.flow

class RevokePermissionUseCaseImpl(
    private val resourceManager: ResourceManager,
    private val deviceManager: DeviceManager
) : RevokePermissionUseCase {

    override suspend fun invoke(permission: PermissionStateModel) = flow<Result<String>> {
        val permissionName = permission.name
        val packageId = deviceManager.packageIdSelectedStateFlow.value.orEmpty()

        if (!permission.isGranted) {
            val alreadyRevokedText = resourceManager.string("revokedDescription", permissionName)
            emit(Result.success(alreadyRevokedText))
            return@flow
        }

        if (!permission.isRuntime) {
            emit(Result.success(resourceManager.string("errorInstallTimePermission")))
            return@flow
        }

        val adbCommands = PermissionAdbCommands.Revoke(packageId, permission.name)
        deviceManager.executeShellCommand(adbCommands).onSuccess {
            if (it.isEmpty()) {
                val successText =
                    resourceManager.string("successRevokeDescription", permissionName, packageId)
                emit(Result.success(successText))
            } else {
                val errorText =
                    resourceManager.string("failedRevokeDescription", permissionName, packageId, it)
                emit(Result.failure(Throwable(errorText)))
            }
        }.onFailure {
            emit(Result.failure(it))
        }
    }
}
