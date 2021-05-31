package com.example.travelapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [PhotoModel::class], version = 1, exportSchema = false)
abstract class PhotoDatabase : RoomDatabase() {

    abstract val photoDao: PhotoDao

    companion object{
        fun open(context: Context) = Room.databaseBuilder(
            context, PhotoDatabase::class.java, "dishdb"
        ).build()
    }
}