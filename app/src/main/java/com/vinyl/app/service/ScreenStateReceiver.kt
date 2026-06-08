package com.vinyl.app.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ScreenStateReceiver : BroadcastReceiver() {

    var onScreenOn: (() -> Unit)? = null
    var onScreenOff: (() -> Unit)? = null

    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            Intent.ACTION_SCREEN_ON -> onScreenOn?.invoke()
            Intent.ACTION_SCREEN_OFF -> onScreenOff?.invoke()
        }
    }
}
