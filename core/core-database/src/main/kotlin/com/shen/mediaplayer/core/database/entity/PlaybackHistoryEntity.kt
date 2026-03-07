package com.shen.mediaplayer.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.shen.mediaplayer.core.common.Constants

@Entity(
    tableName = "playback_history",
    indices = [
        Index(value = ["last_played_at"], unique = false),
        Index(value = ["file_path"], unique = true)
    ]
)
data class PlaybackHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "file_path")
    val filePath: String,
    @ColumnInfo(name = "file_name")
    val fileName: String,
    @ColumnInfo(name = "file_size")
    val fileSize: Long?,
    @ColumnInfo(name = "media_type")
    val mediaType: Int,
    @ColumnInfo(name = "duration")
    val duration: Long?,
    @ColumnInfo(name = "progress")
    val progress: Long = 0,
    @ColumnInfo(name = "folder_path")
    val folderPath: String,
    @ColumnInfo(name = "last_played_at")
    val lastPlayedAt: Long,
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
)
