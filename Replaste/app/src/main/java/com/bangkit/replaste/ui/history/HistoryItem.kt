package com.bangkit.replaste.ui.history

import java.util.Date

data class HistoryItem(
    val imageFile: String,
    val predictedClass: String,
    val confidence: Float,
    val date: Date,
)