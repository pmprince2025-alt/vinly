package com.vinyl.app.ui.onboarding

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.vinyl.app.ui.theme.*

@Composable
fun PermissionScreen(
    onPermissionGranted: () -> Unit
) {
    val context = LocalContext.current

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
                    context.startActivity(
                        Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
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
