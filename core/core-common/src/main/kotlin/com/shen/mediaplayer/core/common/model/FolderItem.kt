package com.shen.mediaplayer.core.common.model

data class FolderItem(
    val path: String,
    val name: String,
    val mediaCount: Int,
    val lastModified: Long
)