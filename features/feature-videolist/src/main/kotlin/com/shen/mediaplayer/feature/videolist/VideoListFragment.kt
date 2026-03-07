package com.shen.mediaplayer.feature.videolist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.shen.mediaplayer.core.common.model.MediaFile
import com.shen.mediaplayer.core.ui.base.BaseFragment
import com.shen.mediaplayer.feature.videolist.adapter.VideoListAdapter
import com.shen.mediaplayer.feature.videolist.databinding.FragmentVideoListBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VideoListFragment : BaseFragment<FragmentVideoListBinding>() {

    private val viewModel: VideoListViewModel by viewModels()
    private lateinit var adapter: VideoListAdapter

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentVideoListBinding {
        return FragmentVideoListBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObservers()
        viewModel.loadVideoList()
    }

    private fun setupRecyclerView() {
        adapter = VideoListAdapter(
            onItemClick = { mediaFile ->
                // Navigate to video player
                // TODO: Implement navigation
            },
            onItemLongClick = { mediaFile ->
                // Show context menu with options
                // TODO: Implement
            }
        )

        binding.rvVideoList.layoutManager = GridLayoutManager(context, 2)
        binding.rvVideoList.adapter = adapter

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refresh()
        }
    }

    private fun setupObservers() {
        viewModel.videoList.observe(viewLifecycleOwner) { videos ->
            adapter.submitList(videos)
            binding.emptyState.visibility = if (videos.isEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.swipeRefresh.isRefreshing = isLoading
        }
    }
}
