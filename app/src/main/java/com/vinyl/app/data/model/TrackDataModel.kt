package com.vinyl.app.data.model

import android.graphics.Bitmap
import android.net.Uri

data class TrackDataModel(
    val title: String,
    val artist: String,
    val album: String,
    val albumArtBitmap: Bitmap?,
    val albumArtUri: Uri?,
    val duration: Long,
    val sourcePackage: String
)
