package com.ouz.myartbookkotlin.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Blob

@Entity(tableName = "imagesbook")
data class ImagesEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0,
    @ColumnInfo(name = "artName")
    var artName: String,
    @ColumnInfo(name = "artistName")
    var artistName: String,
    @ColumnInfo(name = "year")
    var year: Int,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    var image:ByteArray
)
