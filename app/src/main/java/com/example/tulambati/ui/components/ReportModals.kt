package com.example.tulambati.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PropaneTank
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tulambati.data.model.FuelStation
import com.example.tulambati.data.model.GasCenter
import com.example.ui.theme.StatusAvailableGreen
import com.example.ui.theme.StatusClosedRed
import com.example.ui.theme.StatusMediumAmber

@Composable
fun StationReportDialog(
    station: FuelStation,
    onDismiss: () -> Unit,
    onSubmit: (queueLevel: String, petrol: Boolean, diesel: Boolean, notes: String, reporter: String) -> Unit
) {
    var selectedQueue by remember { mutableStateOf(station.queueLevel) }
    var petrolAvailable by remember { mutableStateOf(station.petrolAvailable) }
    var dieselAvailable by remember { mutableStateOf(station.dieselAvailable) }
    var reporterName by remember { mutableStateOf("مواطن واعي") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
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
                Text(
                    text = "تحديث سريع لحالة الطلمبة",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = station.nameAr,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 13.sp
                )

                // Gamification reward banner
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Stars,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "ستحصل على +15 نقطة سمعة لدعم أهل منطقتك 🎖️",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Text(
                    text = "حالة الصف حالياً (سف السافين):",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )

                // Queue options
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    QueueOptionChip("EMPTY", "فاضي", selectedQueue == "EMPTY", Modifier.weight(1f)) {
                        selectedQueue = "EMPTY"
                    }
                    QueueOptionChip("SHORT", "خفيف", selectedQueue == "SHORT", Modifier.weight(1f)) {
                        selectedQueue = "SHORT"
                    }
                    QueueOptionChip("MEDIUM", "متوسط", selectedQueue == "MEDIUM", Modifier.weight(1f)) {
                        selectedQueue = "MEDIUM"
                    }
                    QueueOptionChip("LONG", "تقيل", selectedQueue == "LONG", Modifier.weight(1f)) {
                        selectedQueue = "LONG"
                    }
                }

                // Fuel toggles
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "بنزين سوبر متوفر؟", fontSize = 13.sp)
                    Switch(checked = petrolAvailable, onCheckedChange = { petrolAvailable = it })
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "جازولين (ديزل) متوفر؟", fontSize = 13.sp)
                    Switch(checked = dieselAvailable, onCheckedChange = { dieselAvailable = it })
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("ملاحظة سريعة (مثال: بنكك شغال / التانكر فرغ)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSubmit(selectedQueue, petrolAvailable, dieselAvailable, notes, reporterName)
                },
                modifier = Modifier.testTag("submit_station_report_btn")
            ) {
                Text("نشر التحديث فوراً (+15 XP)")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}

@Composable
fun GasReportDialog(
    center: GasCenter,
    onDismiss: () -> Unit,
    onSubmit: (stockLevel: String, refill: Boolean, exchange: Boolean, notes: String, reporter: String) -> Unit
) {
    var stockLevel by remember { mutableStateOf(center.stockQuantityLevel) }
    var refillAvailable by remember { mutableStateOf(center.isRefillAvailable) }
    var exchangeAvailable by remember { mutableStateOf(center.isExchangeAvailable) }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PropaneTank,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = "تحديث مركز غاز الطهي",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = center.nameAr,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 13.sp
                )

                Text(
                    text = "كمية الأسطوانات المتوفرة الآن:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    QueueOptionChip("HIGH", "وفرة", stockLevel == "HIGH", Modifier.weight(1f)) {
                        stockLevel = "HIGH"
                    }
                    QueueOptionChip("MEDIUM", "متوسط", stockLevel == "MEDIUM", Modifier.weight(1f)) {
                        stockLevel = "MEDIUM"
                    }
                    QueueOptionChip("LOW", "محدود", stockLevel == "LOW", Modifier.weight(1f)) {
                        stockLevel = "LOW"
                    }
                    QueueOptionChip("EMPTY", "نفد", stockLevel == "EMPTY", Modifier.weight(1f)) {
                        stockLevel = "EMPTY"
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "استبدال أسطوانات متاح؟", fontSize = 13.sp)
                    Switch(checked = exchangeAvailable, onCheckedChange = { exchangeAvailable = it })
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "تعبئة أسطوانات متاحة؟", fontSize = 13.sp)
                    Switch(checked = refillAvailable, onCheckedChange = { refillAvailable = it })
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("ملاحظة (مثال: مطابق للسعر الرسمي)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSubmit(stockLevel, refillAvailable, exchangeAvailable, notes, "كشاف الغاز")
                },
                modifier = Modifier.testTag("submit_gas_report_btn")
            ) {
                Text("تأكيد التقرير (+15 XP)")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}

@Composable
fun AddAlertDialog(
    onDismiss: () -> Unit,
    onSubmit: (areaName: String, category: String, notifyAvailable: Boolean, notifyShortQueue: Boolean) -> Unit
) {
    var areaName by remember { mutableStateOf("الخرطوم - شارع الستين والرياض") }
    var selectedCategory by remember { mutableStateOf("ALL") }
    var notifyAvailable by remember { mutableStateOf(true) }
    var notifyShortQueue by remember { mutableStateOf(true) }

    val presetAreas = listOf(
        "الخرطوم - شارع الستين والرياض",
        "الخرطوم - الخرطوم ٢ والمطار",
        "أم درمان - المهندسين والموردة",
        "أم درمان - الثورات وأم بدة",
        "بحري - كافوري وشمبات",
        "بورتسودان - السوق والميناء",
        "ود مدني - السوق الشعبي والحي البريطاني"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.NotificationsActive,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "تفعيل رادار التنبيهات الذكية",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "اختر منطقتك السكنية أو اكتبها ليصلك إشعار فوري عند توفر الوقود أو الغاز:",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = areaName,
                    onValueChange = { areaName = it },
                    label = { Text("اسم الحي أو المنطقة") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Quick presets
                Text(text = "أحياء سريعة الاختيار:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    presetAreas.take(3).forEach { preset ->
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { areaName = preset }
                        ) {
                            Text(
                                text = preset,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                            )
                        }
                    }
                }

                Text(text = "نوع الوقود أو الغاز المطلوب:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    QueueOptionChip("ALL", "الكل", selectedCategory == "ALL", Modifier.weight(1f)) {
                        selectedCategory = "ALL"
                    }
                    QueueOptionChip("PETROL", "بنزين", selectedCategory == "PETROL", Modifier.weight(1f)) {
                        selectedCategory = "PETROL"
                    }
                    QueueOptionChip("DIESEL", "جاز", selectedCategory == "DIESEL", Modifier.weight(1f)) {
                        selectedCategory = "DIESEL"
                    }
                    QueueOptionChip("GAS", "غاز طهي", selectedCategory == "GAS", Modifier.weight(1f)) {
                        selectedCategory = "GAS"
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "تنبيه عند توفر كميات جديدة", fontSize = 12.sp)
                    Switch(checked = notifyAvailable, onCheckedChange = { notifyAvailable = it })
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "تنبيه عند خفة الصف (أقل من ١٥ د)", fontSize = 12.sp)
                    Switch(checked = notifyShortQueue, onCheckedChange = { notifyShortQueue = it })
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (areaName.isNotBlank()) {
                        onSubmit(areaName, selectedCategory, notifyAvailable, notifyShortQueue)
                    }
                },
                modifier = Modifier.testTag("confirm_add_alert_btn")
            ) {
                Text("تفعيل التنبيه")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}

@Composable
private fun QueueOptionChip(
    key: String,
    label: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
        border = BorderStroke(
            1.dp,
            if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Box(
            modifier = Modifier.padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
