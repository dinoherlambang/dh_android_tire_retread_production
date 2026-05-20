package com.odoo.dh_android_tire_retread_production.ui.queue

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.odoo.dh_android_tire_retread_production.data.model.QueueItem
import com.odoo.dh_android_tire_retread_production.databinding.ItemQueueBinding

class QueueAdapter(private val onItemClick: (QueueItem) -> Unit) :
    ListAdapter<QueueItem, QueueAdapter.ViewHolder>(QueueDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemQueueBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemQueueBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: QueueItem) {
            binding.woNumber.text = item.wo_number
            binding.serialNumber.text = item.serial_number ?: "No Serial"
            binding.customerName.text = item.customer_name ?: "No Customer"
            binding.statusChip.text = item.station_status.uppercase()
            binding.serviceTypeChip.text = item.service_type ?: "N/A"

            val statusColor = when (item.station_status.lowercase()) {
                "draft" -> "#9E9E9E"
                "in_progress" -> "#2196F3"
                "failed" -> "#F44336"
                "completed" -> "#4CAF50"
                else -> "#9E9E9E"
            }
            binding.statusChip.chipBackgroundColor = ColorStateList.valueOf(Color.parseColor(statusColor))
            binding.statusChip.setTextColor(Color.WHITE)

            binding.root.setOnClickListener { onItemClick(item) }
        }
    }

    class QueueDiffCallback : DiffUtil.ItemCallback<QueueItem>() {
        override fun areItemsTheSame(oldItem: QueueItem, newItem: QueueItem): Boolean {
            return oldItem.workorder_id == newItem.workorder_id
        }

        override fun areContentsTheSame(oldItem: QueueItem, newItem: QueueItem): Boolean {
            return oldItem == newItem
        }
    }
}
