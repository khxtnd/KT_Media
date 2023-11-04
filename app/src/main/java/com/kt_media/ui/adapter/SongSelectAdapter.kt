package com.mymusic.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kt_media.databinding.ItemSongSelectBinding
import com.kt_media.domain.entities.Song


class SongSelectAdapter (
    private val onClick:(Int)-> Unit
) : RecyclerView.Adapter<SongSelectViewHolder>() {

    private val list: ArrayList<Song> = arrayListOf()
    private val listSongId: ArrayList<Int> = arrayListOf()

    fun submit(list: List<Song>, songIdList: List<Int>) {

        this.list.clear()
        this.list.addAll(list)
        this.listSongId.clear()
        this.listSongId.addAll(songIdList)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {

        return list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongSelectViewHolder {

        val binding =
            ItemSongSelectBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return SongSelectViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SongSelectViewHolder, position: Int) {
        val item = list[position]
        Glide.with(holder.binding.cirIvSsi).load(item.image)
            .into(holder.binding.cirIvSsi)
        holder.binding.tvSongNameSsi.text = item.name
        holder.binding.checkBoxSsi.isChecked = listSongId.contains(item.id)
        holder.binding.checkBoxSsi.setOnClickListener {
            onClick(item.id)
        }

    }
}

class SongSelectViewHolder(val binding: ItemSongSelectBinding) :
    RecyclerView.ViewHolder(binding.root)


