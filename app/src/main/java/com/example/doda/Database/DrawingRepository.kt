package com.example.doda.Database

import androidx.annotation.WorkerThread
import com.example.doda.Model.Drawing
import kotlinx.coroutines.flow.Flow

class DrawingRepository(private val drawingDao: DrawingDao) {

    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    val allDrawings: Flow<List<Drawing>> = drawingDao.getAllDrawings()

    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(drawing: Drawing) {
        drawingDao.insert(drawing)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertAll(drawings: List<Drawing>) {
        drawingDao.insertAll(drawings)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun update(drawing: Drawing) {
        drawingDao.update(drawing)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun delete(drawing: Drawing) {
        drawingDao.delete(drawing)
    }
}
