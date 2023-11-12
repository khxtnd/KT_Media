package com.mymusic.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kt_media.databinding.ItemStatisticalBinding
import com.kt_media.domain.entities.DayOfUse


class DayOfUseAdapter : RecyclerView.Adapter<DayOfUseViewHolder>() {

    private val list: ArrayList<DayOfUse> = arrayListOf()

    fun submit(list: List<DayOfUse>) {

        this.list.clear()
        this.list.addAll(list)

        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {

        return list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayOfUseViewHolder {

        val binding =
            ItemStatisticalBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return DayOfUseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DayOfUseViewHolder, position: Int) {
        val item = list[position]
        holder.binding.tvTitleDoui.text = item.date
        holder.binding.tvUsedMinuteDoui.text = "${item.usedMinute} Phút"
        holder.binding.tvPlaySongTimeDoui.text = item.playSongTime.toString()
    }
}

class DayOfUseViewHolder(val binding: ItemStatisticalBinding) :
    RecyclerView.ViewHolder(binding.root)


