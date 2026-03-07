package com.shen.mediaplayer.utils.storage

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.shen.mediaplayer.core.common.Constants
import java.io.File

object StorageHelper {
    
    fun getDefaultDirectories(): List<File> {
        val directories = mutableListOf<File>()
        Constants.DEFAULT_SCAN_DIRECTORIES.forEach { path ->
            val dir = File(path)
            if (dir.exists() && dir.isDirectory) {
                directories.add(dir)
            }
        }
        return directories
    }
    
    fun getExternalStorageRoot(): File? {
        return Environment.getExternalStorageDirectory()
    }
    
    fun isFileExists(path: String): Boolean {
        return File(path).exists()
    }
    
    fun getFileNameFromPath(path: String): String {
        return File(path).nameWithoutExtension
    }
    
    fun getExtensionFromPath(path: String): String {
        return File(path).extension.lowercase()
    }
    
    fun getParentPath(path: String): String {
        return File(path).parent ?: ""
    }
    
    fun getParentName(path: String): String {
        return File(path).parentFile?.name ?: ""
    }
    
    fun queryMediaFiles(
        contentResolver: ContentResolver,
        mediaType: Int
    ): List<MediaFileInfo> {
        val uri = when (mediaType) {
            Constants.MEDIA_TYPE_VIDEO -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            Constants.MEDIA_TYPE_AUDIO -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            Constants.MEDIA_TYPE_IMAGE -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            else -> return emptyList()
        }
        
        val projection = arrayOf(
            MediaStore.MediaColumns._ID,
            MediaStore.MediaColumns.DATA,
            MediaStore.MediaColumns.DISPLAY_NAME,
            MediaStore.MediaColumns.SIZE,
            MediaStore.MediaColumns.DATE_ADDED,
            when (mediaType) {
                Constants.MEDIA_TYPE_VIDEO -> MediaStore.Video.Media.DURATION
                Constants.MEDIA_TYPE_AUDIO -> MediaStore.Audio.Media.DURATION
                else -> 0
            }
        )
        
        val result = mutableListOf<MediaFileInfo>()
        contentResolver.query(
            uri,
            projection,
            null,
            null,
            MediaStore.MediaColumns.DATE_ADDED + " DESC"
        )?.use { cursor ->
            val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE)
            val dateColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_ADDED)
            val durationColumn = when (mediaType) {
                Constants.MEDIA_TYPE_VIDEO, Constants.MEDIA_TYPE_AUDIO -> 
                    cursor.getColumnIndex(MediaStore.Video.Media.DURATION)
                else -> -1
            }
            
            while (cursor.moveToNext()) {
                val path = cursor.getString(dataColumn)
                if (path.isNullOrEmpty()) continue
                
                val name = cursor.getString(nameColumn) ?: ""
                val size = cursor.getLong(sizeColumn)
                val dateAdded = cursor.getLong(dateColumn) * 1000
                val duration = if (durationColumn >= 0) cursor.getLong(durationColumn) else null
                
                result.add(
                    MediaFileInfo(
                        path = path,
                        name = name,
                        size = size,
                        duration = duration,
                        mediaType = mediaType,
                        dateAdded = dateAdded
                    )
                )
            }
        }
        
        return result
    }
    
    data class MediaFileInfo(
        val path: String,
        val name: String,
        val size: Long,
        val duration: Long?,
        val mediaType: Int,
        val dateAdded: Long
    )
}
