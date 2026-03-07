package com.shen.mediaplayer.feature.audiolist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.shen.mediaplayer.core.common.model.MediaFile
import com.shen.mediaplayer.core.ui.base.BaseFragment
import com.shen.mediaplayer.feature.audiolist.adapter.AudioListAdapter
import com.shen.mediaplayer.feature.audiolist.databinding.FragmentAudioListBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AudioListFragment : BaseFragment<FragmentAudioListBinding>() {

    private val viewModel: AudioListViewModel by viewModels()
    private lateinit var adapter: AudioListAdapter

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAudioListBinding {
        return FragmentAudioListBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObservers()
        viewModel.loadAudioList()
    }

    private fun setupRecyclerView() {
        adapter = AudioListAdapter(
            onItemClick = { mediaFile ->
                onAudioClick(mediaFile)
            },
            onMoreClick = { mediaFile ->
                onMoreClick(mediaFile)
            }
        )

        binding.rvAudioList.layoutManager = LinearLayoutManager(context)
        binding.rvAudioList.adapter = adapter

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refresh()
        }
    }

    private fun setupObservers() {
        viewModel.audioList.observe(viewLifecycleOwner) { audios ->
            adapter.submitList(audios)
            binding.emptyState.visibility = if (audios.isEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.swipeRefresh.isRefreshing = isLoading
        }
    }

    private fun onAudioClick(mediaFile: MediaFile) {
        // TODO: Navigate to audio player
    }

    private fun onMoreClick(mediaFile: MediaFile) {
        // TODO: Show context menu with file options
    }
}
