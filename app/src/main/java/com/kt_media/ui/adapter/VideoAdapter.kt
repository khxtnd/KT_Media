package com.mymusic.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kt_media.databinding.ItemVideoBinding
import com.kt_media.domain.entities.Video


class VideoAdapter (
    private val onClick:(Int)-> Unit
) : RecyclerView.Adapter<VideoViewHolder>() {

    private val list: ArrayList<Video> = arrayListOf()

    fun submit(list: List<Video>) {

        this.list.clear()
        this.list.addAll(list)

        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {

        return list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {

        val binding =
            ItemVideoBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return VideoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val item = list[position]
        Glide.with(holder.binding.ivVideoVi).load(item.image)
            .into(holder.binding.ivVideoVi)
        holder.binding.tvVideoNameVi.text = item.name
        holder.binding.layoutItemArtist.setOnClickListener {
            onClick(position)
        }

    }
}

class VideoViewHolder(val binding: ItemVideoBinding) :
    RecyclerView.ViewHolder(binding.root)


