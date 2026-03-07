package com.shen.mediaplayer.core.database.dao

import androidx.room.*
import com.shen.mediaplayer.core.database.entity.PlaylistsEntity
import kotlin.jvm.JvmSuppressWildcards
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistsDao {
    
    @Query("SELECT * FROM playlists ORDER BY created_at DESC")
    @JvmSuppressWildcards
    fun getAll(): Flow<List<PlaylistsEntity>>
    
    @Query("SELECT * FROM playlists WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): PlaylistsEntity?
    
    @Insert
    suspend fun insert(entity: PlaylistsEntity): Long
    
    @Update
    suspend fun update(entity: PlaylistsEntity)
    
    @Delete
    suspend fun delete(entity: PlaylistsEntity)
}
