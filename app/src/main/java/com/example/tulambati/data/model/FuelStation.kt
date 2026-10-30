package com.example.tulambati.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fuel_stations")
data class FuelStation(
    @PrimaryKey val id: String,
    val name: String,
    val nameAr: String,
    val brand: String,
    val city: String,
    val district: String,
    val latitude: Double,
    val longitude: Double,
    val isOpen: Boolean = true,
    val petrolAvailable: Boolean = true,
    val petrolPrice: Double = 1950.0,
    val dieselAvailable: Boolean = true,
    val dieselPrice: Double = 1880.0,
    val queueLevel: String = "SHORT", // EMPTY, SHORT, MEDIUM, LONG
    val waitMinutes: Int = 15,
    val paymentMethods: String = "BANKAK,CASH,FAWRY", // comma-separated
    val lastReportTimeMinutesAgo: Int = 8,
    val verifiedCount: Int = 24,
    val notes: String = "التانكر فرغ قبل نصف ساعة والصف متحرك بسلاسة"
)

enum class QueueSeverity(val labelAr: String, val labelEn: String) {
    EMPTY("فاضي تماماً", "No Queue"),
    SHORT("صف خفيف (٥-١٥ د)", "Short Queue (5-15 min)"),
    MEDIUM("صف متوسط (٣٠-٤٥ د)", "Medium Queue (30-45 min)"),
    LONG("صف تقيل (أكثر من ساعة)", "Heavy Queue (> 1 hour)")
}
