package com.shen.mediaplayer.feature.favorites.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.shen.mediaplayer.core.common.model.MediaFile
import com.shen.mediaplayer.feature.favorites.databinding.ItemFavoriteBinding
import com.shen.mediaplayer.utils.image.ImageLoader

class FavoritesAdapter(
    private val onItemClick: (MediaFile) -> Unit,
    private val onFavoriteClick: (MediaFile) -> Unit
) : ListAdapter<MediaFile, FavoritesAdapter.FavoriteViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val binding = ItemFavoriteBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FavoriteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class FavoriteViewHolder(
        private val binding: ItemFavoriteBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(position))
                }
            }
            binding.ivRemove.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onFavoriteClick(getItem(position))
                }
            }
        }

        fun bind(mediaFile: MediaFile) {
            binding.tvTitle.text = mediaFile.title
            
            val info = buildString {
                val type = when {
                    mediaFile.isVideo -> "视频"
                    mediaFile.isAudio -> "音频"
                    mediaFile.isImage -> "图片"
                    else -> "文件"
                }
                append(type)
                append(" • ${mediaFile.path.substringBeforeLast('/')}")
            }
            binding.tvInfo.text = info

            val iconRes = when {
                mediaFile.isVideo -> R.drawable.ic_video
                mediaFile.isAudio -> R.drawable.ic_music
                mediaFile.isImage -> {
                    ImageLoader.loadImage(binding.ivIcon, mediaFile.uri)
                    return
                }
                else -> R.drawable.ic_file
            }
            binding.ivIcon.setImageResource(iconRes)
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
