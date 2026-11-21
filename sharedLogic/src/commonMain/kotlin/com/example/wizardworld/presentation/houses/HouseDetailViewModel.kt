package com.example.wizardworld.presentation.houses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wizardworld.domain.model.House
import com.example.wizardworld.domain.usecases.GetHouseUseCase
import com.example.wizardworld.presentation.UiState
import com.example.wizardworld.presentation.toUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HouseDetailViewModel(
    private val houseId: String,
    private val getHouseUseCase: GetHouseUseCase,
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
            _uiState.value = getHouseUseCase(houseId).toUiState()
        }
    }
}
