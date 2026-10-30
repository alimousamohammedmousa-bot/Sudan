package com.example.tulambati.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material.icons.filled.SignalCellularConnectedNoInternet0Bar
import androidx.compose.material.icons.filled.WifiTethering
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.tulambati.data.model.FuelStation
import com.example.tulambati.data.model.GasCenter
import com.example.tulambati.ui.TulambatiViewModel
import com.example.tulambati.ui.components.AddAlertDialog
import com.example.tulambati.ui.components.GasReportDialog
import com.example.tulambati.ui.components.InteractiveRadarMap
import com.example.tulambati.ui.components.StationReportDialog
import com.example.ui.theme.StatusAvailableGreen
import com.example.ui.theme.StatusMediumAmber
import kotlinx.coroutines.flow.collectLatest

enum class TulambatiTab(val titleAr: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    STATIONS("الطلمبات", Icons.Default.LocalGasStation),
    GAS("غاز الطهي", Icons.Default.LocalFireDepartment),
    MAP("الرادار والخريطة", Icons.Default.Map),
    COMMUNITY("البلاغات", Icons.Default.NotificationsActive),
    PROFILE("حسابي", Icons.Default.AccountCircle)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TulambatiApp(
    viewModel: TulambatiViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val filteredStations by viewModel.filteredStations.collectAsStateWithLifecycle()
    val filteredGasCenters by viewModel.filteredGasCenters.collectAsStateWithLifecycle()
    val rawStations by viewModel.rawStations.collectAsStateWithLifecycle()
    val rawGasCenters by viewModel.rawGasCenters.collectAsStateWithLifecycle()
    val reports by viewModel.reports.collectAsStateWithLifecycle()
    val alerts by viewModel.alerts.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableStateOf(TulambatiTab.STATIONS) }
    var showGlobalReportPicker by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.toastEvents.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    val cities = listOf("الكل", "الخرطوم", "أم درمان", "بحري", "بورتسودان", "ود مدني", "كسلا")

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars),
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                // Main Header Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.LocalGasStation,
                                    contentDescription = "شعار طلمبتي",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "طلمبتي",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 19.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Tulambati",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = "دليلك الحي للوقود وغاز الطهي بالسودان 🇸🇩",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Low-Bandwidth Mode Quick Toggle Button
                    Surface(
                        color = if (uiState.isLowBandwidthMode) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .clickable {
                                viewModel.toggleLowBandwidthMode(!uiState.isLowBandwidthMode)
                            }
                            .testTag("low_bandwidth_toggle_chip")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.SignalCellularAlt,
                                contentDescription = null,
                                tint = if (uiState.isLowBandwidthMode) MaterialTheme.colorScheme.onPrimary
                                else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = if (uiState.isLowBandwidthMode) "وضع التوفير (مفعل)" else "توفير البيانات",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (uiState.isLowBandwidthMode) MaterialTheme.colorScheme.onPrimary
                                else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // City Selector Chips (shown on Stations, Gas, and Map tabs)
                if (selectedTab == TulambatiTab.STATIONS || selectedTab == TulambatiTab.GAS || selectedTab == TulambatiTab.MAP) {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(cities) { city ->
                            val isSelected = uiState.selectedCity == city
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.setSelectedCity(city) },
                                label = {
                                    Text(
                                        text = city,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                ),
                                modifier = Modifier.testTag("city_chip_$city")
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                TulambatiTab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.titleAr
                            )
                        },
                        label = {
                            Text(
                                text = tab.titleAr,
                                fontSize = 11.sp,
                                fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
                    )
                }
            }
        },
        floatingActionButton = {
            if (selectedTab == TulambatiTab.STATIONS || selectedTab == TulambatiTab.GAS) {
                ExtendedFloatingActionButton(
                    onClick = { showGlobalReportPicker = true },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = null
                        )
                    },
                    text = {
                        Text(
                            text = "إرسال بلاغ فوري ⚡",
                            fontWeight = FontWeight.Bold
                        )
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.testTag("global_report_fab")
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                TulambatiTab.STATIONS -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Visual Banner (when not in low-bandwidth mode)
                        if (!uiState.isLowBandwidthMode) {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 4.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(90.dp)
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.hero_banner),
                                        contentDescription = "طلمبات السودان",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(
                                                Brush.horizontalGradient(
                                                    colors = listOf(
                                                        Color(0xCC062E25),
                                                        Color(0x66062E25),
                                                        Color.Transparent
                                                    )
                                                )
                                            )
                                    )
                                    Column(
                                        modifier = Modifier
                                            .align(Alignment.CenterStart)
                                            .padding(12.dp)
                                    ) {
                                        Text(
                                            text = "متابعة الصفوف والوقود مباشرة",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = Color.White
                                        )
                                        Text(
                                            text = "بيانات فورية من أهل منطقتك في كل طلمبة",
                                            fontSize = 11.sp,
                                            color = Color(0xFFD1FAE5)
                                        )
                                    }
                                }
                            }
                        }

                        StationsTab(
                            stations = filteredStations,
                            searchQuery = uiState.searchQuery,
                            fuelFilter = uiState.fuelTypeFilter,
                            isLowBandwidthMode = uiState.isLowBandwidthMode,
                            onSearchChange = { viewModel.setSearchQuery(it) },
                            onFilterChange = { viewModel.setFuelTypeFilter(it) },
                            onStationClick = { viewModel.selectStation(it) },
                            onQuickReportClick = { viewModel.openQuickReportStation(it) }
                        )
                    }
                }

                TulambatiTab.GAS -> {
                    GasTab(
                        gasCenters = filteredGasCenters,
                        searchQuery = uiState.searchQuery,
                        gasFilter = uiState.gasFilter,
                        isLowBandwidthMode = uiState.isLowBandwidthMode,
                        onSearchChange = { viewModel.setSearchQuery(it) },
                        onFilterChange = { viewModel.setGasFilter(it) },
                        onGasCenterClick = { viewModel.selectGasCenter(it) },
                        onQuickReportClick = { viewModel.openQuickReportGasCenter(it) }
                    )
                }

                TulambatiTab.MAP -> {
                    InteractiveRadarMap(
                        stations = filteredStations,
                        gasCenters = filteredGasCenters,
                        selectedStation = uiState.selectedStation,
                        selectedGasCenter = uiState.selectedGasCenter,
                        onSelectStation = { viewModel.selectStation(it) },
                        onSelectGasCenter = { viewModel.selectGasCenter(it) },
                        onQuickReportStation = { viewModel.openQuickReportStation(it) },
                        onQuickReportGas = { viewModel.openQuickReportGasCenter(it) }
                    )
                }

                TulambatiTab.COMMUNITY -> {
                    CommunityTab(
                        reports = reports,
                        alerts = alerts,
                        onUpvoteReport = { viewModel.upvoteReport(it) },
                        onDownvoteReport = { viewModel.downvoteReport(it) },
                        onAddAlertClick = { viewModel.openAddAlertDialog() },
                        onToggleAlert = { id, enabled -> viewModel.toggleAlert(id, enabled) },
                        onDeleteAlert = { viewModel.deleteAlert(it) }
                    )
                }

                TulambatiTab.PROFILE -> {
                    ProfileTab(
                        profile = userProfile,
                        onToggleLowBandwidth = { viewModel.toggleLowBandwidthMode(it) }
                    )
                }
            }
        }
    }

    // Station Quick Report Dialog
    uiState.showQuickReportStation?.let { station ->
        StationReportDialog(
            station = station,
            onDismiss = { viewModel.closeQuickReportStation() },
            onSubmit = { queue, petrol, diesel, notes, reporter ->
                viewModel.submitStationReport(station, queue, petrol, diesel, notes, reporter)
            }
        )
    }

    // Gas Center Quick Report Dialog
    uiState.showQuickReportGasCenter?.let { center ->
        GasReportDialog(
            center = center,
            onDismiss = { viewModel.closeQuickReportGasCenter() },
            onSubmit = { stock, refill, exchange, notes, reporter ->
                viewModel.submitGasReport(center, stock, refill, exchange, notes, reporter)
            }
        )
    }

    // Add Alert Subscription Dialog
    if (uiState.showAddAlertDialog) {
        AddAlertDialog(
            onDismiss = { viewModel.closeAddAlertDialog() },
            onSubmit = { area, cat, notifyAvail, notifyQueue ->
                viewModel.addAreaAlert(area, cat, notifyAvail, notifyQueue)
            }
        )
    }

    // Global Report Target Picker (opened from FAB)
    if (showGlobalReportPicker) {
        AlertDialog(
            onDismissRequest = { showGlobalReportPicker = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text("أرسل بلاغاً فورياً للحي", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "اختر الموقع الذي تتواجد فيه الآن لتحديث حالته ومساعدة بقية المواطنين:",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(text = "طلمبات وقود قريبة:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    rawStations.take(3).forEach { st ->
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    showGlobalReportPicker = false
                                    viewModel.openQuickReportStation(st)
                                }
                        ) {
                            Text(
                                text = "⛽ ${st.nameAr}",
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)
                            )
                        }
                    }

                    Text(text = "مراكز غاز قريبة:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    rawGasCenters.take(2).forEach { gc ->
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    showGlobalReportPicker = false
                                    viewModel.openQuickReportGasCenter(gc)
                                }
                        ) {
                            Text(
                                text = "🔥 ${gc.nameAr}",
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                OutlinedButton(onClick = { showGlobalReportPicker = false }) {
                    Text("إلغاء")
                }
            }
        )
    }
}
