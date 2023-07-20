package androidstudio.tools.missed.manager.notification

import androidstudio.tools.missed.manager.notification.model.BalloonNotificationModel
import androidstudio.tools.missed.manager.resource.ResourceManager
import com.intellij.notification.Notification
import com.intellij.notification.NotificationAction
import com.intellij.notification.NotificationGroupManager
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.ProjectManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.awt.event.InputEvent

class NotificationManagerImpl(
    private val scope: CoroutineScope,
    private val resourceManager: ResourceManager
) : NotificationManager {

    override fun showBalloon(message: BalloonNotificationModel) {
        val notification: Notification = provideNotification(message = message)
        showNotification(notification, message.fadeoutTime)
    }

    override fun showBalloonWithButton(message: BalloonNotificationModel, listener: ((e: InputEvent?) -> Unit)?) {
        val notification: Notification = provideNotification(message = message)
        notification.addAction(object : NotificationAction(resourceManager.string("browseAPK")) {
            override fun actionPerformed(e: AnActionEvent, notification: Notification) {
                listener?.invoke(e.inputEvent)
            }
        })
        showNotification(notification, message.fadeoutTime)
    }

    private fun provideNotification(message: BalloonNotificationModel): Notification {
        return NotificationGroupManager.getInstance()
            .getNotificationGroup("Android Studio Tools Missed")
            .createNotification(
                title = message.title,
                content = message.content ?: "",
                type = message.type
            )
    }

    private fun showNotification(notification: Notification, fadeoutTime: Long) {
        notification.notify(ProjectManager.getInstance().openProjects[0])
        scope.launch {
            delay(fadeoutTime)
            notification.hideBalloon()
        }
    }
}
