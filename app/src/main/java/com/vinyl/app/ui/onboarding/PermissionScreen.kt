package com.vinyl.app.ui.onboarding

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.vinyl.app.ui.theme.*
import kotlinx.coroutines.delay

fun isNotificationListenerEnabled(context: Context): Boolean {
    val flat = Settings.Secure.getString(
        context.contentResolver,
        "enabled_notification_listeners"
    ) ?: ""
    return flat.contains(context.packageName)
}

@Composable
fun PermissionScreen(
    onPermissionGranted: () -> Unit
) {
    val context = LocalContext.current

    // Poll for permission — handles both initial state and return from Settings
    LaunchedEffect(Unit) {
        while (true) {
            if (isNotificationListenerEnabled(context)) {
                onPermissionGranted()
                break
            }
            delay(1000)
        }
    }

    val requestNotificationPermission = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { /* no-op; NLS detection handles navigation */ }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Your music,\nvisualized.",
                style = VinylTypography.headlineLarge,
                color = OnSurface,
                textAlign = TextAlign.Center,
                lineHeight = VinylTypography.headlineLarge.lineHeight
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Vinyl transforms your music into a cinematic turntable experience.\n\nIt reads what's playing from your music apps and displays it as a beautiful, spinning vinyl record.",
                style = VinylTypography.bodyMedium,
                color = OnSurfaceMuted,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                    context.startActivity(
                        Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentAmber,
                    contentColor = Background
                )
            ) {
                Text(
                    text = "Enable Access",
                    style = VinylTypography.bodyLarge
                )
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Vinyl reads what's playing. Nothing else.",
                style = VinylTypography.bodySmall,
                color = OnSurfaceSubtle,
                textAlign = TextAlign.Center
            )
        }
    }
}
