package com.shen.mediaplayer.feature.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.shen.mediaplayer.core.common.model.MediaFile
import com.shen.mediaplayer.core.ui.base.BaseFragment
import com.shen.mediaplayer.feature.search.adapter.SearchResultAdapter
import com.shen.mediaplayer.feature.search.databinding.FragmentSearchBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : BaseFragment<FragmentSearchBinding>() {

    private val viewModel: SearchViewModel by viewModels()
    private lateinit var adapter: SearchResultAdapter
    private var searchJob: Job? = null

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSearchBinding {
        return FragmentSearchBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupSearchInput()
        setupRecyclerView()
        setupObservers()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupSearchInput() {
        binding.etQuery.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim()
                searchJob?.cancel()
                searchJob = MainScope().launch {
                    delay(300) // Debounce
                    viewModel.search(query)
                }
            }
        })
    }

    private fun setupRecyclerView() {
        adapter = SearchResultAdapter { mediaFile ->
            onResultClick(mediaFile)
        }
        binding.rvSearchResults.layoutManager = LinearLayoutManager(context)
        binding.rvSearchResults.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.searchResults.observe(viewLifecycleOwner) { results ->
            adapter.submitList(results)
            if (results.isEmpty()) {
                binding.tvEmptyState.visibility = View.VISIBLE
                binding.tvEmptyState.text = if (binding.etQuery.text.isNullOrBlank()) {
                    "输入关键词开始搜索"
                } else {
                    "未找到匹配的媒体文件"
                }
            } else {
                binding.tvEmptyState.visibility = View.GONE
            }
        }
    }

    private fun onResultClick(mediaFile: MediaFile) {
        // TODO: Open the media file based on type
    }
}
