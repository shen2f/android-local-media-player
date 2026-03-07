package com.shen.mediaplayer.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "favorites",
    indices = [
        Index(value = ["media_type"], unique = false)
    ]
)
data class FavoritesEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,
    @ColumnInfo(name = "file_path")
    val filePath: String,
    @ColumnInfo(name = "file_name")
    val fileName: String,
    @ColumnInfo(name = "media_type")
    val mediaType: Int,
    @ColumnInfo(name = "folder_path")
    val folderPath: String,
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)
