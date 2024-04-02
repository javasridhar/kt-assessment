package com.sridhar.telematics.assessment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sridhar.telematics.assessment.databinding.LocationListItemBinding
import com.sridhar.telematics.assessment.model.entity.RealmLocation

class LocationAdapter(private val onLocationSelected: (RealmLocation) -> Unit) : RecyclerView.Adapter<LocationAdapter.ViewHolder>() {

    private var list: MutableList<RealmLocation> = mutableListOf()
    private lateinit var binding: LocationListItemBinding

    inner class ViewHolder(private val binding: LocationListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(locationUpdate: RealmLocation) {
            binding.root.setOnClickListener { onLocationSelected(locationUpdate) }
            binding.location = locationUpdate
        }
    }

    fun add(locationUpdate: RealmLocation) {
        list.add(locationUpdate)
        notifyItemInserted(list.size - 1)
    }

    fun addAll(locationUpdates: MutableList<RealmLocation>) {
        list.clear()
        list.addAll(locationUpdates)
        notifyItemInserted(list.size - 1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = LocationListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val location = list[position]
        holder.bind(location)
    }
}