package androidstudio.tools.missed.features.apkmanagement.presenter

import androidstudio.tools.missed.base.ViewModel
import androidstudio.tools.missed.features.apkmanagement.domain.usecase.downloadapk.DownloadApkFromDeviceUseCase
import androidstudio.tools.missed.features.apkmanagement.domain.usecase.installapk.InstallApkUseCase
import androidstudio.tools.missed.features.apkmanagement.presenter.model.DownloadApkState
import androidstudio.tools.missed.features.apkmanagement.presenter.model.InstallApkNotificationModel
import androidstudio.tools.missed.features.apkmanagement.presenter.model.InstallApkState
import androidstudio.tools.missed.manager.device.DeviceManager
import androidstudio.tools.missed.manager.notification.model.BalloonNotificationModel
import androidstudio.tools.missed.manager.resource.ResourceManager
import com.intellij.notification.NotificationType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.swing.filechooser.FileSystemView

class ApkManagementViewModel(
    private val coroutineDispatcher: CoroutineDispatcher,
    private val resourceManager: ResourceManager,
    private val deviceManager: DeviceManager,
    private val downloadApkFromDeviceUseCase: DownloadApkFromDeviceUseCase,
    private val installApkUseCase: InstallApkUseCase
) : ViewModel(coroutineDispatcher) {

    private val _messageSharedFlow = MutableSharedFlow<BalloonNotificationModel>()
    val messageSharedFlow: SharedFlow<BalloonNotificationModel> = _messageSharedFlow.asSharedFlow()

    private val _installApkNotificationModelSharedFlow = MutableSharedFlow<InstallApkNotificationModel>()
    val installApkNotificationModelSharedFlow: SharedFlow<InstallApkNotificationModel> =
        _installApkNotificationModelSharedFlow.asSharedFlow()

    private val _downloadDirectoryStateFlow =
        MutableStateFlow(FileSystemView.getFileSystemView().homeDirectory.absolutePath)
    val downloadDirectoryStateFlow: StateFlow<String> = _downloadDirectoryStateFlow.asStateFlow()

    private val _installDirectoryStateFlow =
        MutableStateFlow(FileSystemView.getFileSystemView().homeDirectory.absolutePath)
    val installDirectoryStateFlow: StateFlow<String> = _installDirectoryStateFlow.asStateFlow()

    private val _downloadStateFlow = MutableStateFlow(DownloadApkState.Idle)
    val downloadStateFlow: StateFlow<DownloadApkState> = _downloadStateFlow.asStateFlow()

    private val _installStateFlow = MutableStateFlow(InstallApkState.Idle)
    val installStateFlow: StateFlow<InstallApkState> = _installStateFlow.asStateFlow()

    fun getApk() = viewModelScope.launch {
        val packageId = deviceManager.packageIdSelectedStateFlow.value.orEmpty()
        _downloadStateFlow.emit(DownloadApkState.Loading)
        downloadApkFromDeviceUseCase.invoke(saveDirectory = downloadDirectoryStateFlow.value, packageId = packageId)
            .collect { result ->
                result.onSuccess {
                    _installApkNotificationModelSharedFlow.emit(
                        InstallApkNotificationModel(
                            BalloonNotificationModel(
                                title = "",
                                content = resourceManager.string("openDirectory"),
                                type = NotificationType.INFORMATION
                            ),
                            downloadDirectoryStateFlow.value
                        )
                    )
                }.onFailure {
                    _messageSharedFlow.emit(
                        BalloonNotificationModel(
                            title = "",
                            content = resourceManager.string("generateApkFailed", "\n${it.message}"),
                            type = NotificationType.ERROR
                        )
                    )
                }
                _downloadStateFlow.emit(DownloadApkState.Idle)
            }
    }

    fun installApk() = viewModelScope.launch {
        _installStateFlow.emit(InstallApkState.Loading)

        installApkUseCase.invoke(packageFilePath = installDirectoryStateFlow.value).collect { result ->
            result.onSuccess {
                _messageSharedFlow.emit(
                    BalloonNotificationModel(
                        content = resourceManager.string("installSuccessful"),
                        type = NotificationType.INFORMATION
                    )
                )
            }.onFailure {
                if (it.message == "Local path is a directory.") {
                    _messageSharedFlow.emit(
                        BalloonNotificationModel(
                            content = resourceManager.string(
                                "installFailed",
                                "select an apk file from your computer for installing"
                            ),
                            type = NotificationType.ERROR
                        )
                    )
                } else {
                    _messageSharedFlow.emit(
                        BalloonNotificationModel(
                            content = resourceManager.string("installFailed", "\n${it.message}"),
                            type = NotificationType.ERROR
                        )
                    )
                }
            }
            _installStateFlow.emit(InstallApkState.Idle)
        }
    }

    fun setInstallDirectoryStateFlow(directory: String) = viewModelScope.launch {
        _installDirectoryStateFlow.emit(directory)
    }

    fun setDownloadDirectoryStateFlow(directory: String) = viewModelScope.launch {
        _downloadDirectoryStateFlow.emit(directory)
    }
}
