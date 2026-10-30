package com.example.tulambati.data.repository

import com.example.tulambati.data.local.AppDatabase
import com.example.tulambati.data.local.InitialData
import com.example.tulambati.data.model.AreaAlert
import com.example.tulambati.data.model.CommunityReport
import com.example.tulambati.data.model.FuelStation
import com.example.tulambati.data.model.GasCenter
import com.example.tulambati.data.model.UserProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext

class TulambatiRepository(private val database: AppDatabase) {

    val stations: Flow<List<FuelStation>> = database.fuelStationDao().getAllStations()
    val gasCenters: Flow<List<GasCenter>> = database.gasCenterDao().getAllGasCenters()
    val reports: Flow<List<CommunityReport>> = database.communityReportDao().getAllReports()
    val alerts: Flow<List<AreaAlert>> = database.areaAlertDao().getAllAlerts()
    val userProfile: Flow<UserProfile?> = database.userProfileDao().getUserProfile()

    suspend fun ensureDataPopulated() = withContext(Dispatchers.IO) {
        val currentStations = stations.firstOrNull()
        if (currentStations.isNullOrEmpty()) {
            database.fuelStationDao().insertStations(InitialData.defaultStations())
            database.gasCenterDao().insertGasCenters(InitialData.defaultGasCenters())
            for (report in InitialData.defaultReports()) {
                database.communityReportDao().insertReport(report)
            }
            for (alert in InitialData.defaultAlerts()) {
                database.areaAlertDao().insertAlert(alert)
            }
            database.userProfileDao().saveUserProfile(InitialData.defaultProfile())
        }
    }

    suspend fun submitQuickStationReport(
        station: FuelStation,
        queueLevel: String,
        petrolAvailable: Boolean,
        dieselAvailable: Boolean,
        comment: String,
        reporterName: String
    ) = withContext(Dispatchers.IO) {
        database.fuelStationDao().updateQueue(station.id, queueLevel)
        database.fuelStationDao().updateAvailability(
            id = station.id,
            petrol = petrolAvailable,
            diesel = dieselAvailable,
            isOpen = petrolAvailable || dieselAvailable
        )

        val reportType = when {
            queueLevel == "EMPTY" || queueLevel == "SHORT" -> "QUEUE_SHORT"
            queueLevel == "LONG" -> "QUEUE_HEAVY"
            petrolAvailable -> "PETROL_ARRIVED"
            else -> "QUEUE_UPDATE"
        }

        val report = CommunityReport(
            targetType = "FUEL_STATION",
            targetId = station.id,
            targetName = station.nameAr,
            reportType = reportType,
            queueLevel = queueLevel,
            reporterName = reporterName.ifBlank { "مواطن واعي" },
            reporterRank = "كشاف نشط ⚡",
            comment = comment.ifBlank { "تحديث مباشر لحالة الطلمبة والصف" },
            timestamp = System.currentTimeMillis(),
            upvotes = 1,
            isVerified = true
        )
        database.communityReportDao().insertReport(report)
        database.userProfileDao().addReportPoints(15)
    }

    suspend fun submitQuickGasReport(
        center: GasCenter,
        stockLevel: String,
        refillAvailable: Boolean,
        exchangeAvailable: Boolean,
        comment: String,
        reporterName: String
    ) = withContext(Dispatchers.IO) {
        database.gasCenterDao().updateGasStock(
            id = center.id,
            stockLevel = stockLevel,
            refill = refillAvailable,
            exchange = exchangeAvailable
        )

        val report = CommunityReport(
            targetType = "GAS_CENTER",
            targetId = center.id,
            targetName = center.nameAr,
            reportType = if (stockLevel == "EMPTY") "CLOSED" else "GAS_AVAILABLE",
            queueLevel = if (stockLevel == "HIGH") "EMPTY" else "SHORT",
            reporterName = reporterName.ifBlank { "كشاف الغاز" },
            reporterRank = "صياد الغاز 🛡️",
            comment = comment.ifBlank { "تحديث فوري لتوفر غاز الطهي والاستبدال" },
            timestamp = System.currentTimeMillis(),
            upvotes = 1,
            isVerified = true
        )
        database.communityReportDao().insertReport(report)
        database.userProfileDao().addReportPoints(15)
    }

    suspend fun upvoteReport(reportId: Long) = withContext(Dispatchers.IO) {
        database.communityReportDao().upvoteReport(reportId)
        database.userProfileDao().addVerificationPoints(5)
    }

    suspend fun downvoteReport(reportId: Long) = withContext(Dispatchers.IO) {
        database.communityReportDao().downvoteReport(reportId)
    }

    suspend fun addAlert(
        areaName: String,
        targetCategory: String,
        notifyAvailable: Boolean,
        notifyShortQueue: Boolean
    ) = withContext(Dispatchers.IO) {
        val alert = AreaAlert(
            areaName = areaName,
            targetCategory = targetCategory,
            notifyWhenAvailable = notifyAvailable,
            notifyWhenQueueShort = notifyShortQueue,
            isEnabled = true
        )
        database.areaAlertDao().insertAlert(alert)
    }

    suspend fun toggleAlert(alertId: Long, enabled: Boolean) = withContext(Dispatchers.IO) {
        database.areaAlertDao().setAlertEnabled(alertId, enabled)
    }

    suspend fun deleteAlert(alertId: Long) = withContext(Dispatchers.IO) {
        database.areaAlertDao().deleteAlert(alertId)
    }

    suspend fun setLowBandwidthMode(enabled: Boolean) = withContext(Dispatchers.IO) {
        database.userProfileDao().setLowBandwidthMode(enabled)
    }
}
