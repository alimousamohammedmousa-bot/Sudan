package com.example.tulambati.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gas_centers")
data class GasCenter(
    @PrimaryKey val id: String,
    val name: String,
    val nameAr: String,
    val brand: String,
    val city: String,
    val district: String,
    val latitude: Double,
    val longitude: Double,
    val isOpen: Boolean = true,
    val isRefillAvailable: Boolean = true,
    val isExchangeAvailable: Boolean = true,
    val officialPrice: Double = 14500.0,
    val actualPrice: Double = 14500.0,
    val stockQuantityLevel: String = "HIGH", // HIGH, MEDIUM, LOW, EMPTY
    val workingHours: String = "8:00 ص - 5:30 م",
    val paymentMethods: String = "BANKAK,CASH,FAWRY",
    val phoneContact: String = "0912300000",
    val lastReportTimeMinutesAgo: Int = 12,
    val verifiedCount: Int = 19,
    val notes: String = "توزيع منظم بحسب أسبقية الحضور مع إيصال مالي رسمي"
)

enum class GasStockLevel(val labelAr: String, val labelEn: String) {
    HIGH("متوفر بوفرة", "High Stock"),
    MEDIUM("متوفر بكميات متوسطة", "Moderate Stock"),
    LOW("كميات محدودة جداً", "Limited Stock"),
    EMPTY("نفدت الأسطوانات حالياً", "Out of Stock")
}
