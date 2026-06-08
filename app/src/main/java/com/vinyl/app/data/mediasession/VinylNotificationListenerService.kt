package com.vinyl.app.data.mediasession

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class VinylNotificationListenerService : NotificationListenerService() {

    @Inject
    lateinit var dataSource: MediaSessionDataSourceImpl

    override fun onListenerConnected() {
        super.onListenerConnected()

        // Scan existing notifications (handles the case where music was already playing)
        for (sbn in activeNotifications) {
            dataSource.onNotificationPosted(sbn)
        }

        dataSource.onListenerConnected()
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        dataSource.onListenerDisconnected()
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        super.onNotificationPosted(sbn)
        dataSource.onNotificationPosted(sbn)
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        super.onNotificationRemoved(sbn)
        dataSource.onNotificationRemoved(sbn)
    }
}
