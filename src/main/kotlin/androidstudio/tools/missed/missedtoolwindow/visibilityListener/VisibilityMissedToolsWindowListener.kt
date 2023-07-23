package androidstudio.tools.missed.missedtoolwindow.visibilityListener

import androidstudio.tools.missed.missedtoolwindow.presenter.TOOL_WINDOW_ID
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.openapi.wm.ex.ToolWindowManagerListener
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancelChildren

internal class VisibilityMissedToolsWindowListener(private val coroutineScope: CoroutineScope) :
    ToolWindowManagerListener {

    private var windowExpanded = false

    override fun stateChanged(
        toolWindowManager: ToolWindowManager,
        changeType: ToolWindowManagerListener.ToolWindowManagerEventType
    ) {
        val window = toolWindowManager.getToolWindow(TOOL_WINDOW_ID) ?: return

        val isWindowExpanded = window.isVisible
        val windowVisibilityChanged = isWindowExpanded != windowExpanded
        windowExpanded = isWindowExpanded

        if (windowVisibilityChanged && window.id == TOOL_WINDOW_ID) {
            if (changeType == ToolWindowManagerListener.ToolWindowManagerEventType.HideToolWindow) {
                println("New State for $TOOL_WINDOW_ID: $changeType")
                cancelAllJobInScope()
            } else if (changeType == ToolWindowManagerListener.ToolWindowManagerEventType.ActivateToolWindow) {
                println("New State for $TOOL_WINDOW_ID: $changeType")
            }
        }
    }

    private fun cancelAllJobInScope() {
        coroutineScope.coroutineContext.cancelChildren(CancellationException("HideToolWindow"))
    }
}
