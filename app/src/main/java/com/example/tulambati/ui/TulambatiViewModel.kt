package com.example.tulambati.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tulambati.data.local.AppDatabase
import com.example.tulambati.data.local.InitialData
import com.example.tulambati.data.model.AreaAlert
import com.example.tulambati.data.model.CommunityReport
import com.example.tulambati.data.model.FuelStation
import com.example.tulambati.data.model.GasCenter
import com.example.tulambati.data.model.UserProfile
import com.example.tulambati.data.repository.TulambatiRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class TulambatiUiState(
    val selectedCity: String = "الكل",
    val searchQuery: String = "",
    val fuelTypeFilter: String = "ALL", // ALL, PETROL, DIESEL, OPEN_ONLY
    val gasFilter: String = "ALL", // ALL, REFILL, EXCHANGE, IN_STOCK
    val isLowBandwidthMode: Boolean = false,
    val selectedStation: FuelStation? = null,
    val selectedGasCenter: GasCenter? = null,
    val showQuickReportStation: FuelStation? = null,
    val showQuickReportGasCenter: GasCenter? = null,
    val showAddAlertDialog: Boolean = false
)

class TulambatiViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TulambatiRepository

    init {
        val database = AppDatabase.getDatabase(application, viewModelScope)
        repository = TulambatiRepository(database)
        viewModelScope.launch {
            repository.ensureDataPopulated()
        }
    }

    private val _uiState = MutableStateFlow(TulambatiUiState())
    val uiState: StateFlow<TulambatiUiState> = _uiState.asStateFlow()

    private val _toastEvents = MutableSharedFlow<String>()
    val toastEvents: SharedFlow<String> = _toastEvents.asSharedFlow()

    val rawStations: StateFlow<List<FuelStation>> = repository.stations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val rawGasCenters: StateFlow<List<GasCenter>> = repository.gasCenters
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val reports: StateFlow<List<CommunityReport>> = repository.reports
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val alerts: StateFlow<List<AreaAlert>> = repository.alerts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userProfile: StateFlow<UserProfile> = repository.userProfile
        .combine(_uiState) { profile, state ->
            val p = profile ?: InitialData.defaultProfile()
            if (p.isLowBandwidthMode != state.isLowBandwidthMode) {
                p.copy(isLowBandwidthMode = state.isLowBandwidthMode)
            } else {
                p
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), InitialData.defaultProfile())

    // Filtered Stations
    val filteredStations: StateFlow<List<FuelStation>> = combine(
        rawStations,
        _uiState
    ) { stationsList, state ->
        stationsList.filter { station ->
            val matchesCity = state.selectedCity == "الكل" || station.city.contains(state.selectedCity)
            val matchesSearch = state.searchQuery.isBlank() ||
                station.nameAr.contains(state.searchQuery, ignoreCase = true) ||
                station.name.contains(state.searchQuery, ignoreCase = true) ||
                station.district.contains(state.searchQuery, ignoreCase = true) ||
                station.brand.contains(state.searchQuery, ignoreCase = true)

            val matchesFuel = when (state.fuelTypeFilter) {
                "PETROL" -> station.petrolAvailable
                "DIESEL" -> station.dieselAvailable
                "OPEN_ONLY" -> station.isOpen
                else -> true
            }

            matchesCity && matchesSearch && matchesFuel
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered Gas Centers
    val filteredGasCenters: StateFlow<List<GasCenter>> = combine(
        rawGasCenters,
        _uiState
    ) { gasList, state ->
        gasList.filter { center ->
            val matchesCity = state.selectedCity == "الكل" || center.city.contains(state.selectedCity)
            val matchesSearch = state.searchQuery.isBlank() ||
                center.nameAr.contains(state.searchQuery, ignoreCase = true) ||
                center.name.contains(state.searchQuery, ignoreCase = true) ||
                center.district.contains(state.searchQuery, ignoreCase = true) ||
                center.brand.contains(state.searchQuery, ignoreCase = true)

            val matchesGas = when (state.gasFilter) {
                "REFILL" -> center.isRefillAvailable
                "EXCHANGE" -> center.isExchangeAvailable
                "IN_STOCK" -> center.stockQuantityLevel == "HIGH" || center.stockQuantityLevel == "MEDIUM"
                else -> true
            }

            matchesCity && matchesSearch && matchesGas
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSelectedCity(city: String) {
        _uiState.value = _uiState.value.copy(selectedCity = city)
    }

    fun setSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun setFuelTypeFilter(filter: String) {
        _uiState.value = _uiState.value.copy(fuelTypeFilter = filter)
    }

    fun setGasFilter(filter: String) {
        _uiState.value = _uiState.value.copy(gasFilter = filter)
    }

    fun toggleLowBandwidthMode(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(isLowBandwidthMode = enabled)
        viewModelScope.launch {
            repository.setLowBandwidthMode(enabled)
            val msg = if (enabled) "تم تشغيل وضع تقليل استهلاك البيانات للشبكات الضعيفة" else "تم إيقاف وضع توفير البيانات"
            _toastEvents.emit(msg)
        }
    }

    fun selectStation(station: FuelStation?) {
        _uiState.value = _uiState.value.copy(selectedStation = station)
    }

    fun selectGasCenter(center: GasCenter?) {
        _uiState.value = _uiState.value.copy(selectedGasCenter = center)
    }

    fun openQuickReportStation(station: FuelStation) {
        _uiState.value = _uiState.value.copy(showQuickReportStation = station)
    }

    fun closeQuickReportStation() {
        _uiState.value = _uiState.value.copy(showQuickReportStation = null)
    }

    fun openQuickReportGasCenter(center: GasCenter) {
        _uiState.value = _uiState.value.copy(showQuickReportGasCenter = center)
    }

    fun closeQuickReportGasCenter() {
        _uiState.value = _uiState.value.copy(showQuickReportGasCenter = null)
    }

    fun openAddAlertDialog() {
        _uiState.value = _uiState.value.copy(showAddAlertDialog = true)
    }

    fun closeAddAlertDialog() {
        _uiState.value = _uiState.value.copy(showAddAlertDialog = false)
    }

    fun submitStationReport(
        station: FuelStation,
        queueLevel: String,
        petrolAvailable: Boolean,
        dieselAvailable: Boolean,
        comment: String,
        reporterName: String
    ) {
        viewModelScope.launch {
            repository.submitQuickStationReport(
                station = station,
                queueLevel = queueLevel,
                petrolAvailable = petrolAvailable,
                dieselAvailable = dieselAvailable,
                comment = comment,
                reporterName = reporterName
            )
            closeQuickReportStation()
            _toastEvents.emit("تسلم يا بطل! تم نشر البلاغ وإضافة +15 نقطة لتقييمك 🏆")
        }
    }

    fun submitGasReport(
        center: GasCenter,
        stockLevel: String,
        refillAvailable: Boolean,
        exchangeAvailable: Boolean,
        comment: String,
        reporterName: String
    ) {
        viewModelScope.launch {
            repository.submitQuickGasReport(
                center = center,
                stockLevel = stockLevel,
                refillAvailable = refillAvailable,
                exchangeAvailable = exchangeAvailable,
                comment = comment,
                reporterName = reporterName
            )
            closeQuickReportGasCenter()
            _toastEvents.emit("تم تحديث حالة مستودع الغاز بنجاح +15 نقطة 🌟")
        }
    }

    fun upvoteReport(reportId: Long) {
        viewModelScope.launch {
            repository.upvoteReport(reportId)
            _toastEvents.emit("تم تأكيد مصداقية البلاغ +5 نقاط 🤝")
        }
    }

    fun downvoteReport(reportId: Long) {
        viewModelScope.launch {
            repository.downvoteReport(reportId)
            _toastEvents.emit("شكراً لملاحظتك، سيتم تدقيق البلاغ")
        }
    }

    fun addAreaAlert(areaName: String, category: String, notifyAvailable: Boolean, notifyShortQueue: Boolean) {
        viewModelScope.launch {
            repository.addAlert(areaName, category, notifyAvailable, notifyShortQueue)
            closeAddAlertDialog()
            _toastEvents.emit("تم تفعيل رادار التنبيهات لمنطقة: $areaName 🔔")
        }
    }

    fun toggleAlert(alertId: Long, enabled: Boolean) {
        viewModelScope.launch {
            repository.toggleAlert(alertId, enabled)
        }
    }

    fun deleteAlert(alertId: Long) {
        viewModelScope.launch {
            repository.deleteAlert(alertId)
            _toastEvents.emit("تم حذف التنبيه")
        }
    }
}
