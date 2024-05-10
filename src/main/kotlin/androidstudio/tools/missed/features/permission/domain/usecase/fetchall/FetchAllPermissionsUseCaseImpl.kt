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
        // android.permission.ACCESS_NETWORK_STATE
        // android.permission.ACCESS_WIFI_STATE
        // android.permission.ACCESS_LOCATION
        // android.permission.ACCESS_COARSE_LOCATION
        // android.permission.ACCESS_FINE_LOCATION
        // android.permission.CAMERA
        // ...
        // install permissions:
        // com.izettle.android.debug.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION: granted=true
        // com.google.android.finsky.permission.BIND_GET_INSTALL_REFERRER_SERVICE: granted=true
        // android.permission.NFC: granted=true
        // android.permission.FOREGROUND_SERVICE: granted=true
        // android.permission.REQUEST_COMPANION_USE_DATA_IN_BACKGROUND: granted=true
        // android.permission.RECEIVE_BOOT_COMPLETED: granted=true
        // android.permission.INTERNET: granted=true
        // se.eelde.toggles.provider_permission: granted=true
        // android.permission.ACCESS_NETWORK_STATE: granted=true
        // ...
        // runtime permissions:
        // android.permission.POST_NOTIFICATIONS: granted=true, flags=[ USER_SENSITIVE_WHEN_GRANTED|USER_SENSITIVE_WHEN_DENIED]
        // android.permission.ACCESS_FINE_LOCATION: granted=true, flags=[ USER_SENSITIVE_WHEN_GRANTED|USER_SENSITIVE_WHEN_DENIED]
        // android.permission.BLUETOOTH_CONNECT: granted=true, flags=[ USER_SENSITIVE_WHEN_GRANTED|USER_SENSITIVE_WHEN_DENIED]
        // android.permission.READ_EXTERNAL_STORAGE: granted=true, flags=[ USER_SENSITIVE_WHEN_GRANTED|USER_SENSITIVE_WHEN_DENIED|RESTRICTION_INSTALLER_EXEMPT]
        // android.permission.ACCESS_COARSE_LOCATION: granted=true, flags=[ USER_SENSITIVE_WHEN_GRANTED|USER_SENSITIVE_WHEN_DENIED]
        // android.permission.CAMERA: granted=true, flags=[ USER_SENSITIVE_WHEN_GRANTED|USER_SENSITIVE_WHEN_DENIED]
        // android.permission.WRITE_EXTERNAL_STORAGE: granted=true, flags=[ USER_SENSITIVE_WHEN_GRANTED|USER_SENSITIVE_WHEN_DENIED|RESTRICTION_INSTALLER_EXEMPT]
        // android.permission.BLUETOOTH_SCAN: granted=true, flags=[ USER_SENSITIVE_WHEN_GRANTED|USER_SENSITIVE_WHEN_DENIED]

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
