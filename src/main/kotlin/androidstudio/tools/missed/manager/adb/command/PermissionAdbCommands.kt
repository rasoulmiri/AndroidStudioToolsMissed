package androidstudio.tools.missed.manager.adb.command

class PermissionAdbCommands {

    class AllRuntimePermissionDeviceSupported : AdbCommand(
        command = "pm list permissions -g -d | awk -F: '/permission:/ {print \$2}'",
        isNeedDevice = false,
        isNeedPackageId = false,
        successResult = SuccessResultEnum.NOT_EMPTY
    )

    class AllPermissionInPackageIdInstalled(val packageId: String) : AdbCommand(
        command = "dumpsys package $packageId | grep -E 'permission.*granted' | grep -o '[^\$(printf '\\t') ].*' \n",
        isNeedDevice = false,
        isNeedPackageId = true,
        successResult = SuccessResultEnum.EMPTY_OR_NOT_EMPTY
    )

    class Grant(val packageId: String, val permission: String) : AdbCommand(
        command = "pm grant -g $packageId $permission"
    )

    class Revoke(val packageId: String, val permission: String) : AdbCommand(
        command = "pm revoke $packageId $permission"
    )
}
