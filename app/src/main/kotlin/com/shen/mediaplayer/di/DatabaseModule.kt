package com.shen.mediaplayer.di

import android.content.Context
import androidx.room.Room
import com.shen.mediaplayer.core.common.Constants
import com.shen.mediaplayer.core.database.AppDatabase
import com.shen.mediaplayer.core.database.dao.AppConfigDao
import com.shen.mediaplayer.core.database.dao.FavoritesDao
import com.shen.mediaplayer.core.database.dao.HiddenFoldersDao
import com.shen.mediaplayer.core.database.dao.PlaybackHistoryDao
import com.shen.mediaplayer.core.database.dao.PlaylistEntriesDao
import com.shen.mediaplayer.core.database.dao.PlaylistsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            Constants.DB_NAME
        ).build()
    }
    
    @Provides
    fun providePlaybackHistoryDao(db: AppDatabase): PlaybackHistoryDao {
        return db.playbackHistoryDao()
    }
    
    @Provides
    fun provideFavoritesDao(db: AppDatabase): FavoritesDao {
        return db.favoritesDao()
    }
    
    @Provides
    fun provideHiddenFoldersDao(db: AppDatabase): HiddenFoldersDao {
        return db.hiddenFoldersDao()
    }
    
    @Provides
    fun providePlaylistsDao(db: AppDatabase): PlaylistsDao {
        return db.playlistsDao()
    }
    
    @Provides
    fun providePlaylistEntriesDao(db: AppDatabase): PlaylistEntriesDao {
        return db.playlistEntriesDao()
    }
    
    @Provides
    fun provideAppConfigDao(db: AppDatabase): AppConfigDao {
        return db.appConfigDao()
    }
}
