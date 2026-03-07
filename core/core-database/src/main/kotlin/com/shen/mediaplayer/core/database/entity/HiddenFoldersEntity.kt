package com.shen.mediaplayer.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hidden_folders")
data class HiddenFoldersEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val folderPath: String,
    val folderName: String,
    val isProtected: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)
