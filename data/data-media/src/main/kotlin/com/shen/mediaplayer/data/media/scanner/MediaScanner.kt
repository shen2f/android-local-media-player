package com.shen.mediaplayer.data.media.scanner

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.shen.mediaplayer.core.common.Constants
import com.shen.mediaplayer.core.common.model.MediaFile
import com.shen.mediaplayer.core.common.model.MediaType
import com.shen.mediaplayer.utils.storage.StorageHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

/**
 * 媒体文件扫描器
 * 负责扫描设备上的媒体文件，兼容不同Android版本的分区存储
 */
class MediaScanner @Inject constructor(
    private val context: Context,
    private val storageHelper: StorageHelper
) {

    /**
     * 扫描所有媒体文件，通过Flow返回扫描进度和结果
     */
    fun scanAllMedia(): Flow<ScanResult> = flow {
        emit(ScanResult.Started)

        val videoFiles = mutableListOf<MediaFile>()
        val audioFiles = mutableListOf<MediaFile>()
        val imageFiles = mutableListOf<MediaFile>()

        // 通过MediaStore扫描系统媒体库
        scanWithMediaStore(MediaType.VIDEO, videoFiles)
        emit(ScanResult.Progress(videoFiles.size, 0))

        scanWithMediaStore(MediaType.AUDIO, audioFiles)
        emit(ScanResult.Progress(videoFiles.size + audioFiles.size, 1))

        scanWithMediaStore(MediaType.IMAGE, imageFiles)
        emit(ScanResult.Progress(videoFiles.size + audioFiles.size + imageFiles.size, 2))

        emit(ScanResult.Finished(
            videoFiles = videoFiles,
            audioFiles = audioFiles,
            imageFiles = imageFiles,
            total = videoFiles.size + audioFiles.size + imageFiles.size
        ))
    }

    /**
     * 使用MediaStore API扫描指定类型的媒体文件
     */
    private suspend fun scanWithMediaStore(mediaType: MediaType, result: MutableList<MediaFile>) {
        withContext(Dispatchers.IO) {
            val contentResolver: ContentResolver = context.contentResolver

            val uri: Uri = MediaStore.Files.getContentUri("external")
            val projection = arrayOf(
                MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.DISPLAY_NAME,
                MediaStore.Files.FileColumns.SIZE,
                MediaStore.Files.FileColumns.DATE_MODIFIED,
                MediaStore.Files.FileColumns.DURATION,
                MediaStore.Files.FileColumns.MIME_TYPE
            )

            val selection = when (mediaType) {
                MediaType.VIDEO -> "${MediaStore.Files.FileColumns.MEDIA_TYPE} = ${MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO}"
                MediaType.AUDIO -> "${MediaStore.Files.FileColumns.MEDIA_TYPE} = ${MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO}"
                MediaType.IMAGE -> "${MediaStore.Files.FileColumns.MEDIA_TYPE} = ${MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE}"
                else -> null
            } ?: return@withContext

            val sortOrder = "${MediaStore.Files.FileColumns.DATE_MODIFIED} DESC"

            try {
                contentResolver.query(
                    uri,
                    projection,
                    selection,
                    null,
                    sortOrder
                )?.use { cursor ->
                    val idColumn = cursor.getColumnIndex(MediaStore.Files.FileColumns._ID)
                    val dataColumn = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA)
                    val nameColumn = cursor.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME)
                    val sizeColumn = cursor.getColumnIndex(MediaStore.Files.FileColumns.SIZE)
                    val dateColumn = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATE_MODIFIED)
                    val durationColumn = cursor.getColumnIndex(MediaStore.Files.FileColumns.DURATION)
                    val mimeTypeColumn = cursor.getColumnIndex(MediaStore.Files.FileColumns.MIME_TYPE)

                    while (cursor.moveToNext()) {
                        val filePath = if (dataColumn >= 0) cursor.getString(dataColumn) else null
                        if (filePath.isNullOrEmpty()) continue

                        val file = File(filePath)
                        if (!file.exists()) continue

                        val mediaFile = MediaFile(
                            id = cursor.getLong(idColumn),
                            filePath = filePath,
                            fileName = if (nameColumn >= 0) cursor.getString(nameColumn) ?: file.name else file.name,
                            fileSize = if (sizeColumn >= 0) cursor.getLong(sizeColumn) else file.length(),
                            mediaType = mediaType,
                            duration = if (durationColumn >= 0 && mediaType != MediaType.IMAGE) cursor.getLong(durationColumn) * 1000 else 0,
                            folderPath = file.parent ?: "",
                            dateModified = if (dateColumn >= 0) cursor.getLong(dateColumn) * 1000 else System.currentTimeMillis(),
                            mimeType = if (mimeTypeColumn >= 0) cursor.getString(mimeTypeColumn) else null
                        )
                        result.add(mediaFile)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 扫描指定目录下的媒体文件
     */
    suspend fun scanDirectory(directory: File): List<MediaFile> = withContext(Dispatchers.IO) {
        val result = mutableListOf<MediaFile>()
        if (!directory.exists()) return@withContext result

        directory.listFiles()?.forEach { file ->
            if (file.isDirectory) {
                result.addAll(scanDirectory(file))
            } else {
                val mediaType = getMediaTypeFromFileName(file.name)
                if (mediaType != null) {
                    val mediaFile = MediaFile(
                        id = 0,
                        filePath = file.absolutePath,
                        fileName = file.name,
                        fileSize = file.length(),
                        mediaType = mediaType,
                        duration = 0,
                        folderPath = file.parent ?: "",
                        dateModified = file.lastModified(),
                        mimeType = null
                    )
                    result.add(mediaFile)
                }
            }
        }
        return@withContext result
    }

    /**
     * 根据文件名获取媒体类型
     */
    private fun getMediaTypeFromFileName(fileName: String): MediaType? {
        val extension = fileName.substringAfterLast('.', "").lowercase()
        return when {
            Constants.VIDEO_EXTENSIONS.contains(extension) -> MediaType.VIDEO
            Constants.AUDIO_EXTENSIONS.contains(extension) -> MediaType.AUDIO
            Constants.IMAGE_EXTENSIONS.contains(extension) -> MediaType.IMAGE
            else -> null
        }
    }

    /**
     * 扫描结果密封类
     */
    sealed class ScanResult {
        object Started : ScanResult()
        data class Progress(val scanned: Int, val currentType: Int) : ScanResult()
        data class Finished(
            val videoFiles: List<MediaFile>,
            val audioFiles: List<MediaFile>,
            val imageFiles: List<MediaFile>,
            val total: Int
        ) : ScanResult()
    }
}
