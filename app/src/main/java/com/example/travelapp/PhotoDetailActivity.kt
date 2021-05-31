package com.example.travelapp

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.travelapp.databinding.ActivityPhotoDetailBinding
import kotlinx.android.synthetic.main.activity_photo_detail.*


class PhotoDetailActivity : AppCompatActivity() {

    private val binding by lazy { ActivityPhotoDetailBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        getIntentData()
    }

    private fun getIntentData() {
        if (intent.getSerializableExtra("photoUri") != null) {
            val photoUri = intent.getSerializableExtra("photoUri") as String
            setImage(photoUri)
            val comment = intent.getSerializableExtra("comment") as String
            binding.textView.text = comment
        }
    }

    private fun setImage(photoUri: String) {
        val bitmap = BitmapFactory.decodeFile(photoUri)

        val ei = ExifInterface(photoUri)
        val orientation: Int = ei.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_UNDEFINED
        )

        val rotatedBitmap: Bitmap? = when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(bitmap, 90F)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(bitmap, 180F)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(bitmap, 270F)
            ExifInterface.ORIENTATION_NORMAL -> bitmap
            else -> bitmap
        }

        binding.imageView.setImageBitmap(rotatedBitmap)
    }

    private fun rotateImage(source: Bitmap, angle: Float): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(
            source, 0, 0, source.width, source.height,
            matrix, true
        )
    }
}