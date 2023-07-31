package androidstudio.tools.missed.features.permission.presenter

import androidstudio.tools.missed.base.CollapsibleGroupView
import androidstudio.tools.missed.features.permission.domain.usecase.entity.PermissionStateModel
import androidstudio.tools.missed.features.permission.presenter.model.PermissionComboBoxModel
import androidstudio.tools.missed.features.permission.presenter.model.PermissionComboBoxRenderer
import androidstudio.tools.missed.features.permission.presenter.model.PermissionUiState
import androidstudio.tools.missed.manager.notification.NotificationManager
import androidstudio.tools.missed.manager.resource.ResourceManager
import androidstudio.tools.missed.utils.DIMENSION_6
import androidstudio.tools.missed.utils.DIMENSION_8
import androidstudio.tools.missed.utils.FONT_COMMENT_SIZE
import com.intellij.icons.AllIcons
import com.intellij.ui.AnimatedIcon
import com.intellij.ui.JBColor
import com.intellij.ui.dsl.builder.RightGap
import com.intellij.ui.dsl.builder.RowLayout
import com.intellij.ui.dsl.builder.panel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.awt.Dimension
import java.awt.Font
import java.awt.event.ItemEvent
import javax.swing.JButton
import javax.swing.JCheckBox
import javax.swing.JComboBox
import javax.swing.JLabel
import javax.swing.JPanel

