package com.vinyl.app.service

import android.app.Activity
import android.content.IntentSender
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability

class InAppUpdateManager(
    private val activity: ComponentActivity
) {
    private val appUpdateManager: AppUpdateManager = AppUpdateManagerFactory.create(activity)

    private val updateLauncher: ActivityResultLauncher<IntentSenderRequest> =
        activity.registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) { result ->
            if (result.resultCode != Activity.RESULT_OK) {
                // User cancelled or update failed — handled silently
            }
        }

    fun checkForUpdate() {
        appUpdateManager
            .appUpdateInfo
            .addOnSuccessListener { info ->
                when {
                    info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                        && info.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE) -> {
                        startUpdate(info, AppUpdateType.IMMEDIATE)
                    }
                    info.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE) -> {
                        startUpdate(info, AppUpdateType.FLEXIBLE)
                    }
                }
            }
            .addOnFailureListener {
                // Not installed via Play Store — skip silently
            }
    }

    fun onResume() {
        appUpdateManager
            .appUpdateInfo
            .addOnSuccessListener { info ->
                if (info.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                    startUpdate(info, AppUpdateType.IMMEDIATE)
                }
            }
    }

    private fun startUpdate(info: AppUpdateInfo, type: Int) {
        appUpdateManager
            .startUpdateFlowForResult(info, type)
            .addOnSuccessListener { intentSender: IntentSender ->
                updateLauncher.launch(
                    IntentSenderRequest.Builder(intentSender).build()
                )
            }
    }
}
