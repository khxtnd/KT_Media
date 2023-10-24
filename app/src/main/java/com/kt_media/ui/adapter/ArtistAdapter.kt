package com.mymusic.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.kt_media.databinding.ItemArtistBinding
import com.kt_media.domain.entities.Artist
import jp.wasabeef.glide.transformations.RoundedCornersTransformation


class ArtistAdapter(
    private val onClick: (Int) -> Unit
) : RecyclerView.Adapter<ArtistViewHolder>() {

    private val list: ArrayList<Artist> = arrayListOf()

    fun submit(list: List<Artist>) {

        this.list.clear()
        this.list.addAll(list)

        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {

        return list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtistViewHolder {

        val binding =
            ItemArtistBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ArtistViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ArtistViewHolder, position: Int) {
        val item = list[position]
        val transformation = RoundedCornersTransformation(10, 0,
            RoundedCornersTransformation.CornerType.TOP
        )
        Glide.with(holder.binding.ivSai).load(item.image)
            .apply(RequestOptions.bitmapTransform(transformation))
            .into(holder.binding.ivSai)
        holder.binding.tvArtistSai.text = item.name
        holder.binding.layoutItemArtist.setOnClickListener {
            onClick(item.id)
        }

    }
}

class ArtistViewHolder(val binding: ItemArtistBinding) :
    RecyclerView.ViewHolder(binding.root)