class PermissionView(
    override val viewModel: PermissionViewModel,
    private val notificationManager: NotificationManager,
    private val resourceManager: ResourceManager
) : CollapsibleGroupView<PermissionViewModel>(viewModel) {

    private lateinit var restartApplicationGrantCheckBox: JCheckBox
    private lateinit var restartApplicationRevokeCheckBox: JCheckBox

    private lateinit var permissionsComboBox: JComboBox<PermissionStateModel?>
    private var permissionComboBoxRenderer = PermissionComboBoxRenderer(resourceManager.string("selectAPermission"))
    private val permissionsComboBoxModel = PermissionComboBoxModel()
    private lateinit var grantButton: JButton
    private lateinit var grantAllButton: JButton
    private lateinit var revokeButton: JButton
    private lateinit var revokeAllButton: JButton
    private lateinit var commentLabel: JLabel

    init {
        setContent(
            title = resourceManager.string("permissionsTitle"),
            singlePermissionView(),
            allPermissionsView()
        )
    }

    override fun onExpand() {
        super.onExpand()
        initialObserve()
        viewModel.addListenerForPackageIdChange()
        viewModel.fetchAllPermissions()
    }

    private fun singlePermissionView(): JPanel {
        return panel {
            group(resourceManager.string("permissionsSingleTitle")) {
                row {
                    label(resourceManager.string("permissionsSingleDescription")).gap(RightGap.SMALL)
                    browserLink(resourceManager.string("help"), "https://github.com/rasoulmiri/AndroidStudioToolsMissed/wiki/Permissions-Tools")
                }

                row {
                    // Permissions ComboBox
                    permissionsComboBox = JComboBox(permissionsComboBoxModel)
                    permissionsComboBox.minimumSize = Dimension(DIMENSION_8, permissionsComboBox.minimumSize.height)
                    permissionsComboBox.renderer = permissionComboBoxRenderer
                    permissionsComboBox.addItemListener {
                        if (it.stateChange == ItemEvent.SELECTED) {
                            viewModel.setPermissionSelected(permissionsComboBoxModel.selection)
                        }
                    }
                    cell(permissionsComboBox)
                }
                row {
                    // Hint text
                    commentLabel = JLabel("")
                    commentLabel.font = Font(commentLabel.font.name, Font.TYPE1_FONT, FONT_COMMENT_SIZE)
                    commentLabel.foreground = JBColor.GRAY
                    commentLabel.isVisible = false
                    cell(commentLabel)
                }

                row {
                    // JustRuntime checkbox
                    checkBox(resourceManager.string("runtime")).component.apply {
                        toolTipText = resourceManager.string("JustShowRuntimePermissions")
                        isSelected = true
                        addItemListener {
                            viewModel.setShowRuntimePermission(it.stateChange == ItemEvent.SELECTED)
                        }
                    }

                    // Grant
                    grantButton = button(resourceManager.string("grant")) {
                        viewModel.grantSelectedPermission()
                    }.component.apply {
                        icon = AllIcons.Actions.Commit
                        isEnabled = false
                    }

                    // Revoke
                    revokeButton = button(resourceManager.string("revoke")) {
                        viewModel.revokeSelectedPermission()
                    }.component.apply {
                        icon = AllIcons.Actions.Cancel
                        isEnabled = false
                    }
                }
            }
        }
    }

    private fun allPermissionsView(): JPanel {
        return panel {
            group(resourceManager.string("permissionsAllTitle")) {
                row {
                    label(resourceManager.string("permissionsAllDescription")).gap(RightGap.SMALL)
                    browserLink(resourceManager.string("help"), "https://github.com/rasoulmiri/AndroidStudioToolsMissed/wiki/Permissions-Tools")
                }
                row {
                    grantAllButton = button(resourceManager.string("grantAllPermissions")) {
                        viewScope.launch {
                            viewModel.grantAllPermission()
                            if (restartApplicationGrantCheckBox.isSelected) {
                                viewModel.restartApplication()
                            }
                        }
                    }.component.apply {
                        preferredSize = Dimension(DIMENSION_6, preferredSize.height)
                    }

                    restartApplicationGrantCheckBox = checkBox(resourceManager.string("restartApplication")).component
                    restartApplicationGrantCheckBox.toolTipText = resourceManager.string("restartApplicationHint")
                }.layout(RowLayout.PARENT_GRID)

                row {
                    revokeAllButton = button(resourceManager.string("revokeAllPermissions")) {
                        viewScope.launch {
                            viewModel.revokeAllPermission()
                            if (restartApplicationRevokeCheckBox.isSelected) {
                                viewModel.restartApplication()
                            }
                        }
                    }.component.apply {
                        preferredSize = Dimension(DIMENSION_6, preferredSize.height)
                    }
                    restartApplicationRevokeCheckBox = checkBox(resourceManager.string("restartApplication")).component
                    restartApplicationRevokeCheckBox.toolTipText = resourceManager.string("revokeAllPermissionsHint")
                }.layout(RowLayout.PARENT_GRID)
            }
        }
    }

    private fun initialObserve() {
        viewScope.launch {
            viewModel.messageSharedFlow.collect {
                notificationManager.showBalloon(it)
            }
        }

        viewScope.launch {
            viewModel.permissionStateFlow.collectLatest { permissions ->
                updatePermissionsComboBoxModel(permissions)
            }
        }

        viewScope.launch {
            viewModel.grantButtonEnabled.collectLatest { isEnabled ->
                grantButton.isEnabled = isEnabled
            }
        }

        viewScope.launch {
            viewModel.revokeButtonEnabled.collectLatest { isEnabled ->
                revokeButton.isEnabled = isEnabled
            }
        }

        viewScope.launch {
            viewModel.commentText.collectLatest { text ->
                commentLabel.isVisible = text?.let {
                    commentLabel.text = text
                    true
                } ?: run {
                    false
                }
                commentLabel.updateUI()
            }
        }

        viewScope.launch {
            viewModel.grantAllStateFlow.collectLatest { state ->
                when (state) {
                    PermissionUiState.Idle -> grantAllButton.icon = AllIcons.Actions.Commit
                    PermissionUiState.Loading -> grantAllButton.icon = AnimatedIcon.Default()
                }
            }
        }

        viewScope.launch {
            viewModel.revokeAllStateFlow.collectLatest { state ->
                when (state) {
                    PermissionUiState.Idle -> revokeAllButton.icon = AllIcons.Actions.Cancel
                    PermissionUiState.Loading -> revokeAllButton.icon = AnimatedIcon.Default()
                }
            }
        }
    }

    private fun updatePermissionsComboBoxModel(permissions: List<PermissionStateModel>) {
        permissionsComboBoxModel.items.clear()

        if (permissions.isEmpty()) {
            permissionComboBoxRenderer.setPlaceHolder(resourceManager.string("applicationDoesntHavePermission"))
            permissionsComboBox.isEnabled = false
        } else {
            permissionComboBoxRenderer.setPlaceHolder(resourceManager.string("selectAPermission"))
            permissionsComboBox.isEnabled = true
            var permissionSelected = viewModel.getPermissionSelected()
            permissions.forEach { permission ->

                permissionsComboBoxModel.items.add(permission)

                if (permission.name == permissionSelected?.name) {
                    permissionSelected = permission
                }
            }
            permissionsComboBoxModel.selection = permissionSelected
            viewModel.setPermissionSelected(permissionSelected)
        }

        permissionsComboBox.updateUI()
    }
}
