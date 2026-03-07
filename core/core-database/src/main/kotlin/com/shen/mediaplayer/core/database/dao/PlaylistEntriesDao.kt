package com.shen.mediaplayer.core.database.dao

import androidx.room.*
import com.shen.mediaplayer.core.database.entity.PlaylistEntriesEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistEntriesDao {
    
    @Query("SELECT * FROM playlist_entries WHERE playlistId = :playlistId ORDER BY sortOrder ASC")
    fun getByPlaylistId(playlistId: Long): Flow<List<PlaylistEntriesEntity>>
    
    @Insert
    suspend fun insert(entity: PlaylistEntriesEntity): Long
    
    @Update
    suspend fun update(entity: PlaylistEntriesEntity)
    
    @Delete
    suspend fun delete(entity: PlaylistEntriesEntity)
    
    @Query("DELETE FROM playlist_entries WHERE playlistId = :playlistId AND filePath = :filePath")
    suspend fun deleteByPlaylistAndPath(playlistId: Long, filePath: String)
    
    @Query("DELETE FROM playlist_entries WHERE playlistId = :playlistId")
    suspend fun deleteByPlaylistId(playlistId: Long)
}
