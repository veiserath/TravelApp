package com.example.travelapp

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.travelapp.data.PhotoModel

class PhotoAdapter : RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>() {

    lateinit var parent: ViewGroup
    var userList = emptyList<PhotoModel>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PhotoViewHolder {
        this.parent = parent
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.photo_item, parent, false)
        return PhotoViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val currentItem = userList[position]
        holder.category.setImageBitmap(setImage(currentItem.photoUri))
        holder.location.text = currentItem.locationData
        holder.date.text = currentItem.date
        holder.comment.text = currentItem.comment
        holder.itemView.setOnClickListener {
            val intent = Intent(parent.context, PhotoDetailActivity::class.java)
            intent.putExtra("photoUri", userList[position].photoUri)
            intent.putExtra("comment", userList[position].comment)
            parent.context.startActivity(intent)
        }
    }

    private fun setImage(photoUri: String): Bitmap? {
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
        return rotatedBitmap
    }

    private fun rotateImage(source: Bitmap, angle: Float): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(
            source, 0, 0, source.width, source.height,
            matrix, true
        )
    }

    fun setData(photoModel: List<PhotoModel>) {
        userList = photoModel
        notifyDataSetChanged()
    }

    override fun getItemCount() = userList.size

    class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val category: ImageView = itemView.findViewById(R.id.image_view)
        val comment: TextView = itemView.findViewById(R.id.note)
        val date: TextView = itemView.findViewById(R.id.date)
        val location: TextView = itemView.findViewById(R.id.location)
    }
}