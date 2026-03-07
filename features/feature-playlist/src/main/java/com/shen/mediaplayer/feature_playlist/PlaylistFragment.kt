package com.shen.mediaplayer.feature_playlist

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.shen.mediaplayer.core_common.R
import com.shen.mediaplayer.data_local.entity.PlaylistEntity
import com.shen.mediaplayer.data_local.entity.PlaylistEntryEntity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PlaylistFragment : Fragment() {

    private lateinit var viewModel: PlaylistViewModel
    private lateinit var playlistAdapter: PlaylistAdapter
    private lateinit var playlistEntryAdapter: PlaylistEntryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_playlist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = getDefaultViewModelProviderFactory().create(PlaylistViewModel::class.java)
        initRecyclerView()
        initObservers()
        initFab()
    }

    private fun initRecyclerView() {
        playlistAdapter = PlaylistAdapter(
            onItemClick = { playlist ->
                openPlaylistDetail(playlist)
            },
            onLongClick = { playlist ->
                showPlaylistOptionsDialog(playlist)
            }
        )

        playlistEntryAdapter = PlaylistEntryAdapter(
            onItemClick = { entry ->
                // TODO: play the song
            },
            onRemoveClick = { entry ->
                viewModel.removeSongFromPlaylist(entry)
            }
        )

        requireView().findViewById<RecyclerView>(R.id.playlistsRecyclerView).apply {
            adapter = playlistAdapter
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
                val currentList = viewModel.currentPlaylistEntries.value.toMutableList()
                val item = currentList.removeAt(fromPosition)
                currentList.add(toPosition, item)
                playlistEntryAdapter.submitList(currentList)
                viewModel.updateEntrySortOrder(currentList)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}
        })
        itemTouchHelper.attachToRecyclerView(
            requireView().findViewById<RecyclerView>(R.id.playlistEntriesRecyclerView)
        )
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
        requireView().findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.createPlaylistFab).setOnClickListener {
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
        val dialogView = LayoutInflater.getLogger().inflate(R.layout.dialog_create_playlist, null)
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
        requireView().findViewById<View>(R.id.playlistsContainer).visibility = View.GONE
        requireView().findViewById<View>(R.id.playlistDetailContainer).visibility = View.VISIBLE
    }
}
