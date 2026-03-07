package com.shen.mediaplayer.feature.playbackhistory.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.shen.mediaplayer.core.common.model.PlaybackHistoryItem
import com.shen.mediaplayer.core.common.model.MediaFile
import com.shen.mediaplayer.feature.playbackhistory.databinding.ItemPlaybackHistoryBinding
import com.shen.mediaplayer.utils.image.ImageLoader
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PlaybackHistoryAdapter(
    private val onItemClick: (MediaFile) -> Unit,
    private val onMoreClick: (PlaybackHistoryItem) -> Unit
) : ListAdapter<PlaybackHistoryItem, PlaybackHistoryAdapter.PlaybackHistoryViewHolder>(DiffCallback()) {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaybackHistoryViewHolder {
        val binding = ItemPlaybackHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PlaybackHistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlaybackHistoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PlaybackHistoryViewHolder(
        private val binding: ItemPlaybackHistoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(position).mediaFile)
                }
            }
            binding.ivMore.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onMoreClick(getItem(position))
                }
            }
        }

        fun bind(item: PlaybackHistoryItem) {
            val mediaFile = item.mediaFile
            binding.tvTitle.text = mediaFile.title
            
            val type = when {
                mediaFile.isVideo -> "视频"
                mediaFile.isAudio -> "音频"
                mediaFile.isImage -> "图片"
                else -> "文件"
            }
            binding.tvType.text = type

            binding.tvLastPlayed.text = "最近播放: ${dateFormat.format(Date(item.lastPlayedTime))}"

            val iconRes = when {
                mediaFile.isVideo -> {
                    ImageLoader.loadImage(binding.ivThumb, mediaFile.uri)
                    return
                }
                mediaFile.isAudio -> R.drawable.ic_music
                mediaFile.isImage -> {
                    ImageLoader.loadImage(binding.ivThumb, mediaFile.uri)
                    return
                }
                else -> R.drawable.ic_file
            }
            binding.ivThumb.setImageResource(iconRes)
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<PlaybackHistoryItem>() {
        override fun areItemsTheSame(oldItem: PlaybackHistoryItem, newItem: PlaybackHistoryItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: PlaybackHistoryItem, newItem: PlaybackHistoryItem): Boolean {
            return oldItem == newItem
        }
    }
}
