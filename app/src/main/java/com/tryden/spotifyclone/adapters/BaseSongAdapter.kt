package com.tryden.spotifyclone.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.tryden.spotifyclone.data.entities.Song

abstract class BaseSongAdapter(
    private val layoutId: Int
) : RecyclerView.Adapter<BaseSongAdapter.SongViewHolder>(){
    class SongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)


    /**
     *
     * Not using this class for now!
     *
     */


}