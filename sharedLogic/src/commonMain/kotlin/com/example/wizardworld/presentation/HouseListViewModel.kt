package com.example.wizardworld.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wizardworld.domain.model.HouseListItem
import com.example.wizardworld.domain.repository.HouseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HouseListViewModel(
    private val houseRepository: HouseRepository,
) : ViewModel() {
    private val unfilteredState =
        MutableStateFlow<UiState<List<HouseListItem>>>(UiState.Loading)

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    val isOffline: StateFlow<Boolean> =
        unfilteredState
            .map { state -> (state as? UiState.Success)?.isOffline == true }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = false,
            )

    val uiState: StateFlow<UiState<List<HouseListItem>>> =
        combine(unfilteredState, _searchQuery) { state, query ->
            when (state) {
                is UiState.Success -> {
                    val filtered =
                        state.data.filter { house ->
                            house.name.contains(query.trim(), ignoreCase = true)
                        }
                    when {
                        state.data.isEmpty() || filtered.isEmpty() -> UiState.Empty
                        else -> UiState.Success(data = filtered, isOffline = state.isOffline)
                    }
                }
                else -> state
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UiState.Loading,
        )

    init {
        loadHouses()
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun refresh() {
        loadHouses(fromRefresh = true)
    }

    fun retry() {
        loadHouses()
    }

    private fun loadHouses(fromRefresh: Boolean = false) {
        viewModelScope.launch {
            if (fromRefresh) {
                _isRefreshing.value = true
            } else if (unfilteredState.value !is UiState.Success) {
                unfilteredState.value = UiState.Loading
            }
            unfilteredState.value =
                houseRepository.getHouses().toUiState { houses -> houses.isEmpty() }
            _isRefreshing.value = false
        }
    }
}
