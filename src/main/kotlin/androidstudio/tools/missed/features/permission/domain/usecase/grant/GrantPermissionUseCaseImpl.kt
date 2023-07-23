package androidstudio.tools.missed.features.permission.domain.usecase.grant

import androidstudio.tools.missed.features.permission.domain.usecase.entity.PermissionStateModel
import androidstudio.tools.missed.manager.adb.command.PermissionAdbCommands
import androidstudio.tools.missed.manager.device.DeviceManager
import androidstudio.tools.missed.manager.resource.ResourceManager
import kotlinx.coroutines.flow.flow

class GrantPermissionUseCaseImpl(
    private val resourceManager: ResourceManager,
    private val deviceManager: DeviceManager
) : GrantPermissionUseCase {

    override suspend fun invoke(permission: PermissionStateModel) = flow<Result<String>> {
        val permissionName = permission.name
        val packageId = deviceManager.packageIdSelectedStateFlow.value.orEmpty()

        if (permission.isGranted) {
            val alreadyGrantedText = resourceManager.string("grantedDescription", permissionName)
            emit(Result.success(alreadyGrantedText))
            return@flow
        }

        if (!permission.isRuntime) {
            emit(Result.success(resourceManager.string("errorInstallTimePermission")))
            return@flow
        }

        deviceManager.executeShellCommand(
            PermissionAdbCommands.Grant(packageId, permission.name)
        ).onSuccess {
            if (it.isEmpty()) {
                val successText = resourceManager.string("successGrantDescription", permissionName, packageId)
                emit(Result.success(successText))
            } else {
                val errorText =
                    resourceManager.string("failedGrantDescription", permissionName, packageId, it)
                emit(Result.failure(Throwable(errorText)))
            }
        }.onFailure {
            emit(Result.failure(it))
        }
    }
}
