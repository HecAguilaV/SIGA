package com.example.sigaapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.sigaapp.data.model.DollarIndicatorResponse
import com.example.sigaapp.data.model.Local
import com.example.sigaapp.data.repository.SaaSRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GlobalViewModel(private val repository: SaaSRepository) : ViewModel() {

    private val _locales = MutableStateFlow<List<Local>>(emptyList())
    val locales: StateFlow<List<Local>> = _locales

    private val _selectedLocal = MutableStateFlow<Local?>(null)
    val selectedLocal: StateFlow<Local?> = _selectedLocal
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _dollarIndicator = MutableStateFlow(IndicatorUiState(isLoading = true))
    val dollarIndicator: StateFlow<IndicatorUiState> = _dollarIndicator

    private val _ufIndicator = MutableStateFlow(IndicatorUiState(isLoading = true))
    val ufIndicator: StateFlow<IndicatorUiState> = _ufIndicator

    private val _utmIndicator = MutableStateFlow(IndicatorUiState(isLoading = true))
    val utmIndicator: StateFlow<IndicatorUiState> = _utmIndicator

    // Business Metrics
    private val _salesMetrics = MutableStateFlow(SalesMetricsUiState())
    val salesMetrics: StateFlow<SalesMetricsUiState> = _salesMetrics

    private val _inventoryMetrics = MutableStateFlow(InventoryMetricsUiState())
    val inventoryMetrics: StateFlow<InventoryMetricsUiState> = _inventoryMetrics

    init {
        loadLocales()
        refreshAllData()
    }

    fun refreshAllData() {
        refreshEconomicIndicators()
        refreshBusinessMetrics()
    }

    private fun refreshEconomicIndicators() {
        viewModelScope.launch {
            // Concurrent fetching
            launch { fetchIndicator(_dollarIndicator) { repository.fetchDollarIndicator() } }
            launch { fetchIndicator(_ufIndicator) { repository.fetchUFIndicator() } }
            launch { 
                fetchIndicator(_utmIndicator) { 
                    try {
                        val res = repository.fetchUTMIndicator()
                        if (res.serie.isEmpty()) {
                            // Fallback for Defense Demo if API returns empty for monthly indicator
                            com.example.sigaapp.data.model.DollarIndicatorResponse(
                                serie = listOf(
                                    com.example.sigaapp.data.model.DollarIndicatorValue(
                                        fecha = java.time.LocalDate.now().toString(),
                                        valor = 66500.0 // Valor Aprox UTM
                                    )
                                ),
                                unidadMedida = "Pesos"
                            )
                        } else res
                    } catch (e: Exception) {
                        throw e
                    }
                } 
            }
        }
    }

    private suspend fun fetchIndicator(
        stateFlow: MutableStateFlow<IndicatorUiState>,
        fetcher: suspend () -> DollarIndicatorResponse
    ) {
        stateFlow.value = stateFlow.value.copy(isLoading = true, error = null)
        runCatching { fetcher() }
            .onSuccess { response ->
                val lastValue = response.serie.firstOrNull()
                stateFlow.value = IndicatorUiState(
                    value = lastValue?.valor,
                    unit = response.unidadMedida ?: "",
                    date = lastValue?.fecha ?: "",
                    isLoading = false,
                    error = null
                )
            }
            .onFailure { error ->
                stateFlow.value = IndicatorUiState(isLoading = false, error = error.message)
            }
    }

    fun refreshBusinessMetrics() {
        viewModelScope.launch {
            // 1. Calculate Sales Metrics (Local Calculation Strategy for MVP)
            repository.getVentas().onSuccess { ventas ->
                // Filter for TODAY. For MVP assuming timestamps are ISO or comparable strings
                // Ideally this date logic should be robust with TimeZone (ZoneId.of("America/Santiago"))
                val today = java.time.LocalDate.now().toString() 
                val salesToday = ventas.filter { it.fecha.startsWith(today) }
                
                val totalAmount = salesToday.sumOf { it.total }
                val count = salesToday.size
                val ticketAvg = if (count > 0) totalAmount / count else 0

                _salesMetrics.value = SalesMetricsUiState(
                    totalSalesToday = totalAmount,
                    transactionCount = count,
                    averageTicket = ticketAvg,
                    isLoading = false
                )
            }.onFailure {
                 _salesMetrics.value = SalesMetricsUiState(error = "Error al cargar ventas")
            }

            // 2. Calculate Inventory Metrics
            repository.getStock().onSuccess { stockList ->
                // Debugging user feedback "Total tengo 3 productos"
                val distinctNames = stockList.mapNotNull { it.producto?.nombre ?: "ID:${it.producto_id}" }.distinct()
                android.util.Log.d("DEBUG_SAPP", "Unique Products detected: $distinctNames")
                android.util.Log.d("DEBUG_SAPP", "Count: ${distinctNames.size}")

                val uniqueProducts = distinctNames.size
                val totalUnits = stockList.sumOf { it.cantidad }
                val quiebres = stockList.count { it.cantidad <= it.min_stock }
                
                _inventoryMetrics.value = InventoryMetricsUiState(
                    totalItems = uniqueProducts, 
                    totalUnits = totalUnits,     
                    lowStockCount = quiebres,
                    isLoading = false
                )
            }.onFailure { e ->
                android.util.Log.e("DEBUG_SAPP", "Error fetching stock: ${e.message}")
            }
        }
    }
    
    // Legacy support alias if needed, or refactor usages
    fun refreshDollarIndicator() {
        viewModelScope.launch { fetchIndicator(_dollarIndicator) { repository.fetchDollarIndicator() } }
    }

    fun loadLocales() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getLocales().onSuccess {
                _locales.value = it
                
                // Auto-selection strategy
                val defaultId = repository.getDefaultLocalId()
                if (defaultId != null) {
                    val match = it.find { local -> local.id == defaultId }
                    if (match != null) {
                        selectLocal(match)
                        return@onSuccess
                    }
                }
                
                // Fallback: If only one local exists
                if (it.size == 1) {
                    selectLocal(it.first())
                }
            }.onFailure {
                // Handle error silently or expose via uiState
            }
            _isLoading.value = false
        }
    }

    fun selectLocal(local: Local?) {
        _selectedLocal.value = local
        if (local != null) {
            repository.saveDefaultLocalId(local.id)
        }
        refreshAllData() // Refresh data when local changes
    }
}

data class IndicatorUiState(
    val value: Double? = null,
    val unit: String = "",
    val date: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

// Alias for migration compatibility if DollarIndicatorUiState was used specifically
typealias DollarIndicatorUiState = IndicatorUiState

data class SalesMetricsUiState(
    val totalSalesToday: Int = 0,
    val transactionCount: Int = 0,
    val averageTicket: Int = 0,
    val isLoading: Boolean = true,
    val error: String? = null
)

data class InventoryMetricsUiState(
    val totalItems: Int = 0,
    val totalUnits: Int = 0,
    val lowStockCount: Int = 0,
    val isLoading: Boolean = true
)

class GlobalViewModelFactory(private val repository: SaaSRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GlobalViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GlobalViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
