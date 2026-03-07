package com.shen.mediaplayer.feature.folders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.shen.mediaplayer.core.common.model.FolderItem
import com.shen.mediaplayer.core.ui.base.BaseFragment
import com.shen.mediaplayer.feature.folders.adapter.FolderAdapter
import com.shen.mediaplayer.feature.folders.databinding.FragmentFoldersBinding
import dagger.hilt.android.AndroidEntryPoint

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
        viewModel.folderItems.observe(viewLifecycleOwner) { items ->
            adapter.submitList(items)
            binding.emptyState.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.currentTitle.observe(viewLifecycleOwner) { title ->
            binding.toolbar.title = title
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.swipeRefresh.isRefreshing = isLoading
        }
    }

    private fun onItemClick(folderItem: FolderItem) {
        if (folderItem.isFolder) {
            viewModel.loadFolder(folderItem.path)
        } else {
            // TODO: Open media file based on type
        }
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
