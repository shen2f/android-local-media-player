package com.shen.mediaplayer.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "playlist_entries",
    indices = [
        Index(value = ["playlist_id"], unique = false),
        Index(value = ["playlist_id", "sort_order"], unique = false)
    ]
)
data class PlaylistEntriesEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,
    @ColumnInfo(name = "playlist_id")
    val playlistId: Long,
    @ColumnInfo(name = "file_path")
    val filePath: String,
    @ColumnInfo(name = "file_name")
    val fileName: String,
    @ColumnInfo(name = "sort_order")
    val sortOrder: Int = 0,
    @ColumnInfo(name = "added_at")
    val addedAt: Long = System.currentTimeMillis()
)
