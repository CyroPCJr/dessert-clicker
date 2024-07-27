package com.example.dessertclicker.viewmodel

import androidx.lifecycle.ViewModel
import com.example.dessertclicker.data.Datasource
import com.example.dessertclicker.model.Dessert
import com.example.dessertclicker.model.DessertUIState


import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class DessertViewModel : ViewModel() {
    private val _uiState: MutableStateFlow<DessertUIState> = MutableStateFlow(DessertUIState())
    val uiState: StateFlow<DessertUIState> = _uiState.asStateFlow()

    private val _desserts: List<Dessert> = Datasource.dessertList

    init {
        _uiState.update { currentState ->
            val dessert = _desserts[currentState.currentDessertIndex]
            currentState.copy(
                currentDessertPrice = dessert.price,
                currentDessertImageId = dessert.imageId
            )
        }
    }

    /**
     * Determine which dessert to show.
     */
    private fun determineDessertToShow(dessertsSold: Int): Dessert {
        var dessertToShow: Dessert = _desserts.first()
        for (dessert: Dessert in _desserts) {
            if (dessertsSold >= dessert.startProductionAmount) {
                dessertToShow = dessert
            } else {
                // The list of desserts is sorted by startProductionAmount. As you sell more desserts,
                // you'll start producing more expensive desserts as determined by startProductionAmount
                // We know to break as soon as we see a dessert who's "startProductionAmount" is greater
                // than the amount sold.
                break
            }
        }

        return dessertToShow
    }

    fun updateDesserts() {
        // Update the revenue
        _uiState.update { currentState ->
            val updateRevenue = currentState.revenue + currentState.currentDessertPrice
            val updateDessertSold = currentState.dessertSold.inc()
            val displayDessert = determineDessertToShow(updateDessertSold)

            val currentDessertImageId = displayDessert.imageId
            val currentDessertPrice = displayDessert.price

            currentState.copy(
                revenue = updateRevenue,
                dessertSold = updateDessertSold,
                currentDessertPrice = currentDessertPrice,
                currentDessertImageId = currentDessertImageId
            )
        }
    }

}