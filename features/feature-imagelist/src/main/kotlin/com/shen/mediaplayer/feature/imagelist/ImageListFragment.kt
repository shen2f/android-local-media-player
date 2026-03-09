package com.shen.mediaplayer.feature.imagelist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.shen.mediaplayer.core.common.model.MediaFile
import com.shen.mediaplayer.core.ui.base.BaseFragment
import com.shen.mediaplayer.feature.imagelist.adapter.ImageListAdapter
import com.shen.mediaplayer.feature.imagelist.databinding.FragmentImageListBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ImageListFragment : BaseFragment<FragmentImageListBinding>() {

    private val viewModel: ImageListViewModel by viewModels()
    private lateinit var adapter: ImageListAdapter

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentImageListBinding {
        return FragmentImageListBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(binding: FragmentImageListBinding, savedInstanceState: Bundle?) {
        setupRecyclerView()
        setupObservers()
        viewModel.loadImageList()
    }

    private fun setupRecyclerView() {
        val spanCount = getSpanCount()
        val layoutManager = GridLayoutManager(context, spanCount)
        adapter = ImageListAdapter { mediaFile ->
            onImageClick(mediaFile)
        }

        binding.rvImageList.layoutManager = layoutManager
        binding.rvImageList.adapter = adapter
        adapter.setHasStableIds(true)

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refresh()
        }
    }

    private fun getSpanCount(): Int {
        val displayMetrics = resources.displayMetrics
        val width = displayMetrics.widthPixels / displayMetrics.density
        return Math.max((width / 120).toInt(), 2)
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.imageList.collect { images ->
                        adapter.submitList(images)
                        binding.emptyState.visibility = if (images.isEmpty()) View.VISIBLE else View.GONE
                    }
                }
                launch {
                    viewModel.isLoading.collect { isLoading ->
                        binding.swipeRefresh.isRefreshing = isLoading
                    }
                }
            }
        }
    }

    private fun onImageClick(mediaFile: MediaFile) {
        // TODO: Navigate to image viewer
    }
}
