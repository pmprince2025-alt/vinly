package com.vinyl.app.domain.model

import android.graphics.Bitmap
import android.net.Uri

data class Track(
    val title: String,
    val artist: String,
    val album: String,
    val albumArtBitmap: Bitmap?,
    val albumArtUri: Uri?,
    val duration: Long,
    val sourcePackage: String
)
