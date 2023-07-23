package androidstudio.tools.missed.manager.adb.command

class StandbyAdbCommands {

    class GetState(packageId: String) : AdbCommand(
        command = "am get-inactive $packageId",
        isNeedDevice = true,
        isNeedPackageId = false,
        successResult = SuccessResultEnum.NOT_EMPTY
    )

    class SetActive(packageId: String) : AdbCommand(
        command = "dumpsys battery unplug & am set-inactive $packageId true",
        isNeedDevice = true,
        isNeedPackageId = false,
        successResult = SuccessResultEnum.EMPTY
    )

    class SetDeactive(packageId: String) : AdbCommand(
        command = "am set-inactive $packageId false & dumpsys battery reset & input keyevent KEYCODE_WAKEUP",
        isNeedDevice = true,
        isNeedPackageId = false,
        successResult = SuccessResultEnum.NOT_EMPTY
    )
}
