package com.ifscfinder.presentation.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ifscfinder.domain.catalog.IfscBranch
import com.ifscfinder.presentation.viewmodel.IfscViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IfscSearchScreen(
    onBranchClick: (String) -> Unit,
    viewModel: IfscViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { TopAppBar(title = { Text("IFSC Finder") }) }
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = state.query,
                onValueChange = viewModel::setQuery,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Search IFSC, bank or city") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                singleLine = true
            )
            if (state.query.isBlank()) {
                Text(
                    "Enter an IFSC code, bank name, or city to search",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else if (state.results.isEmpty()) {
                Text("No branches found", color = MaterialTheme.colorScheme.error)
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(state.results, key = { it.ifsc }) { branch ->
                        IfscBranchCard(branch, viewModel.isFavorite(branch.ifsc)) {
                            onBranchClick(branch.ifsc)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IfscDetailScreen(
    ifsc: String,
    onBack: () -> Unit,
    viewModel: IfscViewModel = hiltViewModel()
) {
    val branch = remember(ifsc) { com.ifscfinder.domain.catalog.IFSCBankCatalog.findByIfsc(ifsc) }
    val isFavorite by remember(ifsc) {
        derivedStateOf { viewModel.isFavorite(ifsc) }
    }

    if (branch == null) {
        Scaffold(topBar = {
            TopAppBar(
                title = { Text("Not Found") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }) { padding ->
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("IFSC code not found in catalog")
            }
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(branch.ifsc) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleFavorite(branch) }) {
                        Icon(
                            if (isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                            "Favorite"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Card(Modifier.fillMaxWidth().padding(padding).padding(16.dp)) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                DetailRow("IFSC Code", branch.ifsc, FontFamily.Monospace)
                DetailRow("Bank", branch.bankName)
                DetailRow("Branch", branch.branch)
                DetailRow("City", branch.city)
                DetailRow("State", branch.state)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IfscBankBrowseScreen(
    onBankClick: (String) -> Unit,
    viewModel: IfscViewModel = hiltViewModel()
) {
    val banks = remember { viewModel.bankNames() }

    Scaffold(topBar = { TopAppBar(title = { Text("Browse Banks") }) }) { padding ->
        LazyColumn(
            Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(banks, key = { it }) { bank ->
                Card(
                    Modifier.fillMaxWidth().clickable { onBankClick(bank) }
                ) {
                    Row(
                        Modifier.padding(16.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(bank, fontWeight = FontWeight.Medium)
                        Icon(Icons.Default.ChevronRight, null)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IfscBankBranchesScreen(
    bankName: String,
    onBranchClick: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: IfscViewModel = hiltViewModel()
) {
    val branches = remember(bankName) { viewModel.branchesForBank(bankName) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(bankName, maxLines = 1) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(branches, key = { it.ifsc }) { branch ->
                IfscBranchCard(branch, viewModel.isFavorite(branch.ifsc)) {
                    onBranchClick(branch.ifsc)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IfscFavoritesScreen(
    onBranchClick: (String) -> Unit,
    viewModel: IfscViewModel = hiltViewModel()
) {
    val favorites by viewModel.favoriteBranches.collectAsStateWithLifecycle()

    Scaffold(topBar = { TopAppBar(title = { Text("Favorites") }) }) { padding ->
        if (favorites.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.StarBorder, null, Modifier.size(48.dp))
                    Spacer(Modifier.height(8.dp))
                    Text("No saved branches yet")
                }
            }
        } else {
            LazyColumn(
                Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(favorites, key = { it.ifsc }) { branch ->
                    IfscBranchCard(branch, true) { onBranchClick(branch.ifsc) }
                }
            }
        }
    }
}

@Composable
private fun IfscBranchCard(branch: IfscBranch, isFavorite: Boolean, onClick: () -> Unit) {
    Card(Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Row(
            Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(branch.ifsc, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                Text(branch.bankName, style = MaterialTheme.typography.bodyMedium)
                Text("${branch.branch}, ${branch.city}", style = MaterialTheme.typography.bodySmall)
            }
            if (isFavorite) {
                Icon(Icons.Default.Star, "Favorite", tint = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String, fontFamily: FontFamily = FontFamily.Default) {
    Column {
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyLarge, fontFamily = fontFamily, fontWeight = FontWeight.Medium)
    }
}
