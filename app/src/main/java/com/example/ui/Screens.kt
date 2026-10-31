package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.R
import com.example.data.Worker
import com.example.data.WorkerFinancialSummary
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DashboardScreen(viewModel: LabourViewModel, navController: NavController) {
    val workerCount by viewModel.workerCount.collectAsStateWithLifecycle()
    val presentCount by viewModel.presentCountToday.collectAsStateWithLifecycle()
    val totalWages by viewModel.totalWages.collectAsStateWithLifecycle()
    val totalAdvances by viewModel.totalAdvances.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(R.string.dashboard),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            StatCard(
                title = stringResource(R.string.total_workers),
                value = workerCount.toString(),
                icon = Icons.Default.People,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = stringResource(R.string.today_attendance),
                value = presentCount.toString(),
                icon = Icons.Default.CheckCircle,
                modifier = Modifier.weight(1f)
            )
        }

        StatCard(
            title = stringResource(R.string.total_payment),
            value = "₹ ${totalWages ?: 0.0}",
            icon = Icons.Default.Payments,
            modifier = Modifier.fillMaxWidth()
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            StatCard(
                title = stringResource(R.string.advance_payment),
                value = "₹ ${totalAdvances ?: 0.0}",
                icon = Icons.Default.AccountBalanceWallet,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = stringResource(R.string.monthly_expenses),
                value = "₹ ${(totalWages ?: 0.0) + (totalAdvances ?: 0.0)}",
                icon = Icons.Default.TrendingUp,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        LargeMenuButton(
            text = stringResource(R.string.worker_management),
            icon = Icons.Default.PersonAdd,
            onClick = { navController.navigate("worker_list") }
        )
        LargeMenuButton(
            text = stringResource(R.string.attendance_management),
            icon = Icons.Default.EventNote,
            onClick = { navController.navigate("attendance") }
        )
        LargeMenuButton(
            text = stringResource(R.string.reports),
            icon = Icons.Default.BarChart,
            onClick = { navController.navigate("reports") }
        )
    }
}

@Composable
fun StatCard(title: String, value: String, icon: ImageVector, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = title, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            Text(text = value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun LargeMenuButton(text: String, icon: ImageVector, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Icon(imageVector = icon, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkerListScreen(viewModel: LabourViewModel, navController: NavController) {
    val workers by viewModel.allWorkers.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }

    val filteredWorkers = workers.filter {
        it.name.contains(searchQuery, ignoreCase = true) || it.workerId.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text(stringResource(R.string.worker_management)) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = null)
                        }
                    }
                )
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = { Text(stringResource(R.string.search_worker)) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("add_worker") }) {
                Icon(Icons.Default.Add, contentDescription = null)
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredWorkers) { worker ->
                WorkerItem(worker, onClick = { navController.navigate("worker_details/${worker.id}") })
            }
        }
    }
}

@Composable
fun WorkerItem(worker: Worker, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = worker.name.firstOrNull()?.toString() ?: "?",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = worker.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(text = "ID: ${worker.workerId}", style = MaterialTheme.typography.bodySmall)
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(text = "₹ ${worker.dailyWage}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddWorkerScreen(viewModel: LabourViewModel, navController: NavController) {
    var name by remember { mutableStateOf("") }
    var mobile by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var dailyWage by remember { mutableStateOf("") }
    var workerId by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.add_new_worker)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text(stringResource(R.string.worker_name)) }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = mobile, onValueChange = { mobile = it }, label = { Text(stringResource(R.string.mobile_number)) }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone))
            OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text(stringResource(R.string.address)) }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = dailyWage, onValueChange = { dailyWage = it }, label = { Text(stringResource(R.string.daily_wage)) }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            OutlinedTextField(value = workerId, onValueChange = { workerId = it }, label = { Text(stringResource(R.string.worker_id)) }, modifier = Modifier.fillMaxWidth())

            Button(
                onClick = {
                    viewModel.addWorker(name, mobile, address, dailyWage.toDoubleOrNull() ?: 0.0, workerId)
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(stringResource(R.string.save))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceScreen(viewModel: LabourViewModel, navController: NavController) {
    val workers by viewModel.allWorkers.collectAsStateWithLifecycle()
    val attendanceList by viewModel.todayAttendance.collectAsStateWithLifecycle()
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()

    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.attendance_management)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CalendarToday, contentDescription = null)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(text = sdf.format(Date(selectedDate)), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
            }

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(workers) { worker ->
                    val status = attendanceList.find { it.workerId == worker.id }?.status ?: "NONE"
                    AttendanceItem(worker, status, onStatusChange = { viewModel.markAttendance(worker.id, it) })
                }
            }
        }
    }
}

