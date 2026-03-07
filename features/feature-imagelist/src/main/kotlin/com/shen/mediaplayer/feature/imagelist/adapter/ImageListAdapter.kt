package com.shen.mediaplayer.feature.imagelist.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.shen.mediaplayer.core.common.model.MediaFile
import com.shen.mediaplayer.feature.imagelist.databinding.ItemImageBinding
import com.shen.mediaplayer.utils.image.ImageLoader

class ImageListAdapter(
    private val onItemClick: (MediaFile) -> Unit
) : ListAdapter<MediaFile, ImageListAdapter.ImageViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ItemImageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ImageViewHolder(
        private val binding: ItemImageBinding
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
            ImageLoader.loadImage(
                binding.ivImage,
                mediaFile.uri,
                cornerRadius = 4f
            )
            binding.tvFileName.text = mediaFile.displayName
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
