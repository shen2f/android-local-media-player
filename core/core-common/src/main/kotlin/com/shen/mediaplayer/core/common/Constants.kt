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
}
