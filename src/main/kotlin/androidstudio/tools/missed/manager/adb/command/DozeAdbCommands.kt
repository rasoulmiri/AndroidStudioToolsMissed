package androidstudio.tools.missed.manager.adb.command

class DozeAdbCommands {
    class GetState : AdbCommand(
        command = "dumpsys deviceidle step",
        isNeedDevice = true,
        isNeedPackageId = false,
        successResult = SuccessResultEnum.NOT_EMPTY
    )

    class SetActive : AdbCommand(
        command = "dumpsys battery unplug & dumpsys deviceidle force-idle",
        isNeedDevice = true,
        isNeedPackageId = false,
        successResult = SuccessResultEnum.NOT_EMPTY
    )

    class SetDeactive : AdbCommand(
        command = "dumpsys deviceidle unforce & dumpsys battery reset & input keyevent KEYCODE_WAKEUP",
        isNeedDevice = true,
        isNeedPackageId = false,
        successResult = SuccessResultEnum.NOT_EMPTY
    )
}
