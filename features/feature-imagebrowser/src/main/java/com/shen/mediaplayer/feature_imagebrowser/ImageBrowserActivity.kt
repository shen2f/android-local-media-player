package com.shen.mediaplayer.feature_imagebrowser

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.adapter.FragmentViewHolder
import androidx.viewpager2.widget.ViewPager2
import coil.load
import com.github.chrisbanes.photoview.PhotoView
import com.shen.mediaplayer.core_common.R
import dagger.hilt.android.AndroidEntryPoint
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import com.shen.mediaplayer.feature_imagebrowser.databinding.ActivityImageBrowserBinding

@AndroidEntryPoint
class ImageBrowserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityImageBrowserBinding
    private val viewModel: ImageBrowserViewModel by viewModels()
    private lateinit var adapter: ImagePagerAdapter

    companion object {
        private const val EXTRA_IMAGE_URIS = "extra_image_uris"
        private const val EXTRA_INITIAL_POSITION = "extra_initial_position"

        fun newIntent(
            context: Context,
            uris: List<Uri>,
            initialPosition: Int
        ): Intent {
            return Intent(context, ImageBrowserActivity::class.java).apply {
                putParcelableArrayListExtra(EXTRA_IMAGE_URIS, ArrayList(uris))
                putExtra(EXTRA_INITIAL_POSITION, initialPosition)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageBrowserBinding.inflate(layoutInflater)
        setContentView(binding)

        initData()
        initUi()
        initObservers()
    }

    private fun initData() {
        val uris = intent.getParcelableArrayListExtra<Uri>(EXTRA_IMAGE_URIS) ?: emptyList()
        val initialPosition = intent.getIntExtra(EXTRA_INITIAL_POSITION, 0)
        viewModel.setImageList(uris, initialPosition)
    }

    private fun initUi() {
        binding.btnBack.setOnClickListener {
            onBackPressed()
        }

        adapter = ImagePagerAdapter()
        binding.viewPager.adapter = adapter

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateTitle(position)
            }
        })
    }

    private fun initObservers() {
        viewModel.imageUris.observe(this) { uris ->
            adapter.notifyDataSetChanged()
        }

        viewModel.currentPosition.observe(this) { position ->
            binding.viewPager.setCurrentItem(position, false)
            updateTitle(position)
        }
    }

    private fun updateTitle(position: Int) {
        val total = viewModel.imageUris.value.size
        binding.imageCounter.text = "${position + 1} / $total"
    }

    inner class ImagePagerAdapter : RecyclerView.Adapter<ImagePagerAdapter.ImageViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_image_pager, parent, false)
            return ImageViewHolder(view)
        }

        override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
            val uri = viewModel.imageUris.value[position]
            holder.bind(uri)
        }

        override fun getItemCount(): Int = viewModel.imageUris.value.size

        inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val photoView: PhotoView = itemView.findViewById(R.id.photoView)

            fun bind(uri: Uri) {
                photoView.load(uri) {
                    placeholder(R.drawable.ic_image_placeholder)
                    error(R.drawable.ic_broken_image)
                    crossfade(true)
                }

                photoView.setOnPhotoTapListener { view, x, y ->
                    toggleSystemUi()
                }
            }
        }
    }

    private fun toggleSystemUi() {
        val uiOptions = (window.decorView.systemUiVisibility
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)

        val isFullScreen = (uiOptions and View.SYSTEM_UI_FLAG_FULLSCREEN) != 0
        if (isFullScreen) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
            binding.topBar.visibility = View.VISIBLE
        } else {
            window.decorView.systemUiVisibility = uiOptions
            binding.topBar.visibility = View.GONE
        }
    }
}
