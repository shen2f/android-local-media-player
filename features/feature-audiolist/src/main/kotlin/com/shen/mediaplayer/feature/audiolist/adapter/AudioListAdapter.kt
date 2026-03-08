package com.shen.mediaplayer.feature.audiolist.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.shen.mediaplayer.core.common.model.MediaFile
import com.shen.mediaplayer.feature.audiolist.databinding.ItemAudioBinding
import com.shen.mediaplayer.feature.audiolist.databinding.ItemAudioCategoryBinding
import java.util.concurrent.TimeUnit

class AudioListAdapter(
    private val onItemClick: (MediaFile) -> Unit,
    private val onMoreClick: (MediaFile) -> Unit
) : ListAdapter<AudioListAdapter.Item, RecyclerView.ViewHolder>(DiffCallback()) {

    sealed class Item {
        data class Category(val name: String) : Item()
        data class Audio(val mediaFile: MediaFile) : Item()
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is Item.Category -> VIEW_TYPE_CATEGORY
            is Item.Audio -> VIEW_TYPE_AUDIO
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_CATEGORY -> {
                val binding = ItemAudioCategoryBinding.inflate(inflater, parent, false)
                CategoryViewHolder(binding)
            }
            else -> {
                val binding = ItemAudioBinding.inflate(inflater, parent, false)
                AudioViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is Item.Category -> (holder as CategoryViewHolder).bind(item)
            is Item.Audio -> (holder as AudioViewHolder).bind(item.mediaFile)
        }
    }

    inner class AudioViewHolder(
        private val binding: ItemAudioBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val item = getItem(bindingAdapterPosition) as Item.Audio
                onItemClick(item.mediaFile)
            }
            binding.ivMore.setOnClickListener {
                val item = getItem(bindingAdapterPosition) as Item.Audio
                onMoreClick(item.mediaFile)
            }
        }

        fun bind(mediaFile: MediaFile) {
            binding.tvTitle.text = mediaFile.fileName
            binding.tvArtistAlbum.text = mediaFile.folderPath
            binding.tvDuration.text = formatDuration(mediaFile.duration)
        }

        private fun formatDuration(durationMs: Long): String {
            val minutes = TimeUnit.MILLISECONDS.toMinutes(durationMs)
            val seconds = TimeUnit.MILLISECONDS.toSeconds(durationMs) - TimeUnit.MINUTES.toSeconds(minutes)
            return String.format("%d:%02d", minutes, seconds)
        }
    }

    inner class CategoryViewHolder(
        private val binding: ItemAudioCategoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(category: Item.Category) {
            binding.tvCategory.text = category.name
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Item>() {
        override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
            if (oldItem is Item.Category && newItem is Item.Category) {
                return oldItem.name == newItem.name
            }
            if (oldItem is Item.Audio && newItem is Item.Audio) {
                return oldItem.mediaFile.id == newItem.mediaFile.id
            }
            return false
        }

        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem == newItem
        }
    }

    companion object {
        private const val VIEW_TYPE_CATEGORY = 0
        private const val VIEW_TYPE_AUDIO = 1
    }
}
