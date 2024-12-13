package com.bangkit.replaste.ui.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.replaste.R
import com.bangkit.replaste.databinding.ItemHistoryBinding
import com.bangkit.replaste.databinding.ItemHistoryDateBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions

class HistoryAdapter(private val groupedHistoryList: Map<String, List<HistoryItem>>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_DATE = 0
        private const val VIEW_TYPE_ITEM = 1
    }

    override fun getItemViewType(position: Int): Int {
        var count = 0
        for ((date, items) in groupedHistoryList) {
            if (position == count) return VIEW_TYPE_DATE
            count++
            if (position < count + items.size) return VIEW_TYPE_ITEM
            count += items.size
        }
        return VIEW_TYPE_ITEM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_DATE -> {
                val binding = ItemHistoryDateBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                DateViewHolder(binding)
            }
            else -> {
                val binding = ItemHistoryBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                ItemViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var count = 0
        for ((date, items) in groupedHistoryList) {
            if (position == count) {
                (holder as DateViewHolder).bind(date)
                return
            }
            count++
            if (position < count + items.size) {
                val item = items[position - count]
                (holder as ItemViewHolder).bind(item)
                return
            }
            count += items.size
        }
    }

    override fun getItemCount(): Int {
        return groupedHistoryList.entries.sumOf { it.value.size + 1 }
    }

    class DateViewHolder(private val binding: ItemHistoryDateBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(date: String) {
            binding.tvDate.text = date
        }
    }

    class ItemViewHolder(private val binding: ItemHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(history: HistoryItem) {
            binding.apply {
                tvTitle.text = "Plastik ${history.predictedClass}"
                tvCategory.text = history.predictedClass
                tvConfidence.text = "Akurasi: ${String.format("%.2f%%", history.confidence * 100)}"
                tvDescription.text = getPlasticDescription(history.predictedClass)

                
                val requestOptions = RequestOptions()
                    .placeholder(R.drawable.placeholder_plastic)
                    .error(R.drawable.placeholder_plastic)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()

                Glide.with(itemView.context)
                    .load(history.imageFile)
                    .apply(requestOptions)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(ivPlastic)
            }
        }

        private fun getPlasticDescription(plasticType: String): String {
            return when (plasticType) {
                "PET" -> "Polyethylene Terephthalate: Bahan plastik umum untuk botol minuman"
                "HDPE" -> "High-Density Polyethylene: Digunakan untuk wadah detergen dan mainan"
                "PVC" -> "Polyvinyl Chloride: Plastik serbaguna untuk pipa dan konstruksi"
                "LDPE" -> "Low-Density Polyethylene: Plastik lentur untuk kantong plastik"
                "PP" -> "Polypropylene: Tahan panas, cocok untuk wadah makanan"
                "PS" -> "Polystyrene: Ringan dan mudah dibentuk"
                else -> "Jenis plastik tidak dikenal"
            }
        }
    }
}