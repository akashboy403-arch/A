package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class LabourViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val repository = LabourRepository(db)
    private val adminManager = AdminManager(application)

    val allWorkers = repository.allWorkers.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val workerCount = repository.workerCount.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private val _selectedDate = MutableStateFlow(Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis)
    val selectedDate: StateFlow<Long> = _selectedDate.asStateFlow()

    val todayAttendance = _selectedDate.flatMapLatest { date ->
        repository.getAttendanceByDate(date)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val presentCountToday = _selectedDate.flatMapLatest { date ->
        repository.getPresentCountToday(date)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalWages = repository.totalWages.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)
    val totalAdvances = repository.totalAdvances.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val adminPassword = adminManager.adminPassword.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun addWorker(name: String, mobile: String, address: String, dailyWage: Double, workerId: String) {
        viewModelScope.launch {
            repository.addWorker(Worker(name = name, mobile = mobile, address = address, dailyWage = dailyWage, workerId = workerId))
        }
    }

    fun markAttendance(workerId: Long, status: String) {
        viewModelScope.launch {
            repository.markAttendance(Attendance(workerId = workerId, date = _selectedDate.value, status = status))
        }
    }

    fun addPayment(workerId: Long, amount: Double, type: String, notes: String?) {
        viewModelScope.launch {
            repository.addPayment(Payment(workerId = workerId, amount = amount, date = System.currentTimeMillis(), type = type, notes = notes))
        }
    }

    fun updateSelectedDate(date: Long) {
        _selectedDate.value = date
    }

    fun setAdminPassword(password: String) {
        viewModelScope.launch {
            adminManager.setPassword(password)
        }
    }

    fun getWorkerFinancialSummary(workerId: Long) = repository.getWorkerFinancialSummary(workerId)
}
