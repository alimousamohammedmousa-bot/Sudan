package com.example.tulambati.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "community_reports")
data class CommunityReport(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val targetType: String, // FUEL_STATION or GAS_CENTER
    val targetId: String,
    val targetName: String,
    val reportType: String, // PETROL_ARRIVED, DIESEL_AVAILABLE, QUEUE_SHORT, QUEUE_HEAVY, GAS_AVAILABLE, CLOSED, PRICE_COMPLIANT
    val queueLevel: String = "SHORT",
    val reporterName: String,
    val reporterRank: String = "مواطن نشط",
    val comment: String,
    val timestamp: Long = System.currentTimeMillis(),
    val upvotes: Int = 1,
    val downvotes: Int = 0,
    val isVerified: Boolean = true
)

@Entity(tableName = "area_alerts")
data class AreaAlert(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val areaName: String,
    val targetCategory: String = "ALL", // ALL, PETROL, DIESEL, GAS
    val notifyWhenAvailable: Boolean = true,
    val notifyWhenQueueShort: Boolean = true,
    val isEnabled: Boolean = true
)

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val displayName: String = "كشاف الخرطوم",
    val reputationScore: Int = 340,
    val rankTitle: String = "فارس الطلمبات",
    val reportsCount: Int = 12,
    val confirmedCount: Int = 38,
    val currentStreakDays: Int = 4,
    val isLowBandwidthMode: Boolean = false
)
