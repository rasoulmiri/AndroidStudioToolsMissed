package androidstudio.tools.missed.manager.adb.command

sealed class AdbCommand(
    val command: String,
    val isNeedDevice: Boolean = true,
    val isNeedPackageId: Boolean = true,
    val successResult: SuccessResultEnum = SuccessResultEnum.EMPTY,
    val successResultExpect: String? = null
) {

    class GetFoldersApplication(private val packageId: String) : AdbCommand(
        command = "run-as $packageId ls -l /data/data/$packageId\n",
        successResult = SuccessResultEnum.NOT_EMPTY
    )

    class SU : AdbCommand(
        command = "su",
        successResult = SuccessResultEnum.NOT_EMPTY
    )

    class ClearApplicationData(private val packageId: String) : AdbCommand(
        command = "pm clear $packageId",
        successResult = SuccessResultEnum.NOT_EMPTY,
        successResultExpect = "Success"
    )

    class GetPathBaseApkInDevice(packageId: String) : AdbCommand(
        command = "pm path $packageId",
        successResult = SuccessResultEnum.NOT_EMPTY
    )

    class CopyFile(val pathSource: String, val pathDestination: String) : AdbCommand(
        command = "cp $pathSource $pathDestination",
        successResult = SuccessResultEnum.EMPTY
    )

    class InputText(val message: String) : AdbCommand(
        command = "input text $message",
        isNeedDevice = true,
        isNeedPackageId = false
    )

    class InputEvent(val event: String) : AdbCommand(
        command = "input keyevent $event",
        isNeedDevice = true,
        isNeedPackageId = false
    )

    class ClearEditText : AdbCommand(
        command = "input keycombination 113 29 && input keyevent 67",
        isNeedDevice = true,
        isNeedPackageId = false
    )
}

enum class SuccessResultEnum {
    EMPTY, NOT_EMPTY, EMPTY_OR_NOT_EMPTY
}
