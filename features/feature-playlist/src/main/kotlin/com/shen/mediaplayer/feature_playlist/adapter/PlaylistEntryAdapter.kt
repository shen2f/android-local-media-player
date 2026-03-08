package com.shen.mediaplayer.feature_playlist.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.shen.mediaplayer.core.common.model.MediaFile
import com.shen.mediaplayer.feature_playlist.databinding.ItemPlaylistEntryBinding
import java.util.concurrent.TimeUnit

class PlaylistEntryAdapter(
    private val onItemClick: (MediaFile) -> Unit,
    private val onMoreClick: (MediaFile) -> Unit,
    private val onRemoveClick: ((MediaFile) -> Unit)? = null
) : ListAdapter<MediaFile, PlaylistEntryAdapter.PlaylistEntryViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistEntryViewHolder {
        val binding = ItemPlaylistEntryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PlaylistEntryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlaylistEntryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PlaylistEntryViewHolder(
        private val binding: ItemPlaylistEntryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(position))
                }
            }
            binding.ivMore.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onMoreClick(getItem(position))
                }
            }
            binding.ivRemove.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onRemoveClick?.invoke(getItem(position))
                }
            }
        }

        fun bind(mediaFile: MediaFile) {
            binding.tvTitle.text = mediaFile.fileName
            binding.tvDuration.text = formatDuration(mediaFile.duration)
            // TODO: Load thumbnail
        }

        private fun formatDuration(durationMs: Long): String {
            val minutes = TimeUnit.MILLISECONDS.toMinutes(durationMs)
            val seconds = TimeUnit.MILLISECONDS.toSeconds(durationMs) - TimeUnit.MINUTES.toSeconds(minutes)
            return String.format("%d:%02d", minutes, seconds)
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<MediaFile>() {
        override fun areItemsTheSame(oldItem: MediaFile, newItem: MediaFile): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: MediaFile, newItem: MediaFile): Boolean {
            return oldItem == newItem
        }
    }
}
