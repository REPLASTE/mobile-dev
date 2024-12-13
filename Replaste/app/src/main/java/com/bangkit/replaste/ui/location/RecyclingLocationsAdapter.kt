package com.bangkit.replaste.ui.location

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.replaste.R
import com.google.android.material.button.MaterialButton

class RecyclingLocationsAdapter(
    private val onMapClickListener: (RecyclingLocation) -> Unit
) : ListAdapter<RecyclingLocation, RecyclingLocationsAdapter.LocationViewHolder>(LocationDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recycling_location, parent, false)
        return LocationViewHolder(view, onMapClickListener)
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class LocationViewHolder(
        view: View,
        private val onMapClickListener: (RecyclingLocation) -> Unit
    ) : RecyclerView.ViewHolder(view) {
        private val nameTextView: TextView = view.findViewById(R.id.tv_location_name)
        private val addressTextView: TextView = view.findViewById(R.id.tv_location_address)
        private val phoneTextView: TextView = view.findViewById(R.id.tv_phone_number)
        private val mapButton: MaterialButton = view.findViewById(R.id.btn_open_maps)

        fun bind(location: RecyclingLocation) {
            nameTextView.text = location.name
            addressTextView.text = location.address
            phoneTextView.text = location.phoneNumber

            mapButton.setOnClickListener {
                onMapClickListener(location)
            }
        }
    }

    class LocationDiffCallback : DiffUtil.ItemCallback<RecyclingLocation>() {
        override fun areItemsTheSame(oldItem: RecyclingLocation, newItem: RecyclingLocation): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: RecyclingLocation, newItem: RecyclingLocation): Boolean {
            return oldItem == newItem
        }
    }
}