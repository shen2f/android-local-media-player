package com.shen.mediaplayer.core.common

object Constants {
    const val DB_NAME = "media_player.db"
    const val DB_VERSION = 1
    
    // Media types
    const val MEDIA_TYPE_VIDEO = 1
    const val MEDIA_TYPE_AUDIO = 2
    const val MEDIA_TYPE_IMAGE = 3
    
    // Preference keys
    const val PREF_ONLINE_ENABLED = "online_enabled"
    const val PREF_FIRST_LAUNCH = "first_launch"
    
    // Default scan directories
    val DEFAULT_SCAN_DIRECTORIES = listOf(
        "/sdcard/DCIM",
        "/sdcard/Movies", 
        "/sdcard/Music",
        "/sdcard/Pictures"
    )

    // Video file extensions
    val VIDEO_EXTENSIONS = setOf(
        "mp4", "mkv", "avi", "rmvb", "flv", "wmv", "webm",
        "mov", "mpeg", "mpg", "3gp", "ts", "m4v"
    )

    // Audio file extensions
    val AUDIO_EXTENSIONS = setOf(
        "mp3", "aac", "flac", "wav", "ogg", "m4a", "wma",
        "ape", "alac", "aif", "aiff"
    )

    // Image file extensions
    val IMAGE_EXTENSIONS = setOf(
        "jpg", "jpeg", "png", "gif", "webp", "bmp", "heic",
        "heif", "raw"
    )
}
