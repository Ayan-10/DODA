package com.example.doda.Model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Marker(
    val x: Double,
    val y: Double,
    val name: String,
    val details: String
): Parcelable{
    override fun equals(other: Any?): Boolean {
        if (other == null) {
            return false;
        }

        return this.x == (other as Marker).x && this.y == (other as Marker).y
    }
}

