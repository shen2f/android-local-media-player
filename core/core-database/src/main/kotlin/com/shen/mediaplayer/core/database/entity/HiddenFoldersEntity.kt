package com.shen.mediaplayer.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hidden_folders")
data class HiddenFoldersEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,
    @ColumnInfo(name = "folder_path")
    val folderPath: String,
    @ColumnInfo(name = "folder_name")
    val folderName: String,
    @ColumnInfo(name = "is_protected")
    val isProtected: Int = 0,
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)
