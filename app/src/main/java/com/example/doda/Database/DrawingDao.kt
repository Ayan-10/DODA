package com.example.doda.Database

import androidx.room.*
import com.example.doda.Model.Drawing
import kotlinx.coroutines.flow.Flow

@Dao
interface DrawingDao {
    @Query("SELECT * FROM drawing_table")
    fun getAllDrawings(): Flow<List<Drawing>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(drawing: Drawing)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(drawings: List<Drawing>)

    @Update
    suspend fun update(drawing: Drawing)

    @Delete
    suspend fun delete(drawing: Drawing)

    @Query("DELETE FROM drawing_table")
    suspend fun deleteAll()
}