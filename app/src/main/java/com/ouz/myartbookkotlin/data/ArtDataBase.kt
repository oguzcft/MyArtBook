package com.ouz.myartbookkotlin.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ouz.myartbookkotlin.fragments.Converters

@Database(entities = [ImagesEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class ArtDataBase : RoomDatabase() {
    abstract fun artDao(): ArtDAO

    companion object {
        private var INSTANCE: ArtDataBase? = null
        fun getImagesDataBase(context: Context): ArtDataBase? {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context, ArtDataBase::class.java, "imagesbook.db")
                    .allowMainThreadQueries().build()
            }
            return INSTANCE
        }
    }
}