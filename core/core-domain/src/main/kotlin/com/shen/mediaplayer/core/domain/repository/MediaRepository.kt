package com.shen.mediaplayer.core.domain.repository

import com.shen.mediaplayer.core.common.model.FolderItem
import com.shen.mediaplayer.core.common.model.MediaFile

interface MediaRepository {
    suspend fun getAllAudioFiles(): List<MediaFile>
    suspend fun getAllVideoFiles(): List<MediaFile>
    suspend fun getFolderContent(path: String?): List<FolderItem>
    suspend fun getMediaFilesInFolder(path: String): List<MediaFile>
    suspend fun searchMedia(query: String): List<MediaFile>
}
