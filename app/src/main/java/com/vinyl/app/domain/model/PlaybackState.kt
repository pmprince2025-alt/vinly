package com.vinyl.app.domain.model

data class PlaybackState(
    val status: Status,
    val position: Long,
    val playbackSpeed: Float,
    val timestamp: Long
) {
    enum class Status { PLAYING, PAUSED, STOPPED, BUFFERING, NONE }

    fun currentPosition(): Long {
        return if (status == Status.PLAYING) {
            val elapsed = System.currentTimeMillis() - timestamp
            position + (elapsed * playbackSpeed).toLong()
        } else {
            position
        }
    }
}
