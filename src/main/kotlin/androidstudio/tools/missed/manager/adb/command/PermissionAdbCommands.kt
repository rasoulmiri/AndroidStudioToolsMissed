package androidstudio.tools.missed.manager.adb.command

class PermissionAdbCommands {

    class AllRuntimePermissionDeviceSupported : AdbCommand(
        command = "pm list permissions -g -d | grep -i \"permission:\" | sed 's/permission://' ",
        isNeedDevice = false,
        isNeedPackageId = false,
        successResult = SuccessResultEnum.NOT_EMPTY
    )

    class AllPermissionInPackageIdInstalled(val packageId: String) : AdbCommand(
        command = "dumpsys package $packageId | sed -n '/requested permissions:/,/mSkippingApks:/p'",
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
