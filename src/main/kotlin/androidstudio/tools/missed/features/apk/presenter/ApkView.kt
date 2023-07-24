package androidstudio.tools.missed.features.apk.presenter

import androidstudio.tools.missed.base.CollapsibleGroupView
import androidstudio.tools.missed.features.apk.presenter.model.DownloadApkState
import androidstudio.tools.missed.features.apk.presenter.model.InstallApkState
import androidstudio.tools.missed.manager.notification.NotificationManager
import androidstudio.tools.missed.manager.resource.ResourceManager
import androidstudio.tools.missed.utils.DIMENSION_8
import com.intellij.icons.AllIcons
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.ui.TextBrowseFolderListener
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.AnimatedIcon
import com.intellij.ui.dsl.builder.RightGap
import com.intellij.ui.dsl.builder.TopGap
import com.intellij.ui.dsl.builder.panel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.awt.Desktop
import java.awt.Dimension
import java.io.File
import javax.swing.JButton
import javax.swing.JPanel

class ApkView(
    override val viewModel: ApkViewModel,
    private val notificationManager: NotificationManager,
    private val resourceManager: ResourceManager
) : CollapsibleGroupView<ApkViewModel>(viewModel) {

    private val downloadTextField = TextFieldWithBrowseButton()
    private val installTextField = TextFieldWithBrowseButton()
    private var downloadButton = JButton()
    private var installButton = JButton()

    init {
        setContent(
            title = resourceManager.string("apk"),
            downloadView(),
            installView()
        )
    }

    override fun onExpand() {
        super.onExpand()
        initialObserve()
    }

    private fun downloadView(): JPanel {
        return panel {
            group(resourceManager.string("downloadApkTitle")) {
                row {
                    label(resourceManager.string("apkTitle")).gap(RightGap.SMALL)
                    browserLink(resourceManager.string("help"), "https://github")
                }

                row {
                    label(resourceManager.string("saveLocation"))

                    downloadTextField.apply {
                        preferredSize = Dimension(DIMENSION_8, preferredSize.height)
                        addActionListener {
                            viewModel.setDownloadDirectoryStateFlow(downloadTextField.text)
                        }
                        addBrowseFolderListener(
                            TextBrowseFolderListener(
                                FileChooserDescriptor(
                                    false,
                                    true,
                                    false,
                                    false,
                                    false,
                                    false
                                ).withShowHiddenFiles(true)
                            )
                        )
                    }

                    cell(downloadTextField)
                }

                row {
                    downloadButton = button(resourceManager.string("getAPK")) {
                        viewModel.setDownloadDirectoryStateFlow(downloadTextField.text)
                        viewModel.getApk()
                    }.component
                }
            }.topGap(TopGap.SMALL)
        }
    }

    private fun installView(): JPanel {
        return panel {
            group(resourceManager.string("installApkTitle")) {
                row {
                    label(resourceManager.string("installApplicationFromApkFiles")).gap(RightGap.SMALL)
                    browserLink(resourceManager.string("help"), "https://github")
                }

                row {
                    label(resourceManager.string("apkLocation"))

                    installTextField.apply {
                        preferredSize = Dimension(DIMENSION_8, preferredSize.height)
                        addActionListener {
                            viewModel.setInstallDirectoryStateFlow(installTextField.text)
                        }
                        addBrowseFolderListener(
                            TextBrowseFolderListener(
                                FileChooserDescriptor(
                                    true,
                                    false,
                                    false,
                                    false,
                                    false,
                                    false
                                ).withFileFilter { virtualFile ->
                                    virtualFile.extension == "apk" // filter for APK files only
                                }.withShowHiddenFiles(true)
                            )
                        )
                    }

                    cell(installTextField)
                }

                row {
                    installButton = button(resourceManager.string("installAPK")) {
                        viewModel.setInstallDirectoryStateFlow(installTextField.text)
                        viewModel.installApk()
                    }.component
                }
            }.topGap(TopGap.SMALL)
        }
    }

    private fun initialObserve() {
        viewScope.launch {
            viewModel.messageSharedFlow.collect {
                notificationManager.showBalloon(it)
            }
        }

        viewScope.launch {
            viewModel.installApkNotificationModelSharedFlow.collect { installApkNotification ->
                notificationManager.showBalloonWithButton(installApkNotification.notificationModel, listener = {
                    if (Desktop.isDesktopSupported()) {
                        Desktop.getDesktop().open(File(installApkNotification.saveDirectory))
                    }
                })
            }
        }

        viewScope.launch {
            viewModel.downloadDirectoryStateFlow.collect {
                downloadTextField.text = it
            }
        }

        viewScope.launch {
            viewModel.installDirectoryStateFlow.collect {
                installTextField.text = it
            }
        }

        viewScope.launch {
            viewModel.downloadStateFlow.collectLatest { state ->
                when (state) {
                    DownloadApkState.Idle -> downloadButton.icon = AllIcons.Actions.Download
                    DownloadApkState.Loading -> downloadButton.icon = AnimatedIcon.Default()
                }
            }
        }

        viewScope.launch {
            viewModel.installStateFlow.collectLatest { state ->
                when (state) {
                    InstallApkState.Idle -> installButton.icon = AllIcons.Actions.Upload
                    InstallApkState.Loading -> installButton.icon = AnimatedIcon.Default()
                }
            }
        }
    }
}
