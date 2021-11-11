package com.ouz.myartbookkotlin.data

import androidx.room.*

@Dao
interface ArtDAO {
    @Insert
    fun addImages(image: ImagesEntity)

    @Delete
    fun deleteImages(image: ImagesEntity)

    @Update
    fun updateImages(image:ImagesEntity)

    @Query("SELECT * FROM imagesbook")
    fun allImages(): List<ImagesEntity?>

    @Query("SELECT * FROM imagesbook WHERE id=(:uid)")
    fun getUser(uid:Int): ImagesEntity




}