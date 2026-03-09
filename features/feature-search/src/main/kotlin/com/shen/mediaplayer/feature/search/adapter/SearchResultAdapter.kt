package com.shen.mediaplayer.feature.search.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.shen.mediaplayer.core.common.model.MediaFile
import com.shen.mediaplayer.feature.search.databinding.ItemSearchResultBinding

class SearchResultAdapter(
    private val onItemClick: (MediaFile) -> Unit
) : ListAdapter<MediaFile, SearchResultAdapter.SearchResultViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultViewHolder {
        val binding = ItemSearchResultBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SearchResultViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchResultViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class SearchResultViewHolder(
        private val binding: ItemSearchResultBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(position))
                }
            }
        }

        fun bind(mediaFile: MediaFile) {
            binding.tvTitle.text = mediaFile.fileName
            binding.tvPath.text = mediaFile.filePath.substringBeforeLast('/')
            
            val iconRes = when {
                mediaFile.isVideo -> android.R.drawable.ic_menu_gallery
                mediaFile.isAudio -> android.R.drawable.ic_menu_call
                mediaFile.isImage -> android.R.drawable.ic_menu_gallery
                else -> android.R.drawable.ic_menu_help
            }
            binding.ivIcon.setImageResource(iconRes)

            val typeText = buildString {
                append(if (mediaFile.isVideo) "视频" else if (mediaFile.isAudio) "音频" else "图片")
            }
            binding.tvType.text = typeText
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
