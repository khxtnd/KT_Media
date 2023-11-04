package com.mymusic.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.kt_media.databinding.ItemPlayListBinding
import com.kt_media.domain.entities.Playlist
import jp.wasabeef.glide.transformations.RoundedCornersTransformation


class PlayListAdapter(
    private val onClickPlay: (String) -> Unit,
    private val onClickDelete: (String) -> Unit,
    private val onClickUpdate: (String) -> Unit,
) : RecyclerView.Adapter<PlayListViewHolder>() {
    private val list: ArrayList<Playlist> = arrayListOf()

    fun submit(list: List<Playlist>) {

        this.list.clear()
        this.list.addAll(list)

        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayListViewHolder {

        val binding =
            ItemPlayListBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return PlayListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlayListViewHolder, position: Int) {
        val item = list[position]
        val transformation = RoundedCornersTransformation(10, 0,
            RoundedCornersTransformation.CornerType.TOP
        )
        if(item.image.isNotEmpty()){
            Glide.with(holder.binding.ivPli).load(item.image)
                .apply(RequestOptions.bitmapTransform(transformation))
                .into(holder.binding.ivPli)
        }
        holder.binding.tvNamePli.text = item.name
        holder.binding.layoutItemPlayList.setOnClickListener {
            onClickPlay(item.id)
        }
        holder.binding.btUpdatePli.setOnClickListener {
            onClickUpdate(item.id)
        }
        holder.binding.ivDeletePli.setOnClickListener {
            onClickDelete(item.id)
        }

    }
}

class PlayListViewHolder(val binding: ItemPlayListBinding) :
    RecyclerView.ViewHolder(binding.root)


