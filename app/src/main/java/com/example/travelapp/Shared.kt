package com.example.travelapp

import android.graphics.Color
import android.location.Location
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider

object Shared {
    @RequiresApi(Build.VERSION_CODES.O)
    var location: Location? = null
    var textColor: Int = Color.BLUE
    var textSize: Float = 80F
    var radius: Float = 1000F
    var mPhotoViewModel: ViewModelProvider? = null
}