package com.shen.mediaplayer.media.player.di

import com.shen.mediaplayer.media.player.AudioPlayer
import com.shen.mediaplayer.media.player.ExoAudioPlayer
import com.shen.mediaplayer.media.player.ExoVideoPlayer
import com.shen.mediaplayer.media.player.VideoPlayer
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class PlayerModule {

    @Binds
    abstract fun bindVideoPlayer(
        exoVideoPlayer: ExoVideoPlayer
    ): VideoPlayer

    @Binds
    abstract fun bindAudioPlayer(
        exoAudioPlayer: ExoAudioPlayer
    ): AudioPlayer
}
