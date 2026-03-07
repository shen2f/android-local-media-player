package com.shen.mediaplayer.core.database.dao

import androidx.room.*
import com.shen.mediaplayer.core.database.entity.PlaybackHistoryEntity
import kotlin.jvm.JvmSuppressWildcards
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaybackHistoryDao {
    
    @Query("SELECT * FROM playback_history ORDER BY lastPlayedAt DESC")
    fun getAll(): Flow<List<@JvmSuppressWildcards PlaybackHistoryEntity>>
    
    @Query("SELECT * FROM playback_history WHERE filePath = :filePath LIMIT 1")
    suspend fun getByPath(filePath: String): PlaybackHistoryEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: PlaybackHistoryEntity): Long
    
    @Update
    suspend fun update(entity: PlaybackHistoryEntity)
    
    @Delete
    suspend fun delete(entity: PlaybackHistoryEntity)
    
    @Query("DELETE FROM playback_history")
    suspend fun deleteAll()
    
    @Query("DELETE FROM playback_history WHERE id = :id")
    suspend fun deleteById(id: Long)
}
