package com.tryden.spotifyclone.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.tryden.spotifyclone.R
import com.tryden.spotifyclone.data.entities.Song
import com.tryden.spotifyclone.databinding.SwipeItemBinding
import javax.inject.Inject

class SwipeSongAdapter : RecyclerView.Adapter<SwipeSongAdapter.SongViewHolder>(){

    private val diffCallback = object : DiffUtil.ItemCallback<Song>() {
        override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem.mediaId == newItem.mediaId
        }

        override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    var songs: List<Song>
        get() = differ.currentList
        set(value) = differ.submitList(value)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val itemBinding = SwipeItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SongViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = songs[position]
        holder.bind(song)
        holder.itemView.setOnClickListener {
            onItemClickListener?.let { click ->
                click(song)
            }
        }
    }

    override fun getItemCount(): Int {
        return songs.size
    }


    inner class SongViewHolder (
        private val itemBinding: SwipeItemBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(song: Song) {
            val text = "${song.title} - ${song.subtitle}"
            itemBinding.tvPrimary.text = text
        }
    }

    private var onItemClickListener: ((Song) -> Unit)? = null

    fun setOnItemClickListener(listener: (Song) -> Unit) {
        onItemClickListener = listener
    }

}