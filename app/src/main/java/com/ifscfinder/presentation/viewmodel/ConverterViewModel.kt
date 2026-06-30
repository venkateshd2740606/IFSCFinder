package com.ifscfinder.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.ifscfinder.domain.engine.UnitCategory
import com.ifscfinder.domain.engine.UnitConverterEngine
import com.ifscfinder.domain.engine.UnitDef
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class ConverterUiState(
    val category: UnitCategory = UnitCategory.LENGTH,
    val fromUnit: UnitDef = UnitCategory.LENGTH.units.first(),
    val toUnit: UnitDef = UnitCategory.LENGTH.units[1],
    val inputValue: String = "1",
    val outputValue: String = "",
    val favorites: Set<String> = setOf("LENGTH", "WEIGHT"),
    val showFavorites: Boolean = false
)

@HiltViewModel
class ConverterViewModel @Inject constructor() : ViewModel() {
    private val _state = MutableStateFlow(ConverterUiState())
    val state = _state.asStateFlow()

    init { recalculate() }

    fun setCategory(category: UnitCategory) {
        _state.update {
            it.copy(
                category = category,
                fromUnit = category.units.first(),
                toUnit = category.units.getOrElse(1) { category.units.first() },
                showFavorites = false
            )
        }
        recalculate()
    }

    fun setFromUnit(unit: UnitDef) {
        _state.update { it.copy(fromUnit = unit) }
        recalculate()
    }

    fun setToUnit(unit: UnitDef) {
        _state.update { it.copy(toUnit = unit) }
        recalculate()
    }

    fun setInput(value: String) {
        _state.update { it.copy(inputValue = value) }
        recalculate()
    }

    fun swapUnits() {
        _state.update { it.copy(fromUnit = it.toUnit, toUnit = it.fromUnit) }
        recalculate()
    }

    fun toggleFavorite(category: UnitCategory) {
        val key = category.name
        _state.update {
            val updated = it.favorites.toMutableSet()
            if (key in updated) updated.remove(key) else updated.add(key)
            it.copy(favorites = updated)
        }
    }

    fun setShowFavorites(show: Boolean) {
        _state.update { it.copy(showFavorites = show) }
    }

    private fun recalculate() {
        val s = _state.value
        val input = s.inputValue.toDoubleOrNull()
        val output = if (input == null) ""
        else UnitConverterEngine.convert(input, s.fromUnit, s.toUnit, s.category).let {
            if (it % 1.0 == 0.0) it.toLong().toString() else "%.6f".format(it).trimEnd('0').trimEnd('.')
        }
        _state.update { it.copy(outputValue = output) }
    }
}
