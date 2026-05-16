package com.odoo.dh_android_tire_retread_production.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.odoo.dh_android_tire_retread_production.data.api.StationQueueItem
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Entity(tableName = "station_queue")
data class StationQueueEntity(
    @PrimaryKey val workorder_id: Int,
    val wo_number: String,
    val paper_wo: String?,
    val serial_number: String?,
    val customer_name: String?,
    val service_type: String?,
    val station_status: String,
    val station_result: String?,
    val last_update: String
)

@Dao
interface StationQueueDao {
    @Query("SELECT * FROM station_queue")
    suspend fun getAll(): List<StationQueueEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<StationQueueEntity>)

    @Query("DELETE FROM station_queue")
    suspend fun deleteAll()
}

@Database(entities = [StationQueueEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun stationQueueDao(): StationQueueDao
}
