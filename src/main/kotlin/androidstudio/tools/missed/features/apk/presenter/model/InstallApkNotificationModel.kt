package androidstudio.tools.missed.features.apk.presenter.model

import androidstudio.tools.missed.manager.notification.model.BalloonNotificationModel

data class InstallApkNotificationModel(val notificationModel: BalloonNotificationModel, val saveDirectory: String)
