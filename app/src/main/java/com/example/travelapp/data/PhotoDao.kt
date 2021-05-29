package com.example.travelapp.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PhotoDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addUser(photoModel: PhotoModel)
    @Query("SELECT * FROM photo_table ORDER BY id ASC")
    fun readAllData(): LiveData<List<PhotoModel>>

}