@Composable
fun AttendanceItem(worker: Worker, currentStatus: String, onStatusChange: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = worker.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                AttendanceButton(text = stringResource(R.string.present), isSelected = currentStatus == "PRESENT", color = Color(0xFF4CAF50)) { onStatusChange("PRESENT") }
                AttendanceButton(text = stringResource(R.string.half_day), isSelected = currentStatus == "HALF_DAY", color = Color(0xFFFFC107)) { onStatusChange("HALF_DAY") }
                AttendanceButton(text = stringResource(R.string.absent), isSelected = currentStatus == "ABSENT", color = Color(0xFFF44336)) { onStatusChange("ABSENT") }
            }
        }
    }
}

@Composable
fun AttendanceButton(text: String, isSelected: Boolean, color: Color, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) color else Color.LightGray,
            contentColor = if (isSelected) Color.White else Color.Black
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.width(100.dp),
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(text = text, fontSize = 12.sp)
    }
}

@Composable
fun LoginScreen(viewModel: LabourViewModel, onLoginSuccess: () -> Unit) {
    var password by remember { mutableStateOf("") }
    val savedPassword by viewModel.adminPassword.collectAsStateWithLifecycle()
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(24.dp))
        Text(text = stringResource(R.string.admin_login), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(32.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it; error = null },
            label = { Text(stringResource(R.string.password)) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            isError = error != null
        )
        if (error != null) {
            Text(text = error!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                if (savedPassword == null) {
                    // First time login, set password
                    viewModel.setAdminPassword(password)
                    onLoginSuccess()
                } else if (password == savedPassword) {
                    onLoginSuccess()
                } else {
                    error = "Invalid Password"
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(stringResource(if (savedPassword == null) R.string.save else R.string.login))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkerDetailsScreen(workerId: Long, viewModel: LabourViewModel, navController: NavController) {
    val workers by viewModel.allWorkers.collectAsStateWithLifecycle()
    val worker = workers.find { it.id == workerId }
    val financialSummary by viewModel.getWorkerFinancialSummary(workerId).collectAsStateWithLifecycle(initialValue = WorkerFinancialSummary(0.0, 0.0, 0.0))

    var showPaymentDialog by remember { mutableStateOf(false) }

    if (worker == null) return

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(worker.name) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = stringResource(R.string.remaining_balance), style = MaterialTheme.typography.titleMedium)
                    Text(text = "₹ ${financialSummary.balance}", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                StatCard(title = stringResource(R.string.total_earned), value = "₹ ${financialSummary.totalEarned}", icon = Icons.Default.TrendingUp, modifier = Modifier.weight(1f))
                StatCard(title = stringResource(R.string.total_paid), value = "₹ ${financialSummary.totalPaid}", icon = Icons.Default.Payments, modifier = Modifier.weight(1f))
            }

            HorizontalDivider()

            Text(text = "Contact: ${worker.mobile}", style = MaterialTheme.typography.bodyLarge)
            Text(text = "Address: ${worker.address}", style = MaterialTheme.typography.bodyLarge)
            Text(text = "Daily Wage: ₹ ${worker.dailyWage}", style = MaterialTheme.typography.bodyLarge)

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { showPaymentDialog = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(stringResource(R.string.advance_payment))
            }
            
            val context = LocalContext.current
            OutlinedButton(
                onClick = {
                    val intent = android.content.Intent(android.content.Intent.ACTION_VIEW).apply {
                        data = android.net.Uri.parse("https://api.whatsapp.com/send?phone=${worker.mobile}&text=Hello ${worker.name}, this is a reminder for your pending payment of ₹${financialSummary.balance} in Labour Book.")
                    }
                    context.startActivity(intent)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Share, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.whatsapp_reminder))
            }
        }
    }

    if (showPaymentDialog) {
        AddPaymentDialog(
            onDismiss = { showPaymentDialog = false },
            onConfirm = { amount, type, notes ->
                viewModel.addPayment(workerId, amount, type, notes)
                showPaymentDialog = false
            }
        )
    }
}

@Composable
fun AddPaymentDialog(onDismiss: () -> Unit, onConfirm: (Double, String, String?) -> Unit) {
    var amount by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("ADVANCE") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.advance_payment)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = amount, onValueChange = { amount = it }, label = { Text("Amount") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Notes (Optional)") })
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(amount.toDoubleOrNull() ?: 0.0, type, notes.ifBlank { null }) }) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(viewModel: LabourViewModel, navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.reports)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Text("Reports and Export feature coming soon.")
            // Implementation of PDF/Excel export would go here using external libraries
        }
    }
}
