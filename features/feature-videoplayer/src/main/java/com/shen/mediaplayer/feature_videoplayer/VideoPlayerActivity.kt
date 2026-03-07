package com.shen.mediaplayer.feature_videoplayer

import android.app.PictureInPictureParams
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.util.Rational
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.shen.mediaplayer.core.common.getParcelableExtraCompat
import com.shen.mediaplayer.core_common.R
import dagger.hilt.android.AndroidEntryPoint
import android.net.Uri
import android.os.Environment
import android.view.View.OnTouchListener
import android.widget.PopupMenu
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.shen.mediaplayer.feature_videoplayer.databinding.ActivityVideoPlayerBinding
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class VideoPlayerActivity : AppCompatActivity(), VideoPlayerGestureDetector.GestureListener {

    private lateinit var binding: ActivityVideoPlayerBinding
    private val viewModel: VideoPlayerViewModel by viewModels()

    private lateinit var gestureDetector: VideoPlayerGestureDetector
    private var isFullScreen = true
    private val fastRewindDuration = 10000 // 10 seconds

    companion object {
        private const val EXTRA_VIDEO_URI = "extra_video_uri"

        fun newIntent(context: Context, videoUri: Uri): Intent {
            return Intent(context, VideoPlayerActivity::class.java).apply {
                putExtra(EXTRA_VIDEO_URI, videoUri)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initGestureDetector()
        initUi()
        initObservers()
        initPlayerView()

        // Set brightness to system current brightness initially
        val brightness = (window.attributes.screenBrightness)
        if (brightness >= 0) {
            viewModel.updateBrightness(brightness - 0f)
        }
    }

    private fun initGestureDetector() {
        gestureDetector = VideoPlayerGestureDetector(this, this)
    }

    private fun initUi() {
        binding.playerView.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
        }

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }

        binding.btnPlayPause.setOnClickListener {
            viewModel.playPauseToggle()
        }

        binding.btnFastForward.setOnClickListener {
            viewModel.fastForward()
        }

        binding.btnRewind.setOnClickListener {
            viewModel.rewind()
        }

        binding.btnMoreOptions.setOnClickListener {
            showMoreOptionsMenu()
        }

        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    viewModel.updateSeekProgress(progress / 100f)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                viewModel.startSeeking()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                viewModel.finishSeeking()
            }
        })

        binding.btnFullScreenToggle.setOnClickListener {
            toggleFullScreen()
        }
    }

    private fun initPlayerView() {
        viewModel.videoPlayer.getPlayer()?.let { player ->
            binding.playerView.player = player
        }
    }

    private fun initObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    updateUi(uiState)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isBrightnessChanging.collect { changing ->
                    binding.brightnessOverlay.visibility = if (changing) View.VISIBLE else View.GONE
                    if (changing) {
                        binding.brightnessValue.text = "${(viewModel.currentBrightness.value * 100).toInt()}%"
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.currentBrightness.collect { brightness ->
                    updateBrightness(brightness)
                    if (viewModel.isBrightnessChanging.value) {
                        binding.brightnessValue.text = "${(brightness * 100).toInt()}%"
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isVolumeChanging.collect { changing ->
                    binding.volumeOverlay.visibility = if (changing) View.VISIBLE else View.GONE
                    if (changing) {
                        binding.volumeValue.text = "${(viewModel.currentVolume.value * 100).toInt()}%"
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.currentVolume.collect { volume ->
                    if (viewModel.isVolumeChanging.value) {
                        binding.volumeValue.text = "${(volume * 100).toInt()}%"
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isSeeking.collect { seeking ->
                    binding.seekingOverlay.visibility = if (seeking) View.VISIBLE else View.GONE
                    if (seeking) {
                        val duration = viewModel.duration.value
                        val currentSeekMs = (duration * viewModel.seekProgress.value).toLong()
                        updateSeekingOverlay(currentSeekMs, duration)
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.seekProgress.collect { progress ->
                    if (viewModel.isSeeking.value) {
                        val duration = viewModel.duration.value
                        val currentSeekMs = (duration * progress).toLong()
                        updateSeekingOverlay(currentSeekMs, duration)
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.scaleMode.collect { scaleMode ->
                    updateScaleMode(scaleMode)
                }
            }
        }
    }

    private fun updateUi(uiState: VideoPlayerUiState) {
        val visibility = if (uiState.isControlsVisible) View.VISIBLE else View.GONE
        binding.topBar.visibility = visibility
        binding.bottomControls.visibility = visibility

        updatePlayPauseButton(uiState.playerState)
        updateDuration(uiState.currentPosition, uiState.duration)
        updateBufferedPosition(uiState.bufferedPosition, uiState.duration)
    }

    private fun updatePlayPauseButton(state: PlayerState) {
        when (state) {
            PlayerState.Playing -> {
                binding.btnPlayPause.setImageResource(R.drawable.ic_pause)
            }
            else -> {
                binding.btnPlayPause.setImageResource(R.drawable.ic_play)
            }
        }
    }

    private fun updateDuration(currentPosition: Long, duration: Long) {
        binding.seekBar.max = if (duration > 0) duration.toInt() else 100
        if (!viewModel.isSeeking.value) {
            binding.seekBar.progress = currentPosition.toInt()
        }

        binding.currentTime.text = formatTime(currentPosition)
        binding.totalTime.text = formatTime(duration)
    }

    private fun updateBufferedPosition(bufferedPosition: Long, duration: Long) {
        binding.seekBar.secondaryProgress = if (duration > 0) bufferedPosition.toInt() else 0
    }

    private fun updateSeekingOverlay(currentSeekMs: Long, duration: Long) {
        val current = formatTime(currentSeekMs)
        val total = formatTime(duration)
        binding.seekingTime.text = "$current / $total"
    }

    private fun updateBrightness(brightness: Float) {
        val layoutParams = window.attributes
        layoutParams.screenBrightness = brightness
        window.attributes = layoutParams
    }

    private fun updateScaleMode(scaleMode: VideoPlayerViewModel.ScaleMode) {
        when (scaleMode) {
            VideoPlayerViewModel.ScaleMode.FIT -> {
                binding.playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
            }
            VideoPlayerViewModel.ScaleMode.FILL -> {
                binding.playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
            }
            VideoPlayerViewModel.ScaleMode.ZOOM -> {
                binding.playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
            }
        }
    }

    private fun formatTime(ms: Long): String {
        val totalSeconds = (ms / 1000).toInt()
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
    }

    private fun toggleFullScreen() {
        isFullScreen = !isFullScreen
        if (isFullScreen) {
            window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            )
            supportActionBar?.hide()
            binding.btnFullScreenToggle.setImageResource(R.drawable.ic_fullscreen_exit)
        } else {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
            supportActionBar?.show()
            binding.btnFullScreenToggle.setImageResource(R.drawable.ic_fullscreen)
        }
    }

    private fun showMoreOptionsMenu() {
        val popup = PopupMenu(this, binding.btnMoreOptions)
        popup.menuInflater.inflate(R.menu.video_player_menu, popup.menu)

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_speed -> showSpeedDialog()
                R.id.menu_scale -> {
                    viewModel.cycleScaleMode()
                    true
                }
                R.id.menu_screenshot -> captureScreenshot()
                R.id.menu_pip -> enterPictureInPicture()
                else -> false
            }
        }
        popup.show()
    }

    private fun showSpeedDialog() {
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

    private fun captureScreenshot() {
        val bitmap = viewModel.screenshot() ?: return
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "VID_Screenshot_$timeStamp"

        val storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val imageFile = File(storageDir, "$imageFileName.jpg")

        try {
            val outputStream = FileOutputStream(imageFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            outputStream.flush()
            outputStream.close()

            Toast.makeText(
                this,
                getString(R.string.screenshot_saved, imageFile.absolutePath),
                Toast.LENGTH_SHORT
            ).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, R.string.screenshot_failed, Toast.LENGTH_SHORT).show()
        }

        bitmap.recycle()
    }

    private fun enterPictureInPicture(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val aspectRatio = Rational(16, 9)
            val params = PictureInPictureParams.Builder()
                .setAspectRatio(aspectRatio)
                .build()
            enterPictureInPictureMode(params)
            return true
        }
        return false
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val playerState = viewModel.playerState.value
            if (playerState == PlayerState.Playing) {
                enterPictureInPicture()
            }
        }
    }

    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: android.content.res.Configuration
    ) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        if (isInPictureInPictureMode) {
            binding.topBar.visibility = View.GONE
            binding.bottomControls.visibility = View.GONE
            binding.brightnessOverlay.visibility = View.GONE
            binding.volumeOverlay.visibility = View.GONE
            binding.seekingOverlay.visibility = View.GONE
        } else {
            // Restore UI based on current state
            val controlsVisible = viewModel.isControlsVisible.value
            binding.topBar.visibility = if (controlsVisible) View.VISIBLE else View.GONE
            binding.bottomControls.visibility = if (controlsVisible) View.VISIBLE else View.GONE
        }
    }

    override fun onSingleTap(): Boolean {
        viewModel.toggleControlsVisibility()
        return true
    }

    override fun onDoubleTap(): Boolean {
        val touchX = lastTouchX
        val screenWidth = resources.displayMetrics.widthPixels
        if (touchX < screenWidth / 3) {
            viewModel.rewind()
        } else if (touchX > 2 * screenWidth / 3) {
            viewModel.fastForward()
        } else {
            viewModel.playPauseToggle()
        }
        return true
    }

    override fun onBrightnessChange(delta: Float): Boolean {
        viewModel.updateBrightness(delta)
        return true
    }

    override fun onVolumeChange(delta: Float): Boolean {
        viewModel.updateVolume(delta)
        return true
    }

    override fun onSeekChange(deltaX: Float, screenWidth: Int): Boolean {
        val progress = deltaX / screenWidth
        viewModel.updateSeekProgress(progress)
        return true
    }

    override fun onSeekStart(): Boolean {
        viewModel.startSeeking()
        return true
    }

    override fun onSeekEnd(): Boolean {
        viewModel.finishSeeking()
        return true
    }

    override fun onFling(velocityX: Float, velocityY: Float): Boolean {
        return false
    }

    private var lastTouchX = 0f

    override fun onTouchEvent(event: MotionEvent): Boolean {
        lastTouchX = event.x
        return super.onTouchEvent(event)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onCleared()
    }
}
