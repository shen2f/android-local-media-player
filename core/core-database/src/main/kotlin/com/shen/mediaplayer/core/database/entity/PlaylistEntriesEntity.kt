package com.shen.mediaplayer.core.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "playlist_entries",
    indices = [
        Index(value = ["playlistId"], unique = false),
        Index(value = ["playlistId", "sortOrder"], unique = false)
    ]
)
data class PlaylistEntriesEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val playlistId: Long,
    val filePath: String,
    val fileName: String,
    val sortOrder: Int = 0,
    val addedAt: Long = System.currentTimeMillis()
)
