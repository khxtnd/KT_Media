package com.mymusic.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kt_media.databinding.ItemStatisticalBinding
import com.kt_media.domain.entities.MonthOfUse


class MonthOfUseAdapter : RecyclerView.Adapter<MonthOfUseViewHolder>() {

    private val list: ArrayList<MonthOfUse> = arrayListOf()

    fun submit(list: List<MonthOfUse>) {

        this.list.clear()
        this.list.addAll(list)

        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {

        return list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonthOfUseViewHolder {

        val binding =
            ItemStatisticalBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return MonthOfUseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MonthOfUseViewHolder, position: Int) {
        val item = list[position]
        holder.binding.tvTitleDoui.text = item.month
        holder.binding.tvUsedMinuteDoui.text = "${item.usedMinute} Phút"
        holder.binding.tvPlaySongTimeDoui.text = item.playSongTime.toString()
    }
}

class MonthOfUseViewHolder(val binding: ItemStatisticalBinding) :
    RecyclerView.ViewHolder(binding.root)


