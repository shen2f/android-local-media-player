package com.shen.mediaplayer.core.common.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MediaFile(
    val id: Long,
    val filePath: String,
    val fileName: String,
    val fileSize: Long,
    val mediaType: MediaType,
    val duration: Long,
    val folderPath: String,
    val dateModified: Long,
    val mimeType: String?
) : Parcelable {
    val isVideo: Boolean get() = mediaType == MediaType.VIDEO
    val isAudio: Boolean get() = mediaType == MediaType.AUDIO
    val isImage: Boolean get() = mediaType == MediaType.IMAGE
}

enum class MediaType {
    VIDEO, AUDIO, IMAGE
}
