package androidstudio.tools.missed.manager.notification.model

import com.intellij.notification.NotificationType

data class BalloonNotificationModel(
    val title: String = "",
    val content: String?,
    val type: NotificationType = NotificationType.ERROR,
    val fadeoutTime: Long = 6000
)
