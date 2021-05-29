package com.example.travelapp

import android.location.Location
import android.os.Build
import androidx.annotation.RequiresApi

object Shared {
    @RequiresApi(Build.VERSION_CODES.O)
    val photos = fillPhotosWithInitialMetadata()
    var location: Location? = null


    @RequiresApi(Build.VERSION_CODES.O)
    private fun fillPhotosWithInitialMetadata(): ArrayList<PhotoMetadata> {
        val list = ArrayList<PhotoMetadata>()
        val income = PhotoMetadata(0, R.drawable.ada, "Warsaw", "duupa pierdolona", "someNote")
        list.add(income)
        for (i in 1 until 20) {
            val generatedIncome = PhotoMetadata(i, R.drawable.ada, "Warsaw", "chuj", "someNote")
            list.add(generatedIncome)
        }
        return list
    }
}