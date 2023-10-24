package com.mymusic.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kt_media.databinding.ItemImageBinding


class ImageAdapter: RecyclerView.Adapter<ImageViewHolder>() {

    private val list: ArrayList<String> = arrayListOf()

    fun submit(list: List<String>) {

        this.list.clear()
        this.list.addAll(list)

        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {

        val binding =
            ItemImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val item = list[position]
        Glide.with(holder.binding.ivIi).load(item)
            .into(holder.binding.ivIi)
        holder.binding.tvPositionIi.text=(position+1).toString()+"/"+list.size
    }
}

class ImageViewHolder(val binding: ItemImageBinding) :
    RecyclerView.ViewHolder(binding.root)


