package com.shen.mediaplayer.core.database.dao

import androidx.room.*
import com.shen.mediaplayer.core.database.entity.FavoritesEntity
import kotlin.jvm.JvmSuppressWildcards
import kotlinx.coroutines.flow.Flow

// @Dao
interface FavoritesDao {
    
    @Query("SELECT * FROM favorites WHERE media_type = :mediaType ORDER BY created_at DESC")
    fun getByMediaType(mediaType: Int): Flow<@JvmSuppressWildcards List<FavoritesEntity>>
    
    @Query("SELECT * FROM favorites WHERE file_path = :filePath LIMIT 1")
    suspend fun getByPath(filePath: String): FavoritesEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: FavoritesEntity): Long
    
    @Delete
    suspend fun delete(entity: FavoritesEntity)
    
    @Query("DELETE FROM favorites WHERE id = :id")
    suspend fun deleteById(id: Long)
}
