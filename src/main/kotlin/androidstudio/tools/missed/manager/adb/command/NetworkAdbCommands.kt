package androidstudio.tools.missed.manager.adb.command

class NetworkAdbCommands {
    class GetAirplaneModeState : AdbCommand(
        command = "settings get global airplane_mode_on",
        isNeedDevice = true,
        isNeedPackageId = false,
        successResult = SuccessResultEnum.NOT_EMPTY,
        successResultExpect = "1"
    )

    class SetAirplaneModeState(isOn: Boolean) : AdbCommand(
        command = "settings put global airplane_mode_on " + "${
        if (isOn) {
            "1"
        } else {
            "0"
        }
        } ${
        if (isOn) {
            "&& cmd -w wifi set-wifi-enabled disabled"
        } else {
            ""
        }
        }",
        isNeedDevice = true,
        isNeedPackageId = false,
        successResult = SuccessResultEnum.EMPTY
    )

    class GetMobileDataState : AdbCommand(
        command = "settings get global mobile_data",
        isNeedDevice = true,
        isNeedPackageId = false,
        successResult = SuccessResultEnum.NOT_EMPTY,
        successResultExpect = "1"
    )

    class SetMobileDataState(isOn: Boolean) : AdbCommand(
        command = "svc data ${
        if (isOn) {
            "enable"
        } else {
            "disable"
        }
        }",
        isNeedDevice = true,
        isNeedPackageId = false,
        successResult = SuccessResultEnum.EMPTY
    )

    class GetWifiState : AdbCommand(
        command = "settings get global wifi_on",
        isNeedDevice = true,
        isNeedPackageId = false,
        successResult = SuccessResultEnum.NOT_EMPTY,
        successResultExpect = "1"
    )

    class SetWifiState(isOn: Boolean) : AdbCommand(
        command = "cmd -w wifi set-wifi-enabled ${
        if (isOn) {
            "enabled"
        } else {
            "disabled"
        }
        }",
        isNeedDevice = true,
        isNeedPackageId = false,
        successResult = SuccessResultEnum.EMPTY
    )

    class GetBluetoothState : AdbCommand(
        command = "settings get global bluetooth_on",
        isNeedDevice = true,
        isNeedPackageId = false,
        successResult = SuccessResultEnum.NOT_EMPTY,
        successResultExpect = "1"
    )

    class SetBluetoothState(isOn: Boolean) : AdbCommand(
        command = "settings put global bluetooth_on " + "${
        if (isOn) {
            "1"
        } else {
            "0"
        }
        } && svc bluetooth ${
        if (isOn) {
            "enable"
        } else {
            "disable"
        }
        } && am start -a android.bluetooth.adapter.action.${
        if (isOn) {
            "REQUEST_ENABLE"
        } else {
            "REQUEST_DISABLE"
        }
        } && am broadcast -a android.intent.action.BLUETOOTH_ENABLE --ez state $isOn",
        isNeedDevice = true,
        isNeedPackageId = false,
        successResult = SuccessResultEnum.NOT_EMPTY
    )
}
