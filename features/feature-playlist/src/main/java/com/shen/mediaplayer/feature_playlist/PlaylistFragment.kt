package com.shen.mediaplayer.feature_playlist

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.shen.mediaplayer.core.common.model.MediaFile
import com.shen.mediaplayer.core.ui.base.BaseFragment
import com.shen.mediaplayer.feature_playlist.R
import com.shen.mediaplayer.feature_playlist.adapter.PlaylistAdapter
import com.shen.mediaplayer.feature_playlist.adapter.PlaylistEntryAdapter
import com.shen.mediaplayer.feature_playlist.databinding.FragmentPlaylistBinding
import com.shen.mediaplayer.feature_playlist.entity.PlaylistEntity
import com.shen.mediaplayer.feature_playlist.entity.PlaylistEntryEntity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PlaylistFragment : BaseFragment<FragmentPlaylistBinding>() {

    private val viewModel: PlaylistViewModel by viewModels()
    private lateinit var playlistAdapter: PlaylistAdapter
    private lateinit var playlistEntryAdapter: PlaylistEntryAdapter

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentPlaylistBinding {
        return FragmentPlaylistBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(binding: FragmentPlaylistBinding, savedInstanceState: Bundle?) {
        initRecyclerView()
        initObservers()
        initFab()
    }

    private fun initRecyclerView() {
        playlistAdapter = PlaylistAdapter(
            onItemClick = { playlist ->
                openPlaylistDetail(playlist)
            },
            onMoreClick = { playlist ->
                showPlaylistOptionsDialog(playlist)
            }
        )

        playlistEntryAdapter = PlaylistEntryAdapter(
            onItemClick = { mediaFile ->
                // TODO: play the song
            },
            onMoreClick = { mediaFile ->
                // TODO: show more options
            },
            onRemoveClick = { mediaFile ->
                // TODO: implement remove from playlist
            }
        )

        binding.playlistsRecyclerView.apply {
            adapter = playlistAdapter
            layoutManager = LinearLayoutManager(context)
        }

        binding.playlistEntriesRecyclerView.apply {
            adapter = playlistEntryAdapter
            layoutManager = LinearLayoutManager(context)
        }

        // Add drag and drop for reordering
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.Callback() {
            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                return if (recyclerView.adapter == playlistEntryAdapter) {
                    makeMovementFlags(ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0)
                } else {
                    0
                }
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val fromPosition = viewHolder.adapterPosition
                val toPosition = target.adapterPosition
                // TODO: implement drag and drop reordering
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}
        })
        itemTouchHelper.attachToRecyclerView(binding.playlistEntriesRecyclerView)
    }

    private fun initObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.playlists.collect { playlists ->
                    playlistAdapter.submitList(playlists)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.currentPlaylistEntries.collect { entries ->
                    playlistEntryAdapter.submitList(entries)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.currentPlaylist.collect { playlist ->
                    // update title
                    activity?.title = playlist?.name ?: getString(R.string.playlists)
                }
            }
        }
    }

    private fun initFab() {
        binding.createPlaylistFab.setOnClickListener {
            showCreatePlaylistDialog()
        }
    }

    private fun showCreatePlaylistDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_create_playlist, null)
        val nameEditText = dialogView.findViewById<EditText>(R.id.playlistNameEditText)

        AlertDialog.Builder(requireContext())
            .setTitle(R.string.create_playlist)
            .setView(dialogView)
            .setPositiveButton(R.string.create) { dialog, which ->
                val name = nameEditText.text.toString().trim()
                if (name.isNotEmpty()) {
                    viewModel.createPlaylist(name)
                } else {
                    Toast.makeText(context, R.string.playlist_name_empty, Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun showPlaylistOptionsDialog(playlist: PlaylistEntity) {
        val options = arrayOf(
            getString(R.string.edit_playlist),
            getString(R.string.delete_playlist)
        )
        AlertDialog.Builder(requireContext())
            .setTitle(playlist.name)
            .setItems(options) { dialog, which ->
                when (which) {
                    0 -> showEditPlaylistDialog(playlist)
                    1 -> showDeletePlaylistConfirmation(playlist)
                }
            }
            .show()
    }

    private fun showEditPlaylistDialog(playlist: PlaylistEntity) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_create_playlist, null)
        val nameEditText = dialogView.findViewById<EditText>(R.id.playlistNameEditText)
        val descEditText = dialogView.findViewById<EditText>(R.id.playlistDescriptionEditText)

        nameEditText.setText(playlist.name)
        descEditText.setText(playlist.description)

        AlertDialog.Builder(requireContext())
            .setTitle(R.string.edit_playlist)
            .setView(dialogView)
            .setPositiveButton(R.string.save) { dialog, which ->
                val name = nameEditText.text.toString().trim()
                val description = descEditText.text.toString().trim()
                if (name.isNotEmpty()) {
                    viewModel.updatePlaylist(playlist.copy(name = name, description = description))
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun showDeletePlaylistConfirmation(playlist: PlaylistEntity) {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.delete_playlist)
            .setMessage(getString(R.string.delete_playlist_confirmation, playlist.name))
            .setPositiveButton(R.string.delete) { dialog, which ->
                viewModel.deletePlaylist(playlist)
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun openPlaylistDetail(playlist: PlaylistEntity) {
        viewModel.loadPlaylistEntries(playlist.id)
        // Switch to detail view
        binding.playlistsContainer.visibility = View.GONE
        binding.playlistDetailContainer.visibility = View.VISIBLE
    }
}
