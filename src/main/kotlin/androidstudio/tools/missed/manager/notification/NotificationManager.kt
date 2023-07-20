package androidstudio.tools.missed.manager.notification

import androidstudio.tools.missed.manager.notification.model.BalloonNotificationModel
import java.awt.event.InputEvent

interface NotificationManager {

    fun showBalloon(message: BalloonNotificationModel)

    fun showBalloonWithButton(message: BalloonNotificationModel, listener: ((e: InputEvent?) -> Unit)? = null)
}
