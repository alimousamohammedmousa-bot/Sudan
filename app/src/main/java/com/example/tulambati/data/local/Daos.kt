package com.example.tulambati.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.tulambati.data.model.AreaAlert
import com.example.tulambati.data.model.CommunityReport
import com.example.tulambati.data.model.FuelStation
import com.example.tulambati.data.model.GasCenter
import com.example.tulambati.data.model.UserProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface FuelStationDao {
    @Query("SELECT * FROM fuel_stations ORDER BY isOpen DESC, verifiedCount DESC")
    fun getAllStations(): Flow<List<FuelStation>>

    @Query("SELECT * FROM fuel_stations WHERE id = :id")
    suspend fun getStationById(id: String): FuelStation?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStations(stations: List<FuelStation>)

    @Update
    suspend fun updateStation(station: FuelStation)

    @Query("UPDATE fuel_stations SET queueLevel = :queueLevel, lastReportTimeMinutesAgo = 1, verifiedCount = verifiedCount + 1 WHERE id = :id")
    suspend fun updateQueue(id: String, queueLevel: String)

    @Query("UPDATE fuel_stations SET petrolAvailable = :petrol, dieselAvailable = :diesel, isOpen = :isOpen, lastReportTimeMinutesAgo = 1 WHERE id = :id")
    suspend fun updateAvailability(id: String, petrol: Boolean, diesel: Boolean, isOpen: Boolean)
}

@Dao
interface GasCenterDao {
    @Query("SELECT * FROM gas_centers ORDER BY isOpen DESC, verifiedCount DESC")
    fun getAllGasCenters(): Flow<List<GasCenter>>

    @Query("SELECT * FROM gas_centers WHERE id = :id")
    suspend fun getCenterById(id: String): GasCenter?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGasCenters(centers: List<GasCenter>)

    @Update
    suspend fun updateGasCenter(center: GasCenter)

    @Query("UPDATE gas_centers SET stockQuantityLevel = :stockLevel, isRefillAvailable = :refill, isExchangeAvailable = :exchange, lastReportTimeMinutesAgo = 1 WHERE id = :id")
    suspend fun updateGasStock(id: String, stockLevel: String, refill: Boolean, exchange: Boolean)
}

@Dao
interface CommunityReportDao {
    @Query("SELECT * FROM community_reports ORDER BY timestamp DESC")
    fun getAllReports(): Flow<List<CommunityReport>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: CommunityReport): Long

    @Query("UPDATE community_reports SET upvotes = upvotes + 1 WHERE id = :id")
    suspend fun upvoteReport(id: Long)

    @Query("UPDATE community_reports SET downvotes = downvotes + 1 WHERE id = :id")
    suspend fun downvoteReport(id: Long)
}

@Dao
interface AreaAlertDao {
    @Query("SELECT * FROM area_alerts")
    fun getAllAlerts(): Flow<List<AreaAlert>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: AreaAlert): Long

    @Query("DELETE FROM area_alerts WHERE id = :id")
    suspend fun deleteAlert(id: Long)

    @Query("UPDATE area_alerts SET isEnabled = :enabled WHERE id = :id")
    suspend fun setAlertEnabled(id: Long, enabled: Boolean)
}

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getUserProfile(): Flow<UserProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserProfile(profile: UserProfile)

    @Query("UPDATE user_profile SET reputationScore = reputationScore + :points, reportsCount = reportsCount + 1 WHERE id = 1")
    suspend fun addReportPoints(points: Int = 15)

    @Query("UPDATE user_profile SET reputationScore = reputationScore + :points, confirmedCount = confirmedCount + 1 WHERE id = 1")
    suspend fun addVerificationPoints(points: Int = 5)

    @Query("UPDATE user_profile SET isLowBandwidthMode = :enabled WHERE id = 1")
    suspend fun setLowBandwidthMode(enabled: Boolean)
}
