package com.example.doda.Model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "drawing_table")
data class Drawing(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    @ColumnInfo(name = "drawingName") val drawingName: String,
    val additionTime: String,
    val thumbnail: String,
    val countMarker: Int,
    val markers: List<Marker>
){


}

