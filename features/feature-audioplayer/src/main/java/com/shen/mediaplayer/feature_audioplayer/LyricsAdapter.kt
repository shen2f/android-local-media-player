package com.shen.mediaplayer.feature_audioplayer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.shen.mediaplayer.core_common.R

class LyricsAdapter : RecyclerView.Adapter<LyricsAdapter.LyricsViewHolder>() {

    private var lines: List<LrcLine> = emptyList()
    private var currentLineIndex = -1

    fun updateLines(newLines: List<LrcLine>) {
        lines = newLines
        notifyDataSetChanged()
    }

    fun setCurrentLine(index: Int) {
        val oldIndex = currentLineIndex
        currentLineIndex = index
        notifyItemChanged(oldIndex)
        notifyItemChanged(index)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LyricsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_lyric_line, parent, false)
        return LyricsViewHolder(view)
    }

    override fun onBindViewHolder(holder: LyricsViewHolder, position: Int) {
        holder.bind(lines[position], position == currentLineIndex)
    }

    override fun getItemCount(): Int = lines.size

    class LyricsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView: TextView = itemView.findViewById(R.id.lyricText)

        fun bind(line: LrcLine, isCurrent: Boolean) {
            textView.text = line.text
            if (isCurrent) {
                textView.alpha = 1.0f
                textView.setTextColor(itemView.context.getColor(R.color.colorAccent))
            } else {
                textView.alpha = 0.5f
                textView.setTextColor(itemView.context.getColor(android.R.color.white))
            }
        }
    }
}
