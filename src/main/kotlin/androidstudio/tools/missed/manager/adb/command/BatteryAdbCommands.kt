package androidstudio.tools.missed.manager.adb.command

class BatteryAdbCommands {

    class SetLevel(val level: Int) : AdbCommand(
        command = "dumpsys battery set level $level",
        isNeedDevice = true,
        isNeedPackageId = false,
        successResult = SuccessResultEnum.EMPTY
    )

    class GetBatteryLevel : AdbCommand(
        command = "dumpsys battery",
        isNeedDevice = true,
        isNeedPackageId = false,
        successResult = SuccessResultEnum.NOT_EMPTY
    )

    class PowerSavingModeGetState : AdbCommand(
        command = "settings get global low_power",
        isNeedDevice = true,
        isNeedPackageId = false,
        successResult = SuccessResultEnum.NOT_EMPTY
    )

    class PowerSavingModeSetOff : AdbCommand(
        command = "settings put global low_power 0",
        isNeedDevice = true,
        isNeedPackageId = false,
        successResult = SuccessResultEnum.EMPTY
    )

    class PowerSavingModeSetOn : AdbCommand(
        command = "dumpsys battery unplug & settings put global low_power 1",
        isNeedDevice = true,
        isNeedPackageId = false,
        successResult = SuccessResultEnum.EMPTY
    )

    class ChargerSetConnect : AdbCommand(
        command = "dumpsys battery set ac 1 & dumpsys battery set usb 1 & dumpsys battery set wireless 1",
        isNeedDevice = true,
        isNeedPackageId = false,
        successResult = SuccessResultEnum.EMPTY
    )

    class ChargerSetDisconnect : AdbCommand(
        command = "dumpsys battery set ac 0 & dumpsys battery set usb 0 & dumpsys battery set wireless 0",
        isNeedDevice = true,
        isNeedPackageId = false,
        successResult = SuccessResultEnum.EMPTY
    )

    class ResetSetting : AdbCommand(
        command = "dumpsys battery reset & settings put global low_power 0",
        isNeedDevice = true,
        isNeedPackageId = false,
        successResult = SuccessResultEnum.EMPTY
    )
}
