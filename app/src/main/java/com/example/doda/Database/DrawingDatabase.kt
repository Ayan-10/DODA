package com.example.doda.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.doda.Model.Drawing
import kotlinx.coroutines.CoroutineScope

@Database(entities = [Drawing::class], version = 1)
@TypeConverters(MarkerListConverter::class)
abstract class DrawingDatabase : RoomDatabase() {

    abstract fun drawingDao(): DrawingDao

    companion object {
        @Volatile
        private var INSTANCE: DrawingDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): DrawingDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DrawingDatabase::class.java,
                    "drawing_database"
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}
