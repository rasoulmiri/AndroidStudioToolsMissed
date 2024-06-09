package androidstudio.tools.missed.manager.adb.command

class ApplicationAdbCommands {
    class Close(val packageId: String) : AdbCommand(command = "am force-stop $packageId")
    class Open(val packageId: String) : AdbCommand(
        command = "am start \$(cmd package resolve-activity --brief $packageId | tail -n 1)",
        successResult = SuccessResultEnum.NOT_EMPTY
    )

    class Install(private val packageFilePath: String) : AdbCommand(
        command = "install $packageFilePath",
        successResult = SuccessResultEnum.NOT_EMPTY
    )
}
