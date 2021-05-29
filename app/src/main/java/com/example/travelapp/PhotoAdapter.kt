package com.example.travelapp

import android.content.Intent
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
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
            holder.category.setImageBitmap(BitmapFactory.decodeFile(currentItem.photoUri))
            holder.location.text = currentItem.locationData
            holder.date.text = currentItem.date
            holder.comment.text = currentItem.comment
            holder.itemView.setOnClickListener {
                val intent = Intent(parent.context,EditPhotoActivity::class.java)
                intent.putExtra("photoUri", userList[position].photoUri)
                intent.putExtra("comment",userList[position].comment)
                parent.context.startActivity(intent)
            }
//            holder.itemView.setOnLongClickListener {
//
//            }
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