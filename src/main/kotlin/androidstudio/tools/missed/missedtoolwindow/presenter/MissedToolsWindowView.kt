package androidstudio.tools.missed.missedtoolwindow.presenter

import androidstudio.tools.missed.dependencymanager.DependencyInjectionInjectionManagerImpl
import androidstudio.tools.missed.features.ErrorView
import androidstudio.tools.missed.features.LoadingView
import androidstudio.tools.missed.features.MainView
import androidstudio.tools.missed.features.NeedToConnectDeviceView
import androidstudio.tools.missed.manager.notification.NotificationManager
import androidstudio.tools.missed.manager.resource.ResourceManager
import androidstudio.tools.missed.missedtoolwindow.model.MissedToolsWindowStateUi
import androidstudio.tools.missed.missedtoolwindow.visibilityListener.VisibilityMissedToolsWindowListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.openapi.wm.ex.ToolWindowManagerListener
import com.intellij.ui.content.ContentFactory
import com.intellij.util.messages.MessageBusConnection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.swing.Swing
import kotlinx.coroutines.withContext
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent.inject

const val TOOL_WINDOW_ID = "Android Studio Tools Missed"

class MissedToolsWindowView : ToolWindowFactory {

    private val applicationScope: CoroutineScope by inject(
        qualifier = named("ApplicationScope"),
        clazz = CoroutineScope::class.java
    )
    private val resourceManager: ResourceManager by inject(ResourceManager::class.java)
    private val mainView: MainView by inject(MainView::class.java)
    private val viewModel: MissedToolsWindowViewModel by inject(MissedToolsWindowViewModel::class.java)
    private val notificationManager: NotificationManager by inject(NotificationManager::class.java)

    private var toolWindow: ToolWindow? = null
    private lateinit var messageBusConnection: MessageBusConnection

    init {
        initialDependencyInjection()
    }

    private fun initialDependencyInjection() {
        DependencyInjectionInjectionManagerImpl().start()
    }

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        this.toolWindow = toolWindow

        // Register the ToolWindowManagerListener
        messageBusConnection = project.messageBus.connect()
        messageBusConnection.subscribe(
            ToolWindowManagerListener.TOPIC,
            VisibilityMissedToolsWindowListener(applicationScope)
        )

        applicationScope.launch {
            viewModel.uiStateFlow.collectLatest { state ->
                setView(state)
            }
        }
        viewModel.initial()
        initialObserve()
    }

    private suspend fun setView(state: MissedToolsWindowStateUi) {
        withContext(Dispatchers.Swing) {
            val view = when (state) {
                MissedToolsWindowStateUi.Loading -> LoadingView()
                MissedToolsWindowStateUi.Success -> mainView
                MissedToolsWindowStateUi.NeedToConnectDevice -> NeedToConnectDeviceView(resourceManager) {
                    viewModel.initial()
                }

                MissedToolsWindowStateUi.Error -> ErrorView(resourceManager) {
                    viewModel.initial()
                }
            }
            val content = ContentFactory.getInstance().createContent(view, "", false)
            toolWindow?.contentManager?.removeAllContents(true)
            toolWindow?.contentManager?.addContent(content)
        }
    }

    private fun initialObserve() {
        applicationScope.launch {
            viewModel.messageSharedFlow.collect {
                notificationManager.showBalloon(it)
            }
        }
    }
}
