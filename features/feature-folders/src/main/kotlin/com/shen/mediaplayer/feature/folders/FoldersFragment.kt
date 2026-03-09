package com.shen.mediaplayer.feature.folders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.shen.mediaplayer.core.common.model.FolderItem
import com.shen.mediaplayer.core.ui.base.BaseFragment
import com.shen.mediaplayer.feature.folders.adapter.FolderAdapter
import com.shen.mediaplayer.feature.folders.databinding.FragmentFoldersBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FoldersFragment : BaseFragment<FragmentFoldersBinding>() {

    private val viewModel: FoldersViewModel by viewModels()
    private lateinit var adapter: FolderAdapter

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentFoldersBinding {
        return FragmentFoldersBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(binding: FragmentFoldersBinding, savedInstanceState: Bundle?) {
        setupToolbar()
        setupRecyclerView()
        setupObservers()

        if (savedInstanceState == null) {
            viewModel.loadFolder(null)
        }
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        adapter = FolderAdapter(
            onItemClick = { folderItem ->
                onItemClick(folderItem)
            },
            onMoreClick = { folderItem ->
                onMoreClick(folderItem)
            }
        )

        binding.rvFolders.layoutManager = LinearLayoutManager(context)
        binding.rvFolders.adapter = adapter

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refresh()
        }
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.folderItems.collect { items ->
                        adapter.submitList(items)
                        binding.emptyState.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
                    }
                }
                launch {
                    viewModel.currentTitle.collect { title ->
                        binding.toolbar.title = title
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

    private fun onItemClick(folderItem: FolderItem) {
        // All items in this list are folders
        viewModel.loadFolder(folderItem.path)
    }

    private fun onMoreClick(folderItem: FolderItem) {
        // TODO: Show context menu
    }

    fun onBackPressed(): Boolean {
        return if (viewModel.currentPath.value != null) {
            viewModel.navigateBack()
            true
        } else {
            false
        }
    }
}
