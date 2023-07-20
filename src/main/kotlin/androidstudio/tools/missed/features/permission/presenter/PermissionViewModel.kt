package androidstudio.tools.missed.features.permission.presenter

import androidstudio.tools.missed.base.ViewModel
import androidstudio.tools.missed.features.permission.domain.usecase.entity.PermissionStateModel
import androidstudio.tools.missed.features.permission.domain.usecase.fetchall.FetchAllPermissionsUseCase
import androidstudio.tools.missed.features.permission.domain.usecase.grant.GrantPermissionUseCase
import androidstudio.tools.missed.features.permission.domain.usecase.grantall.GrantAllPermissionUseCase
import androidstudio.tools.missed.features.permission.domain.usecase.restartApp.RestartAppUseCase
import androidstudio.tools.missed.features.permission.domain.usecase.revoke.RevokePermissionUseCase
import androidstudio.tools.missed.features.permission.domain.usecase.revokeall.RevokeAllPermissionUseCase
import androidstudio.tools.missed.features.permission.presenter.model.PermissionUiState
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

class PermissionViewModel(
    private val coroutineDispatcher: CoroutineDispatcher,
    private val resourceManager: ResourceManager,
    private val deviceManager: DeviceManager,
    private val restartAppUseCase: RestartAppUseCase,
    private val fetchAllPermissionsUseCase: FetchAllPermissionsUseCase,
    private val grantPermissionUseCase: GrantPermissionUseCase,
    private val revokePermissionUseCase: RevokePermissionUseCase,
    private val grantAllPermissionUseCase: GrantAllPermissionUseCase,
    private val revokeAllPermissionUseCase: RevokeAllPermissionUseCase
) : ViewModel(coroutineDispatcher) {

    private val _messageSharedFlow = MutableSharedFlow<BalloonNotificationModel>()
    val messageSharedFlow: SharedFlow<BalloonNotificationModel> = _messageSharedFlow.asSharedFlow()

    private val _permissionStateFlow = MutableStateFlow<List<PermissionStateModel>>(emptyList())
    val permissionStateFlow: StateFlow<List<PermissionStateModel>> = _permissionStateFlow.asStateFlow()

    private val _grantButtonEnabled = MutableStateFlow(false)
    val grantButtonEnabled: StateFlow<Boolean> = _grantButtonEnabled.asStateFlow()

    private val _revokeButtonEnabled = MutableStateFlow(false)
    val revokeButtonEnabled: StateFlow<Boolean> = _revokeButtonEnabled.asStateFlow()

    private val _commentText = MutableStateFlow<String?>("")
    val commentText: StateFlow<String?> = _commentText.asStateFlow()

    private val _grantAllStateFlow = MutableStateFlow(PermissionUiState.Idle)
    val grantAllStateFlow: StateFlow<PermissionUiState> = _grantAllStateFlow.asStateFlow()

    private val _revokeAllStateFlow = MutableStateFlow(PermissionUiState.Idle)
    val revokeAllStateFlow: StateFlow<PermissionUiState> = _revokeAllStateFlow.asStateFlow()

    private var isShowRuntimePermissionChecked = true
    private var permissionSelected: PermissionStateModel? = null

    fun addListenerForPackageIdChange() {
        viewModelScope.launch {
            deviceManager.packageIdSelectedStateFlow.collect {
                fetchAllPermissions()
            }
        }
    }

    fun fetchAllPermissions() {
        viewModelScope.launch {
            _commentText.emit(null)

            fetchAllPermissionsUseCase.invoke().collect { result ->
                result.onSuccess {
                    val permissions = if (isShowRuntimePermissionChecked) {
                        it.filter { permissionStateModel ->
                            permissionStateModel.isRuntime
                        }.toMutableList() as ArrayList<PermissionStateModel>
                    } else {
                        it
                    }
                    _permissionStateFlow.emit(permissions)
                }.onFailure {
                    _messageSharedFlow.emit(
                        BalloonNotificationModel(
                            title = resourceManager.string("permissionsFetchErrorTitle"),
                            content = it.message,
                            type = NotificationType.ERROR
                        )
                    )
                }
            }
        }
    }

    fun setShowRuntimePermission(isChecked: Boolean) = viewModelScope.launch {
        isShowRuntimePermissionChecked = isChecked
        fetchAllPermissions()
    }

    fun setPermissionSelected(permission: PermissionStateModel?) = viewModelScope.launch {
        permission?.let {
            permissionSelected = it

            _grantButtonEnabled.emit(it.isRuntime && !it.isGranted)
            _revokeButtonEnabled.emit(it.isRuntime && it.isGranted)

            _commentText.emit(
                if (!it.isRuntime) {
                    resourceManager.string("errorInstallTimePermission")
                } else {
                    null
                }
            )
        }
    }

    fun getPermissionSelected(): PermissionStateModel? = permissionSelected

    fun grantSelectedPermission() = viewModelScope.launch {
        permissionSelected?.let {
            grantPermissionUseCase.invoke(it).collect { result ->
                result.onSuccess {
                    fetchAllPermissions()
                }.onFailure {
                    _messageSharedFlow.emit(
                        BalloonNotificationModel(
                            title = resourceManager.string("permissionsGrantErrorTitle"),
                            content = it.message,
                            type = NotificationType.ERROR
                        )
                    )
                }
            }
        } ?: run {
            _commentText.emit(resourceManager.string("selectAPermission"))
        }
    }

    fun revokeSelectedPermission() = viewModelScope.launch {
        permissionSelected?.let {
            revokePermissionUseCase.invoke(it).collect { result ->
                result.onSuccess {
                    fetchAllPermissions()
                }.onFailure {
                    _messageSharedFlow.emit(
                        BalloonNotificationModel(
                            title = resourceManager.string("permissionsRevokeErrorTitle"),
                            content = it.message,
                            type = NotificationType.ERROR
                        )
                    )
                }
            }
        } ?: run {
            _commentText.emit(resourceManager.string("selectAPermission"))
        }
    }

    fun grantAllPermission() = viewModelScope.launch {
        _grantAllStateFlow.emit(PermissionUiState.Loading)
        grantAllPermissionUseCase.invoke().collect { result ->
            result.onSuccess {
                _messageSharedFlow.emit(
                    BalloonNotificationModel(content = it, type = NotificationType.INFORMATION)
                )
                fetchAllPermissions()
            }.onFailure {
                _messageSharedFlow.emit(
                    BalloonNotificationModel(
                        title = resourceManager.string("permissionsGrantErrorTitle"),
                        content = it.message,
                        type = NotificationType.ERROR
                    )
                )
            }
            _grantAllStateFlow.emit(PermissionUiState.Idle)
        }
    }

    fun revokeAllPermission() = viewModelScope.launch {
        _revokeAllStateFlow.emit(PermissionUiState.Loading)
        revokeAllPermissionUseCase.invoke().collect { result ->
            result.onSuccess {
                _messageSharedFlow.emit(
                    BalloonNotificationModel(content = it, type = NotificationType.INFORMATION)
                )
                fetchAllPermissions()
            }.onFailure {
                _messageSharedFlow.emit(
                    BalloonNotificationModel(
                        title = resourceManager.string("permissionsRevokeErrorTitle"),
                        content = it.message,
                        type = NotificationType.ERROR
                    )
                )
            }
            _revokeAllStateFlow.emit(PermissionUiState.Idle)
        }
    }

    fun restartApplication() = viewModelScope.launch {
        restartAppUseCase.invoke().collect { result ->
            result.onFailure {
                _messageSharedFlow.emit(
                    BalloonNotificationModel(
                        title = resourceManager.string("failedRestartApplication"),
                        content = it.message,
                        type = NotificationType.ERROR
                    )
                )
            }
        }
    }
}
