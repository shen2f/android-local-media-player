package com.shen.mediaplayer.feature.playbackhistory

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.shen.mediaplayer.core.common.model.MediaFile
import com.shen.mediaplayer.core.common.model.PlaybackHistoryItem
import com.shen.mediaplayer.core.ui.base.BaseFragment
import com.shen.mediaplayer.feature.playbackhistory.adapter.PlaybackHistoryAdapter
import com.shen.mediaplayer.feature.playbackhistory.databinding.FragmentPlaybackHistoryBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlaybackHistoryFragment : BaseFragment<FragmentPlaybackHistoryBinding>() {

    private val viewModel: PlaybackHistoryViewModel by viewModels()
    private lateinit var adapter: PlaybackHistoryAdapter

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPlaybackHistoryBinding {
        return FragmentPlaybackHistoryBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupFab()
        setupObservers()
        viewModel.loadHistory()
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh()
    }

    private fun setupRecyclerView() {
        adapter = PlaybackHistoryAdapter(
            onItemClick = { mediaFile ->
                onItemClick(mediaFile)
            },
            onMoreClick = { historyItem ->
                onMoreClick(historyItem)
            }
        )

        binding.rvHistory.layoutManager = LinearLayoutManager(context)
        binding.rvHistory.adapter = adapter

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refresh()
        }
    }

    private fun setupFab() {
        binding.fabClearHistory.setOnClickListener {
            showClearHistoryDialog()
        }
    }

    private fun setupObservers() {
        viewModel.history.observe(viewLifecycleOwner) { history ->
            adapter.submitList(history)
            binding.emptyState.visibility = if (history.isEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.swipeRefresh.isRefreshing = isLoading
        }
    }

    private fun onItemClick(mediaFile: MediaFile) {
        // TODO: Open media file based on type
    }

    private fun onMoreClick(historyItem: PlaybackHistoryItem) {
        AlertDialog.Builder(requireContext())
            .setTitle("操作选项")
            .setItems(arrayOf("删除历史记录")) { _, _ ->
                viewModel.removeHistory(historyItem.id)
            }
            .show()
    }

    private fun showClearHistoryDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("清空播放历史")
            .setMessage("确定要清空所有播放历史吗？此操作不可撤销。")
            .setPositiveButton("确定") { _, _ ->
                viewModel.clearHistory()
            }
            .setNegativeButton("取消", null)
            .show()
    }
}
