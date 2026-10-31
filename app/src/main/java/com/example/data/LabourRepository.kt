package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

class LabourRepository(private val db: AppDatabase) {
    private val workerDao = db.workerDao()
    private val attendanceDao = db.attendanceDao()
    private val paymentDao = db.paymentDao()

    val allWorkers = workerDao.getAllWorkers()
    val workerCount = workerDao.getWorkerCount()

    fun getPresentCountToday(date: Long) = attendanceDao.getPresentCountByDate(date)

    val totalWages = paymentDao.getTotalWages()
    val totalAdvances = paymentDao.getTotalAdvances()

    suspend fun addWorker(worker: Worker) = workerDao.insertWorker(worker)
    suspend fun updateWorker(worker: Worker) = workerDao.updateWorker(worker)
    suspend fun deleteWorker(worker: Worker) = workerDao.deleteWorker(worker)

    suspend fun markAttendance(attendance: Attendance) = attendanceDao.insertAttendance(attendance)

    suspend fun addPayment(payment: Payment) = paymentDao.insertPayment(payment)

    fun getWorkerDetails(workerId: Long) = workerDao.getWorkerByIdFlow(workerId)

    fun getAttendanceByDate(date: Long) = attendanceDao.getAttendanceByDate(date)

    fun getWorkerFinancialSummary(workerId: Long): Flow<WorkerFinancialSummary> {
        val workerFlow = workerDao.getWorkerByIdFlow(workerId)
        val attendanceFlow = attendanceDao.getAttendanceByWorker(workerId)
        val paymentsFlow = paymentDao.getPaymentsByWorker(workerId)

        return combine(workerFlow, attendanceFlow, paymentsFlow) { worker, attendance, payments ->
            if (worker == null) return@combine WorkerFinancialSummary(0.0, 0.0, 0.0)

            var totalEarned = 0.0
            attendance.forEach { att ->
                when (att.status) {
                    "PRESENT" -> totalEarned += worker.dailyWage
                    "HALF_DAY" -> totalEarned += worker.dailyWage / 2
                }
            }

            val totalPaid = payments.sumOf { it.amount }
            WorkerFinancialSummary(
                totalEarned = totalEarned,
                totalPaid = totalPaid,
                balance = totalEarned - totalPaid
            )
        }
    }
}

data class WorkerFinancialSummary(
    val totalEarned: Double,
    val totalPaid: Double,
    val balance: Double
)

