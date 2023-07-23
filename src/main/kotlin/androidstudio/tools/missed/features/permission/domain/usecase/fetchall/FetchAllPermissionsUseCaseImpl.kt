package androidstudio.tools.missed.features.permission.domain.usecase.fetchall

import androidstudio.tools.missed.features.permission.domain.usecase.entity.PermissionStateModel
import androidstudio.tools.missed.manager.adb.command.PermissionAdbCommands
import androidstudio.tools.missed.manager.device.DeviceManager
import androidstudio.tools.missed.manager.resource.ResourceManager
import kotlinx.coroutines.flow.flow

class FetchAllPermissionsUseCaseImpl(
    private val resourceManager: ResourceManager,
    private val deviceManager: DeviceManager
) : FetchAllPermissionsUseCase {

    override suspend fun invoke() = flow<Result<ArrayList<PermissionStateModel>>> {
        val packageId = deviceManager.packageIdSelectedStateFlow.value.orEmpty()

        packageId.let {
            val allRuntimePermissionDeviceSupported = getAllRuntimePermissionDeviceSupported()

            deviceManager.executeShellCommand(
                PermissionAdbCommands.AllPermissionInPackageIdInstalled(packageId)
            ).onSuccess { result ->
                val permission = processPermissionResult(result, allRuntimePermissionDeviceSupported)
                emit(Result.success(permission))
            }.onFailure { error ->
                emit(Result.failure(error))
            }
        }
    }

    private suspend fun getAllRuntimePermissionDeviceSupported(): Result<ArrayList<String>> {
        val allRuntimePermissionDeviceSupported = deviceManager.executeShellCommand(
            PermissionAdbCommands.AllRuntimePermissionDeviceSupported()
        )

        return if (allRuntimePermissionDeviceSupported.isSuccess) {
            val array = ArrayList<String>()
            allRuntimePermissionDeviceSupported.getOrNull()?.split("\n")?.let { array.addAll(it) }
            Result.success(array)
        } else {
            Result.failure(
                allRuntimePermissionDeviceSupported.exceptionOrNull()
                    ?: Exception(resourceManager.string("failedToGetPermissions"))
            )
        }
    }

    @Suppress("ExpressionBodySyntax")
    private fun processPermissionResult(
        result: String,
        allRuntimePermissionInDevice: Result<ArrayList<String>>
    ): ArrayList<PermissionStateModel> {
        // Sample result
        // android.permission.WAKE_LOCK: granted=false
        // android.permission.CAMERA: granted=true, flags=[ USER_SET|USER_SENSITIVE_WHEN_GRANTED|USER_SENSITIVE_WHEN_DENIED]

        val permissions = ArrayList<PermissionStateModel>()

        result.split("\n").reversed().forEach { line ->
            val permissionArray = line.split(":")

            if (permissionArray.getOrNull(0)?.isNotEmpty() == true &&
                permissionArray.getOrNull(1)?.isNotEmpty() == true &&
                permissionArray.getOrNull(1)?.contains("granted=") == true
            ) {
                val permissionName = permissionArray.getOrNull(0)
                var isRunTimePermission = false

                allRuntimePermissionInDevice.onSuccess { runtimePermissions ->
                    if (permissionName in runtimePermissions) {
                        isRunTimePermission = true
                    }

                    permissions.add(
                        PermissionStateModel(
                            name = permissionName ?: "-",
                            isGranted = permissionArray.getOrNull(1)?.contains("granted=true") ?: false,
                            isRuntime = isRunTimePermission
                        )
                    )
                }.onFailure {
                    throw Throwable(it.message)
                }
            }
        }
        return permissions
    }
}
