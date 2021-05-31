package com.example.travelapp

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.util.AttributeSet
import android.view.View
import androidx.annotation.RequiresApi
import java.io.FileOutputStream
import java.io.IOException
import java.time.LocalDate

class CanvasView(
    context: Context,
    attributeSet: AttributeSet
) : View(context, attributeSet) {

    lateinit var bitmap: Bitmap
    var text: String? = null
    lateinit var filename: String

    @SuppressLint("DrawAllocation")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onDraw(canvas: Canvas?) {
        canvas ?: return
        bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true)
        val paint = Paint(Color.GRAY)
        canvas.drawBitmap(bitmap, 0f, 0f, paint)
        val canvas1 = Canvas(bitmap)
        paint.strokeWidth = 10f
        paint.textSize = Shared.textSize
        var col = Shared.textColor
        paint.color = col
        text?.let {
            canvas1.drawText(it, 5f, height - height / 4f + 120F, paint)
        }
        val date = LocalDate.now().toString()
        canvas1.drawText(date, 5f, height / 16F, paint)
        saveImage()
    }

    fun saveImage() {
        try {
            FileOutputStream(filename).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}