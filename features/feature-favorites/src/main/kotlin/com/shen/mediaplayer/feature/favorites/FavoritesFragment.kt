package com.shen.mediaplayer.feature.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.shen.mediaplayer.core.common.model.MediaFile
import com.shen.mediaplayer.core.ui.base.BaseFragment
import com.shen.mediaplayer.feature.favorites.adapter.FavoritesAdapter
import com.shen.mediaplayer.feature.favorites.databinding.FragmentFavoritesBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoritesFragment : BaseFragment<FragmentFavoritesBinding>() {

    private val viewModel: FavoritesViewModel by viewModels()
    private lateinit var adapter: FavoritesAdapter

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentFavoritesBinding {
        return FragmentFavoritesBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObservers()
        viewModel.loadFavorites()
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh()
    }

    private fun setupRecyclerView() {
        adapter = FavoritesAdapter(
            onItemClick = { mediaFile ->
                onItemClick(mediaFile)
            },
            onFavoriteClick = { mediaFile ->
                viewModel.removeFavorite(mediaFile)
            }
        )

        binding.rvFavorites.layoutManager = LinearLayoutManager(context)
        binding.rvFavorites.adapter = adapter

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refresh()
        }
    }

    private fun setupObservers() {
        viewModel.favorites.observe(viewLifecycleOwner) { favorites ->
            adapter.submitList(favorites)
            binding.emptyState.visibility = if (favorites.isEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.swipeRefresh.isRefreshing = isLoading
        }
    }

    private fun onItemClick(mediaFile: MediaFile) {
        // TODO: Open media file based on type
    }
}
