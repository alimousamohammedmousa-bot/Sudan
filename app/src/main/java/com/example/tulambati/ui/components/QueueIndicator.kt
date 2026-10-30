package com.example.tulambati.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.LocalAtm
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BankakCrimson
import com.example.ui.theme.CashGreen
import com.example.ui.theme.FawryBlue
import com.example.ui.theme.StatusAvailableGreen
import com.example.ui.theme.StatusClosedRed
import com.example.ui.theme.StatusMediumAmber
import com.example.ui.theme.StatusOutGray

@Composable
fun QueueBadge(queueLevel: String, waitMinutes: Int, modifier: Modifier = Modifier) {
    val (bgColor, textColor, labelAr, icon) = when (queueLevel) {
        "EMPTY" -> Quadruple(
            Color(0xFFE8F8F0),
            StatusAvailableGreen,
            "فاضي (بدون صف)",
            Icons.Default.CheckCircle
        )
        "SHORT" -> Quadruple(
            Color(0xFFE6F7ED),
            Color(0xFF047857),
            "صف خفيف ($waitMinutes د)",
            Icons.Default.AccessTime
        )
        "MEDIUM" -> Quadruple(
            Color(0xFFFEF3C7),
            Color(0xFFB45309),
            "صف متوسط ($waitMinutes د)",
            Icons.Default.AccessTime
        )
        "LONG" -> Quadruple(
            Color(0xFFFEE2E2),
            StatusClosedRed,
            "صف تقيل ($waitMinutes د)",
            Icons.Default.Warning
        )
        else -> Quadruple(
            Color(0xFFF3F4F6),
            StatusOutGray,
            "غير محدد",
            Icons.Default.AccessTime
        )
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(20.dp),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(textColor)
            )
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(13.dp)
            )
            Text(
                text = labelAr,
                color = textColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun PaymentBadgesRow(paymentMethodsCsv: String, modifier: Modifier = Modifier) {
    val methods = paymentMethodsCsv.split(",").map { it.trim().uppercase() }
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        methods.forEach { method ->
            when (method) {
                "BANKAK" -> PaymentPill(label = "بنكك", color = BankakCrimson, icon = Icons.Default.PhoneAndroid)
                "FAWRY" -> PaymentPill(label = "فوري", color = FawryBlue, icon = Icons.Default.PhoneAndroid)
                "CASH" -> PaymentPill(label = "كاش", color = CashGreen, icon = Icons.Default.LocalAtm)
                "POS" -> PaymentPill(label = "نقاط بيع", color = Color(0xFF475569), icon = Icons.Default.CreditCard)
            }
        }
    }
}

@Composable
fun PaymentPill(label: String, color: Color, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Surface(
        color = color.copy(alpha = 0.12f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(12.dp)
            )
            Text(
                text = label,
                color = color,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
