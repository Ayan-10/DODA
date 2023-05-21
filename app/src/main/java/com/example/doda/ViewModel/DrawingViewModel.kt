package com.example.doda.ViewModel

import androidx.lifecycle.*
import com.example.doda.Database.DrawingRepository
import com.example.doda.Model.Drawing
import kotlinx.coroutines.launch

class DrawingViewModel(private val itemRepository: DrawingRepository) : ViewModel() {

    // Using LiveData and caching what allWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    val allDrawing: LiveData<List<Drawing>> = itemRepository.allDrawings.asLiveData()

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insert(drawing: Drawing) = viewModelScope.launch {
        itemRepository.insert(drawing)
    }

    fun update(drawing: Drawing) = viewModelScope.launch {
        itemRepository.update(drawing)
    }

    fun delete(drawing: Drawing) = viewModelScope.launch {
        itemRepository.delete(drawing)
    }
}