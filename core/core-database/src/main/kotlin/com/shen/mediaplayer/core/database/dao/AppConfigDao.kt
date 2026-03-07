package com.shen.mediaplayer.core.database.dao

import androidx.room.*
import com.shen.mediaplayer.core.database.entity.AppConfigEntity

@Dao
interface AppConfigDao {
    
    @Query("SELECT * FROM app_config WHERE `key` = :key LIMIT 1")
    suspend fun getByKey(key: String): AppConfigEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: AppConfigEntity)
    
    @Query("DELETE FROM app_config WHERE `key` = :key")
    suspend fun deleteByKey(key: String)
}
