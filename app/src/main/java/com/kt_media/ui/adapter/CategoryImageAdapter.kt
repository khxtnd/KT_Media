package com.mymusic.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kt_media.databinding.ItemCategoryImageBinding
import com.kt_media.domain.entities.CategoryImage


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
        Glide.with(holder.binding.ivCii).load(item.image)
            .into(holder.binding.ivCii)
        holder.binding.tvNameCii.text = item.name
        holder.binding.layoutItemCategoryImage.setOnClickListener {
            onClick(item.id)
        }

    }
}

class CategoryImageViewHolder(val binding: ItemCategoryImageBinding) :
    RecyclerView.ViewHolder(binding.root)


