package com.shen.mediaplayer.core.database.dao

import androidx.room.*
import com.shen.mediaplayer.core.database.entity.PlaylistEntriesEntity
import kotlin.jvm.JvmSuppressWildcards
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistEntriesDao {
    
    @Query("SELECT * FROM playlist_entries WHERE playlist_id = :playlistId ORDER BY sort_order ASC")
    fun getByPlaylistId(playlistId: Long): Flow<@JvmSuppressWildcards List<@JvmSuppressWildcards PlaylistEntriesEntity>>
    
    @Insert
    suspend fun insert(entity: PlaylistEntriesEntity): Long
    
    @Update
    suspend fun update(entity: PlaylistEntriesEntity)
    
    @Delete
    suspend fun delete(entity: PlaylistEntriesEntity)
    
    @Query("DELETE FROM playlist_entries WHERE playlist_id = :playlistId AND file_path = :filePath")
    suspend fun deleteByPlaylistAndPath(playlistId: Long, filePath: String)
    
    @Query("DELETE FROM playlist_entries WHERE playlist_id = :playlistId")
    suspend fun deleteByPlaylistId(playlistId: Long)
}
