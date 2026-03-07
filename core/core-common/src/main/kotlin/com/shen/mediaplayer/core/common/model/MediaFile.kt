package com.shen.mediaplayer.core.common.model

import android.os.Parcel
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

    override fun describeContents(): Int {
        return 0
    }

    companion object : Parcelable.Creator<MediaFile> {
        override fun createFromParcel(parcel: Parcel): MediaFile {
            return MediaFile(
                id = parcel.readLong(),
                filePath = parcel.readString()!!,
                fileName = parcel.readString()!!,
                fileSize = parcel.readLong(),
                mediaType = MediaType.valueOf(parcel.readString()!!),
                duration = parcel.readLong(),
                folderPath = parcel.readString()!!,
                dateModified = parcel.readLong(),
                mimeType = parcel.readString()
            )
        }

        override fun newArray(size: Int): Array<MediaFile?> {
            return arrayOfNulls(size)
        }
    }
}

enum class MediaType {
    VIDEO, AUDIO, IMAGE
}
