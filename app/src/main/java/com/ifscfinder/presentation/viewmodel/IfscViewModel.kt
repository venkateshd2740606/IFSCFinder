package com.ifscfinder.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ifscfinder.data.local.database.dao.IfscFavoriteDao
import com.ifscfinder.data.local.database.entity.IfscFavoriteEntity
import com.ifscfinder.domain.catalog.IFSCBankCatalog
import com.ifscfinder.domain.catalog.IfscBranch
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class IfscUiState(
    val query: String = "",
    val results: List<IfscBranch> = emptyList(),
    val favoriteIfscs: Set<String> = emptySet()
)

@HiltViewModel
class IfscViewModel @Inject constructor(
    private val favoriteDao: IfscFavoriteDao
) : ViewModel() {

    private val _state = MutableStateFlow(IfscUiState())
    val state = _state.asStateFlow()

    val favorites = favoriteDao.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val favoriteBranches = favoriteDao.observeAll()
        .map { list -> list.map { fav -> IFSCBankCatalog.findByIfsc(fav.ifsc) ?: fav.toBranch() } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        viewModelScope.launch {
            favoriteDao.observeAll().collect { list ->
                _state.update { it.copy(favoriteIfscs = list.map { f -> f.ifsc }.toSet()) }
            }
        }
    }

    fun setQuery(query: String) {
        _state.update {
            it.copy(
                query = query,
                results = if (query.isBlank()) emptyList() else IFSCBankCatalog.search(query)
            )
        }
    }

    fun isFavorite(ifsc: String): Boolean = ifsc.uppercase() in _state.value.favoriteIfscs.map { it.uppercase() }.toSet()

    fun toggleFavorite(branch: IfscBranch) {
        viewModelScope.launch {
            if (isFavorite(branch.ifsc)) {
                favoriteDao.delete(branch.ifsc.uppercase())
            } else {
                favoriteDao.insert(
                    IfscFavoriteEntity(
                        ifsc = branch.ifsc.uppercase(),
                        bankName = branch.bankName,
                        branch = branch.branch,
                        city = branch.city,
                        state = branch.state
                    )
                )
            }
        }
    }

    fun bankNames(): List<String> = IFSCBankCatalog.bankNames()

    fun branchesForBank(bankName: String): List<IfscBranch> = IFSCBankCatalog.branchesForBank(bankName)

    private fun IfscFavoriteEntity.toBranch() = IfscBranch(ifsc, bankName, branch, city, state)
}
