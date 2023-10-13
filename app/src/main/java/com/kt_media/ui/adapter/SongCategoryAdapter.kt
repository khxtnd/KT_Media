package com.mymusic.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.kt_media.databinding.ItemSongCategoryBinding
import com.kt_media.domain.entities.SongArtist
import com.kt_media.domain.entities.SongCategory


class SongCategoryAdapter (
    private val onClick:(SongCategory)-> Unit
): RecyclerView.Adapter<SongCategoryViewHolder>() {

    private val list: ArrayList<SongCategory> = arrayListOf()

    fun submit(list: List<SongCategory>) {

        this.list.clear()
        this.list.addAll(list)

        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {

        return list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongCategoryViewHolder {

        val binding =
            ItemSongCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return SongCategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SongCategoryViewHolder, position: Int) {
        val item = list[position]
        Glide.with(holder.binding.ivSci).load(item.image).transform(RoundedCorners(16))
            .into(holder.binding.ivSci)
        holder.binding.layoutItemSongCategory.setOnClickListener {
            onClick(item)
        }

    }
}

class SongCategoryViewHolder(val binding: ItemSongCategoryBinding) :
    RecyclerView.ViewHolder(binding.root)


