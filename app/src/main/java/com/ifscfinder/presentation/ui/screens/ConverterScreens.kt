package com.ifscfinder.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ifscfinder.domain.engine.UnitCategory
import com.ifscfinder.domain.engine.UnitDef
import com.ifscfinder.presentation.viewmodel.ConverterViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConverterScreen(viewModel: ConverterViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()
    val categories = if (state.showFavorites) {
        UnitCategory.entries.filter { state.favorites.contains(it.name) }
    } else UnitCategory.entries.toList()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (state.showFavorites) "Favorites" else "Unit Converter") },
                actions = {
                    IconButton(onClick = { viewModel.setShowFavorites(!state.showFavorites) }) {
                        Icon(
                            if (state.showFavorites) Icons.Default.GridView else Icons.Default.Star,
                            "Toggle favorites"
                        )
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(categories) { cat ->
                        val selected = state.category == cat
                        FilterChip(
                            selected = selected,
                            onClick = { viewModel.setCategory(cat) },
                            label = { Text(cat.displayName) },
                            leadingIcon = if (state.favorites.contains(cat.name)) {
                                { Icon(Icons.Default.Star, null, modifier = Modifier.size(16.dp)) }
                            } else null,
                            trailingIcon = {
                                IconButton(onClick = { viewModel.toggleFavorite(cat) }, modifier = Modifier.size(18.dp)) {
                                    Icon(
                                        if (state.favorites.contains(cat.name)) Icons.Default.Star else Icons.Default.StarBorder,
                                        "Favorite",
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        )
                    }
                }
            }
            item {
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        UnitDropdown("From", state.fromUnit, state.category.units) { viewModel.setFromUnit(it) }
                        OutlinedTextField(
                            value = state.inputValue,
                            onValueChange = viewModel::setInput,
                            label = { Text("Value") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                            IconButton(onClick = viewModel::swapUnits) {
                                Icon(Icons.Default.SwapVert, "Swap")
                            }
                        }
                        UnitDropdown("To", state.toUnit, state.category.units) { viewModel.setToUnit(it) }
                        OutlinedTextField(
                            value = state.outputValue,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Result") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                disabledBorderColor = MaterialTheme.colorScheme.outline,
                                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            enabled = false
                        )
                    }
                }
            }
            item {
                Text(
                    "1 ${state.fromUnit.label} = ${state.outputValue.ifEmpty { "—" }} ${state.toUnit.label}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UnitDropdown(label: String, selected: UnitDef, units: List<UnitDef>, onSelect: (UnitDef) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = selected.label,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            units.forEach { unit ->
                DropdownMenuItem(
                    text = { Text("${unit.label} (${unit.id})") },
                    onClick = { onSelect(unit); expanded = false }
                )
            }
        }
    }
}
