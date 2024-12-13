package com.bangkit.replaste.ui.guide.detail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.replaste.R
import com.bangkit.replaste.ui.guide.data.PlasticGuideModel

class DetailGuideAdapter(
    private val plastics: List<PlasticGuideModel>,
    private val onItemClick: (PlasticGuideModel) -> Unit
) : RecyclerView.Adapter<DetailGuideAdapter.PlasticGuideViewHolder>() {

    inner class PlasticGuideViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvStep: TextView = itemView.findViewById(R.id.tvStep)
        private val tvStepTitle: TextView = itemView.findViewById(R.id.tvStepTitle)
        private val btnMoreDetails: Button = itemView.findViewById(R.id.btnMoreDetails)

        fun bind(plastic: PlasticGuideModel) {

            val steps = plastic.recycling_steps.joinToString("\n\n") { step ->
                "${step.step_title}\n${step.description}"
            }

            tvStep.text = steps

            tvStepTitle.text = "Langkah Daur Ulang Tahap ${plastic.id_recycling_step}"


            btnMoreDetails.setOnClickListener { onItemClick(plastic) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlasticGuideViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_plastic_guide, parent, false)
        return PlasticGuideViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlasticGuideViewHolder, position: Int) {

        holder.bind(plastics[position])
    }

    override fun getItemCount() = plastics.size
}