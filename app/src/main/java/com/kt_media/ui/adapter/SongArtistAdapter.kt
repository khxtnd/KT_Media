package com.mymusic.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.kt_media.databinding.ItemSongArtistBinding
import com.kt_media.domain.entities.SongArtist
import jp.wasabeef.glide.transformations.RoundedCornersTransformation


class SongArtistAdapter(
    private val onClick: (SongArtist) -> Unit
) : RecyclerView.Adapter<SongArtistViewHolder>() {

    private val list: ArrayList<SongArtist> = arrayListOf()

    fun submit(list: List<SongArtist>) {

        this.list.clear()
        this.list.addAll(list)

        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {

        return list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongArtistViewHolder {

        val binding =
            ItemSongArtistBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return SongArtistViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SongArtistViewHolder, position: Int) {
        val item = list[position]
        val transformation = RoundedCornersTransformation(
            10,
            0,
            RoundedCornersTransformation.CornerType.TOP
        )
        Glide.with(holder.binding.ivSai).load(item.image)
            .apply(RequestOptions.bitmapTransform(transformation))
            .into(holder.binding.ivSai)
        holder.binding.tvArtistSai.text = item.name
        holder.binding.layoutItemSongArtist.setOnClickListener {
            onClick(item)
        }

    }
}

class SongArtistViewHolder(val binding: ItemSongArtistBinding) :
    RecyclerView.ViewHolder(binding.root)


