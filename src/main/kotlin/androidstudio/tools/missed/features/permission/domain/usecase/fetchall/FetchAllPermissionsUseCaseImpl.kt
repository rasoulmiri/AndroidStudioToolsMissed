package androidstudio.tools.missed.features.permission.domain.usecase.fetchall

import androidstudio.tools.missed.features.permission.domain.usecase.entity.PermissionStateModel
import androidstudio.tools.missed.manager.adb.command.PermissionAdbCommands
import androidstudio.tools.missed.manager.device.DeviceManager
import kotlinx.coroutines.flow.flow

class FetchAllPermissionsUseCaseImpl(
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

    private suspend fun getAllRuntimePermissionDeviceSupported(): ArrayList<String> {
        val allRuntimePermissionDeviceSupported = deviceManager.executeShellCommand(
            PermissionAdbCommands.AllRuntimePermissionDeviceSupported()
        )
        val array = ArrayList<String>()
        if (allRuntimePermissionDeviceSupported.isSuccess) {
            allRuntimePermissionDeviceSupported
                .getOrNull()?.split("\n")?.let { permissionsText ->
                    permissionsText.forEach {
                        array.add(it.trim())
                    }
                }
        }
        return array
    }

    @Suppress("ExpressionBodySyntax")
    private fun processPermissionResult(
        result: String,
        allRuntimePermissionDeviceSupported: ArrayList<String>
    ): ArrayList<PermissionStateModel> {
        // Sample result
        // requested permissions:
        // android.permission.FOREGROUND_SERVICE
        // android.permission.INTERNET
        // android.permission.CAMERA
        // ...
        // ...
        // install permissions:
        // android.permission.NFC: granted=true
        // android.permission.INTERNET: granted=true
        // android.permission.BLUETOOTH_ADMIN: granted=true
        // gids=[3002, 3003, 3001]
        // runtime permissions:
        // android.permission.ACCESS_FINE_LOCATION: granted=true
        // android.permission.CAMERA: granted=true
        // disabledComponents:
        // ...
        // enabledComponents:
        // ...
        // mSkippingApks:

        val requestedPermissions = result
            .substringAfter("requested permissions:")
            .substringBefore("install permissions:")
            .trim()
            .split("\n")
            .map { line ->
                val isRuntime = allRuntimePermissionDeviceSupported.any { it.trim() == line.trim() }
                PermissionStateModel(name = line.trim(), isGranted = false, isRuntime = isRuntime)
            }
            .toCollection(ArrayList())

        val installPermissions = result
            .substringAfter("install permissions:")
            .substringBefore("runtime permissions:")
            .split("\n")
            .filter { it.contains("granted=") }
            .map { line ->
                val (permissionName, isGranted) = line.split(":")
                PermissionStateModel(
                    name = permissionName.trim() ?: "-",
                    isGranted = isGranted.contains("granted=true"),
                    isRuntime = false
                )
            }
            .toCollection(ArrayList())

        val runtimePermissions = result
            .substringAfter("runtime permissions:")
            .substringBefore("mSkippingApks:")
            .split("\n")
            .filter { it.contains("granted=") }
            .map { line ->
                val (permissionName, isGranted) = line.split(":")
                PermissionStateModel(
                    name = permissionName.trim() ?: "-",
                    isGranted = isGranted.contains("granted=true"),
                    isRuntime = true
                )
            }
            .toCollection(ArrayList())

        requestedPermissions.forEachIndexed { index, permission ->
            installPermissions.find { it.name == permission.name }?.let {
                requestedPermissions[index] = it
            }
            runtimePermissions.find { it.name == permission.name }?.let {
                requestedPermissions[index] = it
            }
        }

        return requestedPermissions
    }
}
