package com.shen.mediaplayer.feature_playlist.repository

import com.shen.mediaplayer.core.common.model.MediaFile
import com.shen.mediaplayer.feature_playlist.entity.PlaylistEntity
import com.shen.mediaplayer.feature_playlist.entity.PlaylistEntryEntity
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    fun getAllPlaylists(): Flow<List<PlaylistEntity>>
    fun getEntriesForPlaylist(playlistId: Long): Flow<List<PlaylistEntryEntity>>
    fun getPlaylistEntries(playlistId: Long): Flow<List<PlaylistEntryEntity>> = getEntriesForPlaylist(playlistId)
    fun getMediaFilesForPlaylist(playlistId: Long): Flow<List<MediaFile>>

    suspend fun createPlaylist(name: String, description: String? = null, coverPath: String? = null): Long
    suspend fun insertPlaylist(playlist: PlaylistEntity): Long = createPlaylist(playlist.name, playlist.description, playlist.coverPath)
    suspend fun updatePlaylist(playlist: PlaylistEntity)
    suspend fun deletePlaylist(playlistId: Long)
    suspend fun deletePlaylist(playlist: PlaylistEntity) = deletePlaylist(playlist.id)

    suspend fun addMediaToPlaylist(playlistId: Long, mediaId: Long, filePath: String)
    suspend fun addMediaListToPlaylist(playlistId: Long, mediaList: List<Pair<Long, String>>)
    suspend fun insertEntry(entry: PlaylistEntryEntity)
    suspend fun updateEntry(entry: PlaylistEntryEntity)
    suspend fun deleteEntry(entry: PlaylistEntryEntity)
    suspend fun removeMediaFromPlaylist(playlistId: Long, mediaId: Long)
    suspend fun updatePlaylistEntryPositions(playlistId: Long, entries: List<PlaylistEntryEntity>)
}
