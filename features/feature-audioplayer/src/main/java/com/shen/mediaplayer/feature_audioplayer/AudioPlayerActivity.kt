package com.shen.mediaplayer.feature_audioplayer

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.shen.mediaplayer.core_common.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import com.shen.mediaplayer.feature_audioplayer.databinding.ActivityAudioPlayerBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class AudioPlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAudioPlayerBinding
    private val viewModel: AudioPlayerViewModel by viewModels()
    private lateinit var lyricsAdapter: LyricsAdapter

    private val lrcParser = LrcParser()
    private var lyricsLines = emptyList<LrcLine>()

    companion object {
        private const val EXTRA_AUDIO_URI = "extra_audio_uri"
        private const val EXTRA_AUDIO_TITLE = "extra_audio_title"
        private const val EXTRA_AUDIO_ARTIST = "extra_audio_artist"
        private const val CHANNEL_ID = "audio_playback"

        fun newIntent(
            context: Context,
            audioUri: Uri,
            title: String,
            artist: String
        ): Intent {
            return Intent(context, AudioPlayerActivity::class.java).apply {
                putExtra(EXTRA_AUDIO_URI, audioUri)
                putExtra(EXTRA_AUDIO_TITLE, title)
                putExtra(EXTRA_AUDIO_ARTIST, artist)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        createNotificationChannel()
        initUi()
        initObservers()
        loadLyricsIfExists()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.audio_playback_channel_name),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = getString(R.string.audio_playback_channel_description)
            }
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun initUi() {
        binding.btnBack.setOnClickListener {
            onBackPressed()
        }

        binding.btnPlayPause.setOnClickListener {
            viewModel.playPauseToggle()
        }

        binding.btnRewind.setOnClickListener {
            viewModel.rewind()
        }

        binding.btnFastForward.setOnClickListener {
            viewModel.fastForward()
        }

        binding.btnNext.setOnClickListener {
            viewModel.next()
        }

        binding.btnPrevious.setOnClickListener {
            viewModel.previous()
        }

        binding.btnToggleLyrics.setOnClickListener {
            viewModel.toggleLyricsVisibility()
        }

        binding.btnMoreOptions.setOnClickListener {
            showMoreOptions()
        }

        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    val duration = viewModel.duration.value
                    val position = (duration * progress / 100f).toLong()
                    viewModel.seekTo(position)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        lyricsAdapter = LyricsAdapter()
        binding.lyricsRecyclerView.adapter = lyricsAdapter
        binding.lyricsRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun initObservers() {
        viewModel.uiState.observe(this) { uiState ->
            updateUi(uiState)
        }

        viewModel.isLyricsVisible.observe(this) { visible ->
            binding.lyricsContainer.visibility = if (visible) android.view.View.VISIBLE else android.view.View.GONE
        }

        viewModel.currentPosition.observe(this) { position ->
            updateCurrentLyrics(position)
        }
    }

    private fun updateUi(uiState: AudioPlayerUiState) {
        updatePlayPauseButton(uiState.playerState)
        updateDuration(uiState.currentPosition, uiState.duration)
        binding.trackTitle.text = uiState.currentTitle
        binding.trackArtist.text = uiState.currentArtist
    }

    private fun updatePlayPauseButton(state: AudioPlayerState) {
        when (state) {
            AudioPlayerState.Playing -> {
                binding.btnPlayPause.setImageResource(R.drawable.ic_pause_circle)
            }
            else -> {
                binding.btnPlayPause.setImageResource(R.drawable.ic_play_circle)
            }
        }
    }

    private fun updateDuration(currentPosition: Long, duration: Long) {
        binding.seekBar.max = if (duration > 0) duration.toInt() else 100
        binding.seekBar.progress = currentPosition.toInt()

        binding.currentTime.text = formatTime(currentPosition)
        binding.totalTime.text = formatTime(duration)
    }

    private fun formatTime(ms: Long): String {
        val totalSeconds = (ms / 1000).toInt()
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
    }

    private fun loadLyricsIfExists() {
        val audioUri = viewModel.audioPlayer.currentMediaUri.value ?: return
        val audioPath = audioUri.path ?: return
        val audioFile = File(audioPath)
        val parentDir = audioFile.parentFile ?: return
        val fileNameWithoutExt = audioFile.nameWithoutExtension
        val lrcFile = File(parentDir, "$fileNameWithoutExt.lrc")

        if (lrcFile.exists()) {
            GlobalScope.launch {
                lyricsLines = lrcParser.parse(lrcFile)
                runOnUiThread {
                    lyricsAdapter.updateLines(lyricsLines)
                }
            }
        }
    }

    private fun updateCurrentLyrics(position: Long) {
        if (lyricsLines.isEmpty()) return
        val index = lrcParser.getCurrentLineIndex(lyricsLines, position)
        lyricsAdapter.setCurrentLine(index)
        if (index >= 0) {
            binding.lyricsRecyclerView.smoothScrollToPosition(index)
        }
    }

    private fun showMoreOptions() {
        val speeds = arrayOf("0.25x", "0.5x", "0.75x", "1.0x", "1.25x", "1.5x", "2.0x")
        val speedValues = floatArrayOf(0.25f, 0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f)
        val currentSpeed = viewModel.currentSpeed.value
        val selectedIndex = speedValues.indexOfFirst { it == currentSpeed }.coerceAtLeast(0)

        AlertDialog.Builder(this)
            .setTitle(getString(R.string.playback_speed))
            .setSingleChoiceItems(speeds, selectedIndex) { dialog, which ->
                viewModel.setPlaybackSpeed(speedValues[which])
                dialog.dismiss()
            }
            .setNegativeButton(getString(android.R.string.cancel), null)
            .show()
    }
}
