package com.shen.mediaplayer.core.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "favorites",
    indices = [
        Index(value = ["mediaType"], unique = false)
    ]
)
data class FavoritesEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val filePath: String,
    val fileName: String,
    val mediaType: Int,
    val folderPath: String,
    val createdAt: Long = System.currentTimeMillis()
)
