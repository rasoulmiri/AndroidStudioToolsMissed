package androidstudio.tools.missed.manager.adb.command

class FileAdbCommands {
    class GetLisOfFoldersAndFiles(packageId: String) : AdbCommand(
        command = "run-as $packageId ls -l -R -p /data/data/$packageId",
        isNeedDevice = true,
        isNeedPackageId = true,
        successResult = SuccessResultEnum.NOT_EMPTY
    )

    class PullFile(
        remoteFilepath: String,
        localFilePath: String
    ) : AdbCommand(
        command = "pull $remoteFilepath $localFilePath",
        isNeedDevice = true,
        isNeedPackageId = false,
        successResult = SuccessResultEnum.NOT_EMPTY
    )
}
