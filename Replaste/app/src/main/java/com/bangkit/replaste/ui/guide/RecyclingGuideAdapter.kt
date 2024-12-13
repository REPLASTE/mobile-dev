package com.bangkit.replaste.ui.guide

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.replaste.R
import com.google.android.material.chip.Chip

class RecyclingGuideAdapter(
    private val guides: List<GuideModel>,
    private val onItemClick: (GuideModel) -> Unit
) : RecyclerView.Adapter<RecyclingGuideAdapter.GuideViewHolder>() {

    inner class GuideViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tvGuideTitle)
        private val tvDescription: TextView = itemView.findViewById(R.id.tvGuideDescription)
        private val tvEmoji: TextView = itemView.findViewById(R.id.tvEmoji)
        private val chipCategory: Chip = itemView.findViewById(R.id.chipCategory)

        fun bind(guide: GuideModel) {
            tvTitle.text = guide.title
            tvDescription.text = guide.description
            tvEmoji.text = guide.emoji
            chipCategory.text = guide.category

            itemView.setOnClickListener { onItemClick(guide) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GuideViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_guide, parent, false)
        return GuideViewHolder(view)
    }

    override fun onBindViewHolder(holder: GuideViewHolder, position: Int) {
        holder.bind(guides[position])
    }

    override fun getItemCount() = guides.size
}