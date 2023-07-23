package androidstudio.tools.missed.missedtoolwindow.model

sealed class MissedToolsWindowStateUi {
    object Loading : MissedToolsWindowStateUi()
    object Success : MissedToolsWindowStateUi()
    object NeedToConnectDevice : MissedToolsWindowStateUi()
    object Error : MissedToolsWindowStateUi()
}
