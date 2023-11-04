package com.mymusic.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kt_media.databinding.ItemAlbumBinding
import com.kt_media.domain.entities.Album


class AlbumAdapter(
    private val onClick: (Int) -> Unit
) : RecyclerView.Adapter<AlbumViewHolder>() {

    private val list: ArrayList<Album> = arrayListOf()

    fun submit(list: List<Album>) {

        this.list.clear()
        this.list.addAll(list)

        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {

        val binding =
            ItemAlbumBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return AlbumViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        val item = list[position]
        Glide.with(holder.binding.ivCii).load(item.image)
            .into(holder.binding.ivCii)
        holder.binding.tvNameCii.text = item.name
        holder.binding.layoutItemAlbum.setOnClickListener {
            onClick(item.id)
        }

    }
}

class AlbumViewHolder(val binding: ItemAlbumBinding) :
    RecyclerView.ViewHolder(binding.root)


