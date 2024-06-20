package androidstudio.tools.missed.features.apkmanagement.presenter.model

import androidstudio.tools.missed.manager.notification.model.BalloonNotificationModel

data class InstallApkNotificationModel(val notificationModel: BalloonNotificationModel, val saveDirectory: String)
