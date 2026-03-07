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
    val id: Long = 0,
    val filePath: String,
    val fileName: String,
    @ColumnInfo(name = "media_type")
    val mediaType: Int,
    val folderPath: String,
    val createdAt: Long = System.currentTimeMillis()
)
