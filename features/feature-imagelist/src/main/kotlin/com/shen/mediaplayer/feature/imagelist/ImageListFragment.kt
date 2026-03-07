package com.shen.mediaplayer.feature.imagelist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.shen.mediaplayer.core.common.model.MediaFile
import com.shen.mediaplayer.core.ui.base.BaseFragment
import com.shen.mediaplayer.feature.imagelist.adapter.ImageListAdapter
import com.shen.mediaplayer.feature.imagelist.databinding.FragmentImageListBinding
import dagger.hilt.android.AndroidEntryPoint

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
        viewModel.imageList.observe(viewLifecycleOwner) { images ->
            adapter.submitList(images)
            binding.emptyState.visibility = if (images.isEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.swipeRefresh.isRefreshing = isLoading
        }
    }

    private fun onImageClick(mediaFile: MediaFile) {
        // TODO: Navigate to image viewer
    }
}
