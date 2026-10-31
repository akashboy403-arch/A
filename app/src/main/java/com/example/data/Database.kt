package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkerDao {
    @Query("SELECT * FROM workers ORDER BY name ASC")
    fun getAllWorkers(): Flow<List<Worker>>

    @Query("SELECT * FROM workers WHERE id = :id")
    suspend fun getWorkerById(id: Long): Worker?

    @Query("SELECT * FROM workers WHERE id = :id")
    fun getWorkerByIdFlow(id: Long): Flow<Worker?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorker(worker: Worker): Long

    @Update
    suspend fun updateWorker(worker: Worker)

    @Delete
    suspend fun deleteWorker(worker: Worker)

    @Query("SELECT COUNT(*) FROM workers")
    fun getWorkerCount(): Flow<Int>
}

@Dao
interface AttendanceDao {
    @Query("SELECT * FROM attendance WHERE date = :date")
    fun getAttendanceByDate(date: Long): Flow<List<Attendance>>

    @Query("SELECT * FROM attendance WHERE workerId = :workerId")
    fun getAttendanceByWorker(workerId: Long): Flow<List<Attendance>>

    @Query("SELECT * FROM attendance WHERE date BETWEEN :startDate AND :endDate")
    fun getAttendanceInRange(startDate: Long, endDate: Long): Flow<List<Attendance>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendance(attendance: Attendance)

    @Query("SELECT COUNT(*) FROM attendance WHERE date = :date AND status IN ('PRESENT', 'HALF_DAY')")
    fun getPresentCountByDate(date: Long): Flow<Int>
}

@Dao
interface PaymentDao {
    @Query("SELECT * FROM payments WHERE workerId = :workerId")
    fun getPaymentsByWorker(workerId: Long): Flow<List<Payment>>

    @Query("SELECT * FROM payments WHERE date BETWEEN :startDate AND :endDate")
    fun getPaymentsInRange(startDate: Long, endDate: Long): Flow<List<Payment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: Payment)

    @Query("SELECT SUM(amount) FROM payments WHERE type = 'WAGE'")
    fun getTotalWages(): Flow<Double?>

    @Query("SELECT SUM(amount) FROM payments WHERE type = 'ADVANCE'")
    fun getTotalAdvances(): Flow<Double?>
}

@Database(entities = [Worker::class, Attendance::class, Payment::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun workerDao(): WorkerDao
    abstract fun attendanceDao(): AttendanceDao
    abstract fun paymentDao(): PaymentDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: android.content.Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "labour_book_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
