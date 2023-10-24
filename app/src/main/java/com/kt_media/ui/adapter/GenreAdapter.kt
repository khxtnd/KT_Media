package com.mymusic.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.kt_media.databinding.ItemGenreBinding
import com.kt_media.domain.entities.Genre


class GenreAdapter (
    private val onClick:(Int)-> Unit
): RecyclerView.Adapter<GenreViewHolder>() {

    private val list: ArrayList<Genre> = arrayListOf()

    fun submit(list: List<Genre>) {

        this.list.clear()
        this.list.addAll(list)

        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {

        return list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenreViewHolder {

        val binding =
            ItemGenreBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return GenreViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GenreViewHolder, position: Int) {
        val item = list[position]
        Glide.with(holder.binding.ivSci).load(item.image).transform(RoundedCorners(16))
            .into(holder.binding.ivSci)
        holder.binding.layoutItemGenre.setOnClickListener {
            onClick(item.id)
        }

    }
}

class GenreViewHolder(val binding: ItemGenreBinding) :
    RecyclerView.ViewHolder(binding.root)


