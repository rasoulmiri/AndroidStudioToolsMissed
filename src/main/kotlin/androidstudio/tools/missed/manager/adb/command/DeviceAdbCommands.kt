package androidstudio.tools.missed.manager.adb.command

class DeviceAdbCommands {
    class Brand : AdbCommand(
        command = "getprop | grep \"ro.product.brand\"",
        isNeedDevice = false,
        isNeedPackageId = false,
        successResult = SuccessResultEnum.NOT_EMPTY
    )

    class Model : AdbCommand(
        command = "getprop | grep \"ro.product.model\"",
        isNeedDevice = false,
        isNeedPackageId = false,
        successResult = SuccessResultEnum.NOT_EMPTY
    )

    class AllPackageIdsInstalled : AdbCommand(
        command = "cmd package list packages | cut -f 2 -d \":\"",
        isNeedDevice = true,
        isNeedPackageId = false,
        successResult = SuccessResultEnum.NOT_EMPTY
    )

    class AllPackageIdsUserInstalled : AdbCommand(
        command = "cmd package list packages -3 | cut -f 2 -d \":\"",
        isNeedDevice = true,
        isNeedPackageId = false,
        successResult = SuccessResultEnum.NOT_EMPTY
    )
}
