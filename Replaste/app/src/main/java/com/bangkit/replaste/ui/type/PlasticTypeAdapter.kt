package com.bangkit.replaste.ui.type

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.replaste.R
import com.bangkit.replaste.api.menu.PlasticTypeModel
import com.bangkit.replaste.databinding.ItemPlasticTypeBinding
import com.bumptech.glide.Glide

class PlasticTypeAdapter(
    private val onItemClick: (PlasticTypeModel) -> Unit
) : ListAdapter<PlasticTypeModel, PlasticTypeAdapter.PlasticTypeViewHolder>(PlasticTypeDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlasticTypeViewHolder {
        val binding = ItemPlasticTypeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PlasticTypeViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: PlasticTypeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PlasticTypeViewHolder(
        private val binding: ItemPlasticTypeBinding,
        private val onItemClick: (PlasticTypeModel) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(plasticType: PlasticTypeModel) {
            binding.apply {
                
                tvPlasticName.text = plasticType.nama
                tvPlasticDesc.text = plasticType.deskripsi

                Glide.with(root.context)
                    .load(plasticType.imageUrl)
                    .placeholder(R.drawable.placeholder_plastic)
                    .error(R.drawable.placeholder_plastic)
                    .into(ivPlasticIcon)

                root.setOnClickListener {
                    onItemClick(plasticType)
                }
            }
        }
    }

    class PlasticTypeDiffCallback : DiffUtil.ItemCallback<PlasticTypeModel>() {
        override fun areItemsTheSame(
            oldItem: PlasticTypeModel,
            newItem: PlasticTypeModel
        ): Boolean {
            return oldItem.kode == newItem.kode
        }

        override fun areContentsTheSame(
            oldItem: PlasticTypeModel,
            newItem: PlasticTypeModel
        ): Boolean {
            return oldItem == newItem
        }
    }
}