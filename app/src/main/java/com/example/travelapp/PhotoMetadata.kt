package com.example.travelapp

import android.widget.ImageView
import java.io.Serializable
import java.time.LocalDate

data class PhotoMetadata(
    val index: Int,
    val category: Int,
    val location: String,
    val date: String,
    val note: String
) : Serializable