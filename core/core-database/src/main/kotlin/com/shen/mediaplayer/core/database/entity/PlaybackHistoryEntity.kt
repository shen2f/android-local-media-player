package com.shen.mediaplayer.core.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.shen.mediaplayer.core.common.Constants

@Entity(
    tableName = "playback_history",
    indices = [
        Index(value = ["lastPlayedAt"], unique = false),
        Index(value = ["filePath"], unique = true)
    ]
)
data class PlaybackHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val filePath: String,
    val fileName: String,
    val fileSize: Long?,
    val mediaType: Int,
    val duration: Long?,
    val progress: Long = 0,
    val folderPath: String,
    val lastPlayedAt: Long,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
