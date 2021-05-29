package com.example.travelapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "photo_table")
data class PhotoModel(
    @PrimaryKey(autoGenerate = true)
    val id:Int,
    val photoUri: String,
    val date: String,
    val locationData: String,
    val comment: String
)