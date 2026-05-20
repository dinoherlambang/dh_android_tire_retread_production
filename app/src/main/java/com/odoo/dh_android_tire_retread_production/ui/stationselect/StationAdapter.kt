package com.odoo.dh_android_tire_retread_production.ui.stationselect

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.odoo.dh_android_tire_retread_production.data.model.StationData
import com.odoo.dh_android_tire_retread_production.databinding.ItemStationBinding

class StationAdapter(private val onStationClick: (StationData) -> Unit) :
    ListAdapter<StationData, StationAdapter.ViewHolder>(StationDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemStationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val station = getItem(position)
        holder.bind(station)
    }

    inner class ViewHolder(private val binding: ItemStationBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(station: StationData) {
            binding.stationName.text = station.name
            binding.stationCode.text = station.code
            binding.root.setOnClickListener { onStationClick(station) }
        }
    }

    class StationDiffCallback : DiffUtil.ItemCallback<StationData>() {
        override fun areItemsTheSame(oldItem: StationData, newItem: StationData): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: StationData, newItem: StationData): Boolean {
            return oldItem == newItem
        }
    }
}
