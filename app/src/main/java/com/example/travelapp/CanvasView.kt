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
import java.time.LocalDate

class CanvasView(
    context: Context,
    attributeSet: AttributeSet
) : View(context, attributeSet) {

    lateinit var mBitmap: Bitmap
    var text: String? = null

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        canvas ?: return
        mBitmap = Bitmap.createScaledBitmap(mBitmap, width, height, true)
        val paint = Paint(Color.GRAY)
        canvas.drawBitmap(mBitmap, 0f, 0f, paint)
        val buff = Canvas(mBitmap)
        paint.strokeWidth = 10f
        paint.textSize = 60f //wez z shared.size
        var col = Color.BLACK //wez z shared.color
        paint.color = col
        text?.let {
            buff.drawText(it, 10f, height / 4f, paint)
            canvas.drawText(it, 10f, height / 4f, paint)
        }
        val date = LocalDate.now().toString()
        buff.drawText( date, 10f,  height - height / 4f + 30f + 100f,paint)
        canvas.drawText( date, 10f,  height - height / 4f + 30f + 100f,paint)
    }

    fun loadBitmapFromView(): Bitmap {
        return mBitmap
    }
}