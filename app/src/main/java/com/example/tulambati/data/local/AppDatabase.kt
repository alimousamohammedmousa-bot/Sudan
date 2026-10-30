package com.example.tulambati.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.tulambati.data.model.AreaAlert
import com.example.tulambati.data.model.CommunityReport
import com.example.tulambati.data.model.FuelStation
import com.example.tulambati.data.model.GasCenter
import com.example.tulambati.data.model.UserProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        FuelStation::class,
        GasCenter::class,
        CommunityReport::class,
        AreaAlert::class,
        UserProfile::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun fuelStationDao(): FuelStationDao
    abstract fun gasCenterDao(): GasCenterDao
    abstract fun communityReportDao(): CommunityReportDao
    abstract fun areaAlertDao(): AreaAlertDao
    abstract fun userProfileDao(): UserProfileDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "tulambati_database.db"
                ).addCallback(DatabaseCallback(scope)).build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(database)
                    }
                }
            }

            suspend fun populateDatabase(db: AppDatabase) {
                db.fuelStationDao().insertStations(InitialData.defaultStations())
                db.gasCenterDao().insertGasCenters(InitialData.defaultGasCenters())
                for (report in InitialData.defaultReports()) {
                    db.communityReportDao().insertReport(report)
                }
                for (alert in InitialData.defaultAlerts()) {
                    db.areaAlertDao().insertAlert(alert)
                }
                db.userProfileDao().saveUserProfile(InitialData.defaultProfile())
            }
        }
    }
}
