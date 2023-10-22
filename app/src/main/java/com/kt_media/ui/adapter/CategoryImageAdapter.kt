package com.mymusic.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.kt_media.databinding.ItemCategoryImageBinding
import com.kt_media.domain.entities.CategoryImage
import jp.wasabeef.glide.transformations.RoundedCornersTransformation


class CategoryImageAdapter(
    private val onClick: (Int) -> Unit
) : RecyclerView.Adapter<CategoryImageViewHolder>() {

    private val list: ArrayList<CategoryImage> = arrayListOf()

    fun submit(list: List<CategoryImage>) {

        this.list.clear()
        this.list.addAll(list)

        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {

        return list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryImageViewHolder {

        val binding =
            ItemCategoryImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return CategoryImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryImageViewHolder, position: Int) {
        val item = list[position]
        val transformation = RoundedCornersTransformation(10, 0,
            RoundedCornersTransformation.CornerType.TOP
        )
        Glide.with(holder.binding.ivCii).load(item.image)
            .apply(RequestOptions.bitmapTransform(transformation))
            .into(holder.binding.ivCii)
        holder.binding.tvNameCii.text = item.name
        holder.binding.layoutItemCategoryImage.setOnClickListener {
            onClick(it.id)
        }

    }
}

class CategoryImageViewHolder(val binding: ItemCategoryImageBinding) :
    RecyclerView.ViewHolder(binding.root)


