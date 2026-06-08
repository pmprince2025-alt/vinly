package com.vinyl.app.ui.onboarding

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.vinyl.app.ui.theme.*

fun isNotificationListenerEnabled(context: Context): Boolean {
    return try {
        val packages = NotificationManagerCompat.getEnabledListenerPackages(context)
        packages.contains(context.packageName)
    } catch (_: Exception) {
        false
    }
}

@Composable
fun PermissionScreen(
    onPermissionGranted: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var nlsGranted by remember { mutableStateOf(false) }

    // Check immediately after composition
    LaunchedEffect(Unit) {
        if (isNotificationListenerEnabled(context)) {
            nlsGranted = true
        }
    }

    // Re-check each time the activity resumes (e.g., returning from Settings)
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME && isNotificationListenerEnabled(context)) {
                nlsGranted = true
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    // Navigate when permission is detected
    LaunchedEffect(nlsGranted) {
        if (nlsGranted) {
            onPermissionGranted()
        }
    }

    var openNlsSettings by remember { mutableStateOf(false) }

    val requestNotificationPermission = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { openNlsSettings = true }

    LaunchedEffect(openNlsSettings) {
        if (openNlsSettings) {
            context.startActivity(
                Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            )
            openNlsSettings = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 32.dp, vertical = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
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
                text = "Vinyl reads what's playing from your music apps\nand displays it as a beautiful, spinning vinyl record.",
                style = VinylTypography.bodyMedium,
                color = OnSurfaceMuted,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
                    } else {
                        context.startActivity(
                            Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS).apply {
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentAmber,
                    contentColor = Background
                )
            ) {
                Text("Enable Access", style = VinylTypography.bodyLarge)
            }

            Text(
                text = "If the settings page is unavailable, tap \"Open App Info\"\nand enable \"Notification Access\" there.",
                style = VinylTypography.bodySmall,
                color = OnSurfaceSubtle,
                textAlign = TextAlign.Center,
                lineHeight = VinylTypography.bodySmall.lineHeight
            )

            OutlinedButton(
                onClick = {
                    context.startActivity(
                        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.parse("package:${context.packageName}")
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                    )
                },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Open App Info", style = VinylTypography.bodyMedium)
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = { onPermissionGranted() },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = OnSurfaceMuted.copy(alpha = 0.2f),
                    contentColor = OnSurface
                )
            ) {
                Text("Continue Anyway", style = VinylTypography.bodyMedium)
            }
        }
    }
}
