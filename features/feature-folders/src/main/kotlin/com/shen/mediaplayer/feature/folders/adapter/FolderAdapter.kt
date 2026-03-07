package com.shen.mediaplayer.feature.folders.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.shen.mediaplayer.core.common.model.FolderItem
import com.shen.mediaplayer.feature.folders.databinding.ItemFolderBinding

class FolderAdapter(
    private val onItemClick: (FolderItem) -> Unit,
    private val onMoreClick: (FolderItem) -> Unit
) : ListAdapter<FolderItem, FolderAdapter.FolderViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderViewHolder {
        val binding = ItemFolderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FolderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FolderViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class FolderViewHolder(
        private val binding: ItemFolderBinding
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
        }

        fun bind(folderItem: FolderItem) {
            binding.tvName.text = folderItem.name
            val countText = buildString {
                if (folderItem.isFolder) {
                    append("${folderItem.mediaCount} 个媒体文件")
                } else {
                    append(folderItem.mimeType ?: "未知类型")
                }
            }
            binding.tvCount.text = countText
            val iconRes = if (folderItem.isFolder) {
                R.drawable.ic_folder
            } else {
                when {
                    folderItem.isVideo -> R.drawable.ic_video
                    folderItem.isAudio -> R.drawable.ic_music
                    folderItem.isImage -> R.drawable.ic_image
                    else -> R.drawable.ic_file
                }
            }
            binding.ivIcon.setImageResource(iconRes)
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<FolderItem>() {
        override fun areItemsTheSame(oldItem: FolderItem, newItem: FolderItem): Boolean {
            return oldItem.path == newItem.path
        }

        override fun areContentsTheSame(oldItem: FolderItem, newItem: FolderItem): Boolean {
            return oldItem == newItem
        }
    }
}
