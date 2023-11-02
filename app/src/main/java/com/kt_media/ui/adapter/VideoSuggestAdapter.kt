package com.mymusic.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kt_media.R
import com.kt_media.databinding.ItemVideoBinding
import com.kt_media.domain.entities.Video


class VideoSuggestAdapter (
    private val onClick:(Int)-> Unit
) : RecyclerView.Adapter<VideoSuggestViewHolder>() {
    private var videoSelectedPosition=-1
    private val list: ArrayList<Video> = arrayListOf()

    fun submit(list: List<Video>) {

        this.list.clear()
        this.list.addAll(list)

        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {

        return list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoSuggestViewHolder {

        val binding =
            ItemVideoBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return VideoSuggestViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VideoSuggestViewHolder, position: Int) {
        val item = list[position]
        Glide.with(holder.binding.ivVideoVi).load(item.image)
            .into(holder.binding.ivVideoVi)
        holder.binding.tvVideoNameVi.text = item.name
        holder.binding.layoutItemVideo.setOnClickListener {
            onClick(position)
        }
        if (videoSelectedPosition==position) {
            holder.binding.layoutItemVideo.setBackgroundResource(R.drawable.bg_item_song_or_video_selected)
        } else {
            holder.binding.layoutItemVideo.setBackgroundResource(R.drawable.bg_item_song_or_video)
        }

    }
    fun setBackgroundItem(index:Int){
        videoSelectedPosition=index
        notifyDataSetChanged()
    }
}

class VideoSuggestViewHolder(val binding: ItemVideoBinding) :
    RecyclerView.ViewHolder(binding.root)


