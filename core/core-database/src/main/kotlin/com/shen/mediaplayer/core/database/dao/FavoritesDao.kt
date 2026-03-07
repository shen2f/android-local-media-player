package com.shen.mediaplayer.core.database.dao

import androidx.room.*
import com.shen.mediaplayer.core.database.entity.FavoritesEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritesDao {
    
    @Query("SELECT * FROM favorites WHERE mediaType = :mediaType ORDER BY createdAt DESC")
    fun getByMediaType(mediaType: Int): Flow<List<FavoritesEntity>>
    
    @Query("SELECT * FROM favorites WHERE filePath = :filePath LIMIT 1")
    suspend fun getByPath(filePath: String): FavoritesEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: FavoritesEntity): Long
    
    @Delete
    suspend fun delete(entity: FavoritesEntity)
    
    @Query("DELETE FROM favorites WHERE id = :id")
    suspend fun deleteById(id: Long)
}
