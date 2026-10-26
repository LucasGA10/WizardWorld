package com.example.wizardworld.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wizardworld.domain.model.House
import com.example.wizardworld.domain.repository.HouseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HouseDetailViewModel(
    private val houseId: String,
    private val houseRepository: HouseRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<House>>(UiState.Loading)
    val uiState: StateFlow<UiState<House>> = _uiState.asStateFlow()

    init {
        loadHouse()
    }

    fun retry() {
        loadHouse()
    }

    private fun loadHouse() {
        viewModelScope.launch {
            if (_uiState.value !is UiState.Success) {
                _uiState.value = UiState.Loading
            }
            _uiState.value = houseRepository.getHouse(houseId).toUiState()
        }
    }
}
