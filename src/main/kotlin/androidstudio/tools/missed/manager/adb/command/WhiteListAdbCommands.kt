package androidstudio.tools.missed.manager.adb.command

class WhiteListAdbCommands {

    class FetchAll : AdbCommand(
        command = "dumpsys deviceidle whitelist",
        isNeedDevice = true,
        isNeedPackageId = false,
        successResult = SuccessResultEnum.NOT_EMPTY
    )

    class Add(packageId: String) : AdbCommand(
        command = "dumpsys deviceidle whitelist +$packageId",
        isNeedDevice = true,
        isNeedPackageId = false,
        successResult = SuccessResultEnum.NOT_EMPTY
    )

    class Remove(packageId: String) : AdbCommand(
        command = "dumpsys deviceidle whitelist -$packageId",
        isNeedDevice = true,
        isNeedPackageId = false,
        successResult = SuccessResultEnum.NOT_EMPTY
    )
}
