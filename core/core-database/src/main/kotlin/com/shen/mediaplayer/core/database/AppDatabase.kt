package com.shen.mediaplayer.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.shen.mediaplayer.core.database.dao.AppConfigDao
import com.shen.mediaplayer.core.database.dao.FavoritesDao
import com.shen.mediaplayer.core.database.dao.HiddenFoldersDao
import com.shen.mediaplayer.core.database.dao.PlaybackHistoryDao
import com.shen.mediaplayer.core.database.dao.PlaylistEntriesDao
import com.shen.mediaplayer.core.database.dao.PlaylistsDao
import com.shen.mediaplayer.core.database.entity.AppConfigEntity
import com.shen.mediaplayer.core.database.entity.FavoritesEntity
import com.shen.mediaplayer.core.database.entity.HiddenFoldersEntity
import com.shen.mediaplayer.core.database.entity.PlaybackHistoryEntity
import com.shen.mediaplayer.core.database.entity.PlaylistEntriesEntity
import com.shen.mediaplayer.core.database.entity.PlaylistsEntity

@Database(
    entities = [
        PlaybackHistoryEntity::class,
        FavoritesEntity::class,
        HiddenFoldersEntity::class,
        PlaylistsEntity::class,
        PlaylistEntriesEntity::class,
        AppConfigEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun playbackHistoryDao(): PlaybackHistoryDao
    abstract fun favoritesDao(): FavoritesDao
    abstract fun hiddenFoldersDao(): HiddenFoldersDao
    abstract fun playlistsDao(): PlaylistsDao
    abstract fun playlistEntriesDao(): PlaylistEntriesDao
    abstract fun appConfigDao(): AppConfigDao
}
