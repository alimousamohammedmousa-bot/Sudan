package com.example.tulambati.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tulambati.data.model.FuelStation
import com.example.tulambati.data.model.GasCenter
import com.example.ui.theme.StatusAvailableGreen
import com.example.ui.theme.StatusClosedRed
import com.example.ui.theme.StatusMediumAmber
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun InteractiveRadarMap(
    stations: List<FuelStation>,
    gasCenters: List<GasCenter>,
    selectedStation: FuelStation?,
    selectedGasCenter: GasCenter?,
    onSelectStation: (FuelStation?) -> Unit,
    onSelectGasCenter: (GasCenter?) -> Unit,
    onQuickReportStation: (FuelStation) -> Unit,
    onQuickReportGas: (GasCenter) -> Unit,
    modifier: Modifier = Modifier
) {
    var scale by remember { mutableFloatStateOf(1.0f) }
    var panOffset by remember { mutableStateOf(Offset.Zero) }
    var showOnlyFuel by remember { mutableStateOf(false) }
    var showOnlyGas by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "radar_anim")
    val radarAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radar_sweep"
    )
    val radarPulse by infiniteTransition.animateFloat(
        initialValue = 10f,
        targetValue = 60f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse_radius"
    )

    Box(modifier = modifier.fillMaxSize().background(Color(0xFF0F1E1B))) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        scale = (scale * zoom).coerceIn(0.6f, 3.5f)
                        panOffset = Offset(
                            x = (panOffset.x + pan.x).coerceIn(-1200f, 1200f),
                            y = (panOffset.y + pan.y).coerceIn(-1200f, 1200f)
                        )
                    }
                }
                .pointerInput(stations, gasCenters, scale, panOffset) {
                    detectTapGestures { tapOffset ->
                        val width = size.width.toFloat()
                        val height = size.height.toFloat()
                        val centerX = width / 2f + panOffset.x
                        val centerY = height / 2f + panOffset.y

                        var tappedStation: FuelStation? = null
                        var tappedGas: GasCenter? = null

                        if (!showOnlyGas) {
                            for (station in stations) {
                                val (pinX, pinY) = calculateScreenPos(
                                    station.latitude,
                                    station.longitude,
                                    centerX,
                                    centerY,
                                    scale,
                                    width,
                                    height
                                )
                                val dist = (Offset(pinX, pinY) - tapOffset).getDistance()
                                if (dist < 40f * scale) {
                                    tappedStation = station
                                    break
                                }
                            }
                        }

                        if (tappedStation == null && !showOnlyFuel) {
                            for (center in gasCenters) {
                                val (pinX, pinY) = calculateScreenPos(
                                    center.latitude,
                                    center.longitude,
                                    centerX,
                                    centerY,
                                    scale,
                                    width,
                                    height
                                )
                                val dist = (Offset(pinX, pinY) - tapOffset).getDistance()
                                if (dist < 40f * scale) {
                                    tappedGas = center
                                    break
                                }
                            }
                        }

                        if (tappedStation != null) {
                            onSelectStation(tappedStation)
                            onSelectGasCenter(null)
                        } else if (tappedGas != null) {
                            onSelectGasCenter(tappedGas)
                            onSelectStation(null)
                        } else {
                            onSelectStation(null)
                            onSelectGasCenter(null)
                        }
                    }
                }
        ) {
            val w = size.width
            val h = size.height
            val centerX = w / 2f + panOffset.x
            val centerY = h / 2f + panOffset.y

            // Draw Dark Topographic Radar Grid
            drawRadarGrid(centerX, centerY, w, h, scale, radarAngle, radarPulse)

            // Draw Nile River paths (Blue Nile, White Nile, Confluence at Khartoum)
            drawNileRivers(centerX, centerY, scale)

            // Draw Stations Pins
            if (!showOnlyGas) {
                for (station in stations) {
                    val (px, py) = calculateScreenPos(
                        station.latitude,
                        station.longitude,
                        centerX,
                        centerY,
                        scale,
                        w,
                        h
                    )
                    val isSelected = selectedStation?.id == station.id
                    drawStationPin(px, py, station, isSelected, scale)
                }
            }

            // Draw Gas Centers Pins
            if (!showOnlyFuel) {
                for (center in gasCenters) {
                    val (px, py) = calculateScreenPos(
                        center.latitude,
                        center.longitude,
                        centerX,
                        centerY,
                        scale,
                        w,
                        h
                    )
                    val isSelected = selectedGasCenter?.id == center.id
                    drawGasPin(px, py, center, isSelected, scale)
                }
            }
        }

        // Top Filter Bar on Map
        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Surface(
                color = Color(0xDD132521),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, Color(0x332DD4BF))
            ) {
                Row(
                    modifier = Modifier.padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    MapFilterButton(
                        text = "الكل",
                        active = !showOnlyFuel && !showOnlyGas,
                        onClick = { showOnlyFuel = false; showOnlyGas = false }
                    )
                    MapFilterButton(
                        text = "طلمبات وقود ⛽",
                        active = showOnlyFuel,
                        onClick = { showOnlyFuel = true; showOnlyGas = false }
                    )
                    MapFilterButton(
                        text = "مستودعات غاز 🔥",
                        active = showOnlyGas,
                        onClick = { showOnlyGas = true; showOnlyFuel = false }
                    )
                }
            }
        }

        // Map Zoom Controls & Recenter
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MapIconButton(
                icon = Icons.Default.Add,
                contentDescription = "تكبير الخريطة",
                onClick = { scale = (scale * 1.25f).coerceAtMost(3.5f) }
            )
            MapIconButton(
                icon = Icons.Default.Remove,
                contentDescription = "تصغير الخريطة",
                onClick = { scale = (scale / 1.25f).coerceAtLeast(0.6f) }
            )
            MapIconButton(
                icon = Icons.Default.CenterFocusStrong,
                contentDescription = "إعادة ضبط الموقع",
                onClick = {
                    scale = 1.0f
                    panOffset = Offset.Zero
                }
            )
        }

        // Selected Station / Gas Center Preview Card at Bottom
        if (selectedStation != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                StationCard(
                    station = selectedStation,
                    isLowBandwidthMode = false,
                    onCardClick = {},
                    onQuickReportClick = { onQuickReportStation(selectedStation) }
                )
            }
        } else if (selectedGasCenter != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                GasCenterCard(
                    center = selectedGasCenter,
                    isLowBandwidthMode = false,
                    onCardClick = {},
                    onQuickReportClick = { onQuickReportGas(selectedGasCenter) }
                )
            }
        } else {
            // Friendly Map Legend overlay
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp),
                color = Color(0xCC0D1F1C),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LegendItem(color = StatusAvailableGreen, label = "متوفر / صف خفيف")
                    LegendItem(color = StatusMediumAmber, label = "صف متوسط")
                    LegendItem(color = StatusClosedRed, label = "مغلق / صف تقيل")
                }
            }
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(text = label, color = Color(0xFFD1FAE5), fontSize = 10.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun MapFilterButton(text: String, active: Boolean, onClick: () -> Unit) {
    Surface(
        color = if (active) MaterialTheme.colorScheme.primary else Color.Transparent,
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier.clip(RoundedCornerShape(18.dp)).clickable(onClick = onClick)
    ) {
        Text(
            text = text,
            color = if (active) Color(0xFF042F2C) else Color(0xFFE2EBE8),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun MapIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    onClick: () -> Unit
) {
    Surface(
        color = Color(0xEE162B26),
        shape = CircleShape,
        border = BorderStroke(1.dp, Color(0x332DD4BF)),
        modifier = Modifier.size(38.dp).clip(CircleShape).clickable(onClick = onClick)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = Color(0xFF2DD4BF),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

private fun calculateScreenPos(
    lat: Double,
    lng: Double,
    centerX: Float,
    centerY: Float,
    scale: Float,
    w: Float,
    h: Float
): Pair<Float, Float> {
    // Relative positioning centered around Khartoum (lat: 15.6, lng: 32.53)
    val refLat = 15.60
    val refLng = 32.53
    val deltaLng = (lng - refLng) * 1200f * scale
    val deltaLat = -(lat - refLat) * 1200f * scale
    return Pair(centerX + deltaLng.toFloat(), centerY + deltaLat.toFloat())
}

private fun DrawScope.drawRadarGrid(
    centerX: Float,
    centerY: Float,
    w: Float,
    h: Float,
    scale: Float,
    radarAngle: Float,
    radarPulse: Float
) {
    val gridColor = Color(0x222DD4BF)
    val ringColor = Color(0x182DD4BF)

    // Range rings
    val radii = listOf(80f * scale, 180f * scale, 300f * scale, 450f * scale, 650f * scale)
    for (r in radii) {
        drawCircle(
            color = ringColor,
            radius = r,
            center = Offset(centerX, centerY),
            style = Stroke(width = 1.2f)
        )
    }

    // Expanding active pulse
    drawCircle(
        color = Color(0x222DD4BF),
        radius = radarPulse * 5f * scale,
        center = Offset(centerX, centerY),
        style = Stroke(width = 2f)
    )

    // Center locator
    drawCircle(
        color = Color(0xFF2DD4BF),
        radius = 5f,
        center = Offset(centerX, centerY)
    )

    // Radar rotating beam sweep
    val rad = Math.toRadians(radarAngle.toDouble())
    val endX = centerX + (320f * scale * cos(rad)).toFloat()
    val endY = centerY + (320f * scale * sin(rad)).toFloat()

    drawLine(
        color = Color(0x662DD4BF),
        start = Offset(centerX, centerY),
        end = Offset(endX, endY),
        strokeWidth = 2f
    )
}

private fun DrawScope.drawNileRivers(centerX: Float, centerY: Float, scale: Float) {
    val riverColor = Color(0x550284C7)
    val riverStroke = Stroke(width = 7f * scale)

    // White Nile coming from south
    val whiteNile = Path().apply {
        moveTo(centerX - 40f * scale, centerY + 280f * scale)
        quadraticBezierTo(
            centerX - 20f * scale,
            centerY + 120f * scale,
            centerX - 10f * scale,
            centerY + 10f * scale
        )
    }
    drawPath(whiteNile, riverColor, style = riverStroke)

    // Blue Nile coming from south-east
    val blueNile = Path().apply {
        moveTo(centerX + 180f * scale, centerY + 240f * scale)
        quadraticBezierTo(
            centerX + 90f * scale,
            centerY + 80f * scale,
            centerX - 10f * scale,
            centerY + 10f * scale
        )
    }
    drawPath(blueNile, riverColor, style = riverStroke)

    // Main Nile flowing north past Mogran
    val mainNile = Path().apply {
        moveTo(centerX - 10f * scale, centerY + 10f * scale)
        quadraticBezierTo(
            centerX - 30f * scale,
            centerY - 100f * scale,
            centerX - 20f * scale,
            centerY - 280f * scale
        )
    }
    drawPath(mainNile, riverColor, style = riverStroke)
}

private fun DrawScope.drawStationPin(
    px: Float,
    py: Float,
    station: FuelStation,
    isSelected: Boolean,
    scale: Float
) {
    val pinColor = when {
        !station.isOpen -> StatusClosedRed
        station.queueLevel == "EMPTY" || station.queueLevel == "SHORT" -> StatusAvailableGreen
        station.queueLevel == "MEDIUM" -> StatusMediumAmber
        else -> StatusClosedRed
    }

    val radius = if (isSelected) 18f * scale.coerceAtMost(1.8f) else 13f * scale.coerceAtMost(1.6f)

    // Outer glow for selected
    if (isSelected) {
        drawCircle(
            color = pinColor.copy(alpha = 0.35f),
            radius = radius + 10f,
            center = Offset(px, py)
        )
    }

    // Base pin
    drawCircle(
        color = Color(0xFF0F2621),
        radius = radius + 3f,
        center = Offset(px, py)
    )
    drawCircle(
        color = pinColor,
        radius = radius,
        center = Offset(px, py)
    )

    // Inner dot
    drawCircle(
        color = Color.White,
        radius = radius * 0.45f,
        center = Offset(px, py)
    )
}

private fun DrawScope.drawGasPin(
    px: Float,
    py: Float,
    center: GasCenter,
    isSelected: Boolean,
    scale: Float
) {
    val pinColor = when (center.stockQuantityLevel) {
        "HIGH" -> Color(0xFFFBBF24)
        "MEDIUM" -> StatusMediumAmber
        "LOW" -> Color(0xFFF97316)
        else -> StatusClosedRed
    }

    val radius = if (isSelected) 17f * scale.coerceAtMost(1.8f) else 12f * scale.coerceAtMost(1.6f)

    if (isSelected) {
        drawCircle(
            color = pinColor.copy(alpha = 0.35f),
            radius = radius + 10f,
            center = Offset(px, py)
        )
    }

    drawCircle(
        color = Color(0xFF281C0C),
        radius = radius + 3f,
        center = Offset(px, py)
    )
    drawCircle(
        color = pinColor,
        radius = radius,
        center = Offset(px, py)
    )
    drawCircle(
        color = Color(0xFF451A03),
        radius = radius * 0.45f,
        center = Offset(px, py)
    )
}
