package com.mymusic.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kt_media.databinding.ItemSongBinding
import com.kt_media.domain.entities.Song


class SongAdapter (
    private val onClick:(Int)-> Unit
) : RecyclerView.Adapter<SongViewHolder>() {

    private val list: ArrayList<Song> = arrayListOf()

    fun submit(list: List<Song>) {

        this.list.clear()
        this.list.addAll(list)

        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {

        return list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {

        val binding =
            ItemSongBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return SongViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val item = list[position]
        Glide.with(holder.binding.cirIvSi).load(item.image)
            .into(holder.binding.cirIvSi)
        holder.binding.tvSongNameSi.text = item.name
        holder.binding.layoutItemArtist.setOnClickListener {
            onClick(position)
        }

    }
}

class SongViewHolder(val binding: ItemSongBinding) :
    RecyclerView.ViewHolder(binding.root)


