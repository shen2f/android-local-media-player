package com.shen.mediaplayer.feature.folders

data class FolderItem(
    val path: String,
    val name: String,
    val mediaCount: Int,
    val lastModified: Long
)