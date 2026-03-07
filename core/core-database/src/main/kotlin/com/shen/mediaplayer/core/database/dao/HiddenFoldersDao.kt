package com.shen.mediaplayer.core.database.dao

import androidx.room.*
import com.shen.mediaplayer.core.database.entity.HiddenFoldersEntity
import kotlin.jvm.JvmSuppressWildcards
import kotlinx.coroutines.flow.Flow

@Dao
interface HiddenFoldersDao {
    
    @Query("SELECT * FROM hidden_folders ORDER BY createdAt DESC")
    fun getAll(): Flow<List<@JvmSuppressWildcards HiddenFoldersEntity>>
    
    @Query("SELECT * FROM hidden_folders WHERE folderPath = :folderPath LIMIT 1")
    suspend fun getByPath(folderPath: String): HiddenFoldersEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: HiddenFoldersEntity): Long
    
    @Delete
    suspend fun delete(entity: HiddenFoldersEntity)
}
