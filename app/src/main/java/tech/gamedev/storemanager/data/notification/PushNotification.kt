package tech.gamedev.storemanager.data.notification

import tech.gamedev.storemanager.data.notification.NotificationData

data class PushNotification(
        val data: NotificationData,
        val to: String
) {
}