package com.example.nammamistri.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.nammamistri.ui.theme.*
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Data Class for the Team Screen
data class Worker(
    val id: Int,
    val name: String,
    val rate: Int,
    val phone: String,
    var isPresent: Boolean? = null,
    var advance: String = "",
    val totalAdvance: Int = 0
)

@Composable
fun TeamScreen() {
    var isKannada by remember { mutableStateOf(false) }
    var showAddWorkerDialog by remember { mutableStateOf(false) }

    val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    val displayDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())

    // Initial state to match your screenshot
    val workers = remember {
        mutableStateListOf(
            Worker(1, "WORKER 2", 600, "9876543210", isPresent = true, advance = "", totalAdvance = 800),
            Worker(2, "WORKER 1", 600, "9876543210", isPresent = true, advance = "", totalAdvance = 900),
            Worker(3, "FFFF", 700, "8888", isPresent = false, advance = "", totalAdvance = 0),
            Worker(4, "UNKNOWN", 600, "-", isPresent = true, advance = "", totalAdvance = 0)
        )
    }

    // Dynamic Grand Total Calculation
    val grandTotalPaid = workers.sumOf { it.totalAdvance + (it.advance.toIntOrNull() ?: 0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBlack)
    ) {
        HeaderSectionTeam(isKannada = isKannada, onToggleLanguage = { isKannada = !isKannada })

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 32.dp)
        ) {

            Spacer(Modifier.height(16.dp))

            // 1. TOP SUMMARY CARD
            SummaryCard(isKannada = isKannada)

            // 2. TEAM TITLE & ADD BUTTON
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isKannada) "ತಂಡ" else "Team",
                    color = AppWhite,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    fontStyle = FontStyle.Italic,
                    modifier = Modifier.drawBehind {
                        drawLine(AppYellow, Offset(0f, size.height), Offset(size.width, size.height), 8.dp.toPx())
                    }
                )

                Button(
                    onClick = { showAddWorkerDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = AppYellow),
                    shape = RoundedCornerShape(0.dp),
                    border = BorderStroke(2.dp, AppBlack),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = if (isKannada) "ಕೆಲಸಗಾರ ಸೇರಿಸಿ" else "Add Worker",
                        color = AppBlack,
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp
                    )
                }
            }

            // 3. DATE BAR
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .background(Color(0xFF0A0A0A))
                    .padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = if (isKannada) "ದಿನಾಂಕ: " else "DATE: ", color = Color.Gray, fontWeight = FontWeight.Black, fontSize = 10.sp)
                    Text(text = displayDate, color = AppWhite, fontWeight = FontWeight.Black, fontSize = 12.sp)
                }
            }

            Spacer(Modifier.height(16.dp))

            // 4. WORKER CARDS
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                workers.forEach { worker ->
                    WorkerCard(
                        worker = worker,
                        isKannada = isKannada,
                        onUpdate = { updatedWorker ->
                            val index = workers.indexOfFirst { it.id == worker.id }
                            if (index != -1) workers[index] = updatedWorker
                        },
                        onDelete = { workerToDelete ->
                            workers.remove(workerToDelete)
                        }
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // 5. GRAND TOTAL PAID BOX (NEW)
            GrandTotalBox(total = grandTotalPaid, isKannada = isKannada)

            Spacer(Modifier.height(32.dp))

            // 6. ATTENDANCE LOG
            AttendanceLog(workers = workers, isKannada = isKannada, currentDate = currentDate)

            Spacer(Modifier.height(32.dp))
        }
    }

    if (showAddWorkerDialog) {
        AddWorkerDialog(
            isKannada = isKannada,
            onDismiss = { showAddWorkerDialog = false },
            onSave = { name, phone, wage ->
                val newId = (workers.maxOfOrNull { it.id } ?: 0) + 1
                workers.add(Worker(id = newId, name = name.uppercase(), phone = phone, rate = wage))
                showAddWorkerDialog = false
            }
        )
    }
}

@Composable
fun GrandTotalBox(total: Int, isKannada: Boolean) {
    val format = NumberFormat.getNumberInstance(Locale("en", "IN"))
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(AppBlack)
            .border(2.dp, AppYellow)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isKannada) "ಒಟ್ಟು ಪಾವತಿಸಲಾಗಿದೆ" else "GRAND TOTAL PAID",
                color = AppYellow,
                fontWeight = FontWeight.Black,
                fontSize = 12.sp
            )
            Text(
                text = "₹${format.format(total)}",
                color = AppYellow,
                fontWeight = FontWeight.Black,
                fontSize = 20.sp
            )
        }
    }
}

@Composable
fun SummaryCard(isKannada: Boolean) {
    var daysWorked by remember { mutableStateOf("0") }
    var dailyWage by remember { mutableStateOf("0") }
    var advanceTaken by remember { mutableStateOf("0") }

    val totalBalance = (daysWorked.toIntOrNull() ?: 0) * (dailyWage.toIntOrNull() ?: 0) - (advanceTaken.toIntOrNull() ?: 0)
    val isCleared = totalBalance <= 0
    val NeonGreen = Color(0xFF00FF7F)
    val format = NumberFormat.getNumberInstance(Locale("en", "IN"))

    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
        Column(
            modifier = Modifier.fillMaxWidth().background(AppWhite).border(3.dp, AppBlack).padding(16.dp)
        ) {
            Icon(Icons.Default.Assignment, contentDescription = null, tint = AppYellow, modifier = Modifier.size(16.dp))
            Spacer(Modifier.height(8.dp))
            Divider(color = AppBlack, thickness = 2.dp)
            Spacer(Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(Modifier.weight(1f)) { SummaryInput(if(isKannada) "ಕೆಲಸ ಮಾಡಿದ ದಿನಗಳು" else "DAYS WORKED", daysWorked) { daysWorked = it } }
                Box(Modifier.weight(1f)) { SummaryInput(if(isKannada) "ದೈನಂದಿನ ವೇತನ (₹)" else "DAILY WAGE (₹)", dailyWage) { dailyWage = it } }
                Box(Modifier.weight(1f)) { SummaryInput(if(isKannada) "ಪಡೆದ ಮುಂಗಡ (₹)" else "ADVANCE TAKEN (₹)", advanceTaken) { advanceTaken = it } }
            }

            Spacer(Modifier.height(16.dp))

            Box(
                modifier = Modifier.fillMaxWidth().background(if(isCleared) NeonGreen else AppYellow).border(2.dp, AppBlack).padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(if(isKannada) "ಒಟ್ಟು ಬಾಕಿ" else "TOTAL BALANCE DUE", fontSize = 8.sp, fontWeight = FontWeight.Black, color = AppBlack)
                    Text("₹${format.format(totalBalance)}", fontSize = 24.sp, fontWeight = FontWeight.Black, fontStyle = FontStyle.Italic, color = AppWhite, modifier = Modifier.drawBehind { drawLine(AppBlack, Offset(0f, size.height), Offset(size.width, size.height), 2.dp.toPx()) })
                    Spacer(Modifier.height(4.dp))
                    Text(if(isCleared) (if(isKannada) "ಸಂಪೂರ್ಣವಾಗಿ ಪಾವತಿಸಲಾಗಿದೆ" else "FULLY CLEARED") else (if(isKannada) "ಬಾಕಿ ಇದೆ" else "PENDING"), fontSize = 8.sp, fontWeight = FontWeight.Black, color = AppBlack)
                }
            }
        }
    }
}

@Composable
fun SummaryInput(label: String, value: String, onValueChange: (String) -> Unit) {
    Column {
        Text(label, fontSize = 7.sp, fontWeight = FontWeight.Black, color = Color.Gray)
        Spacer(Modifier.height(4.dp))
        BasicTextField(
            value = value,
            onValueChange = { if (it.isEmpty() || it.all { char -> char.isDigit() }) onValueChange(it) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            textStyle = TextStyle(color = AppBlack, fontWeight = FontWeight.Black, fontSize = 14.sp),
            modifier = Modifier.fillMaxWidth().border(1.dp, AppBlack).padding(8.dp)
        )
    }
}

@Composable
fun WorkerCard(worker: Worker, isKannada: Boolean, onUpdate: (Worker) -> Unit, onDelete: (Worker) -> Unit) {
    val CardDarkGrey = Color(0xFF1E1E1E)
    val NeonGreen = Color(0xFF00FF7F)

    Box {
        Box(modifier = Modifier.matchParentSize().offset(4.dp, 4.dp).background(Color.Black))

        Column(
            modifier = Modifier.fillMaxWidth().background(CardDarkGrey).border(2.dp, Color.Black).padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = worker.name, color = AppYellow, fontWeight = FontWeight.Black, fontSize = 16.sp, fontStyle = FontStyle.Italic)
                        Spacer(Modifier.width(8.dp))

                        Surface(color = AppYellow, border = BorderStroke(1.dp, AppBlack), shape = RoundedCornerShape(2.dp), modifier = Modifier.clickable { }) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)) {
                                Icon(Icons.Default.History, null, tint = AppBlack, modifier = Modifier.size(10.dp))
                                Spacer(Modifier.width(2.dp))
                                Text(if(isKannada) "ಇತಿಹಾಸ" else "History", color = AppBlack, fontWeight = FontWeight.Black, fontSize = 8.sp)
                            }
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(text = "₹${worker.rate}/DAY - ${worker.phone}", color = Color.Gray, fontWeight = FontWeight.Bold, fontSize = 9.sp)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    val presentBg = if (worker.isPresent == true) NeonGreen else AppWhite
                    val presentText = AppBlack

                    Surface(
                        color = presentBg, border = BorderStroke(2.dp, AppBlack), shape = RoundedCornerShape(0.dp),
                        modifier = Modifier.clickable { onUpdate(worker.copy(isPresent = true)) }
                    ) {
                        Text(text = if (isKannada) "ಹಾಜರ್" else "Present", color = presentText, fontWeight = FontWeight.Black, fontSize = 9.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                    }

                    val absentBg = if (worker.isPresent == false) AppRed else AppWhite
                    val absentText = if (worker.isPresent == false) AppWhite else AppBlack

                    Surface(
                        color = absentBg, border = BorderStroke(2.dp, AppBlack), shape = RoundedCornerShape(0.dp),
                        modifier = Modifier.clickable { onUpdate(worker.copy(isPresent = false)) }
                    ) {
                        Text(text = if (isKannada) "ಗೈರು" else "Absent", color = absentText, fontWeight = FontWeight.Black, fontSize = 9.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                    }

                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color.Gray,
                        modifier = Modifier.size(18.dp).clickable { onDelete(worker) }
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier.weight(1f).background(Color.Black).border(1.dp, Color(0xFF333333)).padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = if (isKannada) "ಮುಂಗಡ" else "ADVANCE", color = Color.Gray, fontWeight = FontWeight.Black, fontSize = 10.sp)

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("₹", color = AppYellow, fontWeight = FontWeight.Black, fontSize = 12.sp)
                            BasicTextField(
                                value = worker.advance,
                                onValueChange = { newValue ->
                                    if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                                        onUpdate(worker.copy(advance = newValue))
                                    }
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                textStyle = TextStyle(color = AppYellow, fontWeight = FontWeight.Black, fontSize = 12.sp, textAlign = TextAlign.End),
                                modifier = Modifier.width(40.dp)
                            )
                        }
                    }
                }

                Spacer(Modifier.width(16.dp))

                val liveTotalAdvance = worker.totalAdvance + (worker.advance.toIntOrNull() ?: 0)
                val format = NumberFormat.getNumberInstance(Locale("en", "IN"))

                Column(horizontalAlignment = Alignment.End) {
                    Text(text = if (isKannada) "ಒಟ್ಟು ಮುಂಗಡ" else "TOTAL ADV", color = Color.Gray, fontWeight = FontWeight.Black, fontSize = 7.sp)
                    Text(text = "₹${format.format(liveTotalAdvance)}", color = Color.LightGray, fontWeight = FontWeight.Black, fontSize = 10.sp)
                }
            }
        }
    }
}

@Composable
fun AttendanceLog(workers: List<Worker>, isKannada: Boolean, currentDate: String) {
    val loggedWorkers = workers.filter { it.isPresent != null }
    val presentCount = loggedWorkers.count { it.isPresent == true }
    val absentCount = loggedWorkers.count { it.isPresent == false }
    val NeonGreen = Color(0xFF00FF7F)

    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.History, null, tint = AppYellow, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(8.dp))
                Text(if(isKannada) "ಹಾಜರಾತಿ ಲಾಗ್" else "ATTENDANCE LOG", color = AppWhite, fontWeight = FontWeight.Black, fontSize = 14.sp, fontStyle = FontStyle.Italic)
            }
            Surface(color = AppYellow, shape = RoundedCornerShape(2.dp)) {
                Text("${loggedWorkers.size} ENTRIES", color = AppBlack, fontWeight = FontWeight.Black, fontSize = 8.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
            }
        }

        Spacer(Modifier.height(12.dp))

        Column(modifier = Modifier.fillMaxWidth().background(AppWhite).border(3.dp, AppBlack)) {
            Row(
                modifier = Modifier.fillMaxWidth().background(Color(0xFFF3F3F3)).padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.DateRange, null, tint = AppBlack, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(6.dp))
                    Surface(color = AppYellow, shape = RoundedCornerShape(4.dp), border = BorderStroke(1.dp, AppBlack)) {
                        Text(currentDate, color = AppBlack, fontWeight = FontWeight.Black, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Surface(color = AppWhite, border = BorderStroke(1.dp, NeonGreen), shape = RoundedCornerShape(12.dp)) {
                        Text("PRESENT: $presentCount", color = NeonGreen, fontWeight = FontWeight.Black, fontSize = 8.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                    Surface(color = AppWhite, border = BorderStroke(1.dp, AppRed), shape = RoundedCornerShape(12.dp)) {
                        Text("ABSENT: $absentCount", color = AppRed, fontWeight = FontWeight.Black, fontSize = 8.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                    Icon(Icons.Default.KeyboardArrowDown, null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                }
            }

            Divider(color = AppBlack, thickness = 2.dp)

            loggedWorkers.forEachIndexed { index, worker ->
                LogItemRow(worker = worker, isKannada = isKannada)
                if (index < loggedWorkers.size - 1) {
                    Divider(color = Color.LightGray, thickness = 1.dp)
                }
            }
        }
    }
}

@Composable
fun LogItemRow(worker: Worker, isKannada: Boolean) {
    val NeonGreen = Color(0xFF00FF7F)
    val isPresent = worker.isPresent == true
    val statusColor = if (isPresent) NeonGreen else AppRed

    Row(
        modifier = Modifier.fillMaxWidth().padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(color = statusColor, border = BorderStroke(1.dp, AppBlack), shape = RoundedCornerShape(2.dp), modifier = Modifier.size(24.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Text(worker.name.firstOrNull()?.toString() ?: "?", color = AppWhite, fontWeight = FontWeight.Black, fontSize = 12.sp)
                }
            }
            Spacer(Modifier.width(12.dp))
            Text(worker.name, color = AppBlack, fontWeight = FontWeight.Black, fontSize = 10.sp)
        }

        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            val adv = worker.advance.toIntOrNull() ?: 0
            if (adv > 0) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("ADVANCE", color = Color.Gray, fontSize = 6.sp, fontWeight = FontWeight.Black)
                    Surface(color = AppYellow, border = BorderStroke(1.dp, AppBlack), shape = RoundedCornerShape(2.dp)) {
                        Text("₹$adv", color = AppBlack, fontWeight = FontWeight.Black, fontSize = 8.sp, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                    }
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(if (isPresent) "PRESENT" else "ABSENT", color = statusColor, fontWeight = FontWeight.Black, fontSize = 8.sp)
                Spacer(Modifier.height(2.dp))
                Surface(color = statusColor, shape = CircleShape, border = BorderStroke(1.dp, AppBlack), modifier = Modifier.size(8.dp)) {}
            }
        }
    }
}

@Composable
fun AddWorkerDialog(isKannada: Boolean, onDismiss: () -> Unit, onSave: (String, String, Int) -> Unit) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var wage by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Box(modifier = Modifier.fillMaxWidth(0.95f).padding(10.dp)) {
            Box(modifier = Modifier.matchParentSize().offset(8.dp, 8.dp).background(AppYellow))

            Column(
                modifier = Modifier.background(AppWhite).border(3.dp, AppBlack).padding(20.dp).verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = if (isKannada) "ಕೆಲಸಗಾರ ಸೇರಿಸಿ" else "ADD WORKER",
                    fontWeight = FontWeight.Black, fontSize = 22.sp, fontStyle = FontStyle.Italic, color = AppBlack,
                    modifier = Modifier.drawBehind { drawLine(AppYellow, Offset(0f, size.height), Offset(size.width, size.height), 6.dp.toPx()) }
                )

                Spacer(Modifier.height(24.dp))

                FormInputTeam(if (isKannada) "ಕೆಲಸಗಾರನ ಹೆಸರು" else "WORKER NAME", "ENTER NAME", name, KeyboardType.Text) { name = it }
                FormInputTeam(if (isKannada) "ಫೋನ್ ಸಂಖ್ಯೆ" else "PHONE NUMBER", "9876...", phone, KeyboardType.Phone) { phone = it }
                FormInputTeam(if (isKannada) "ದೈನಂದಿನ ವೇತನ" else "DAILY WAGE", "600", wage, KeyboardType.Number) { wage = it }

                Spacer(Modifier.height(32.dp))

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(
                        onClick = onDismiss, modifier = Modifier.weight(1f).height(48.dp), border = BorderStroke(2.dp, AppBlack), shape = RoundedCornerShape(0.dp)
                    ) {
                        Text(if (isKannada) "ರದ್ದುಮಾಡಿ" else "CANCEL", color = AppBlack, fontWeight = FontWeight.Black)
                    }

                    Box(modifier = Modifier.weight(1f).height(48.dp)) {
                        Box(modifier = Modifier.matchParentSize().offset(4.dp, 4.dp).background(Color(0xFF00FF7F)))
                        Button(
                            onClick = {
                                val parsedWage = wage.toIntOrNull() ?: 0
                                if (name.isNotBlank() && parsedWage > 0) {
                                    onSave(name, phone, parsedWage)
                                }
                            },
                            modifier = Modifier.fillMaxSize(), colors = ButtonDefaults.buttonColors(containerColor = AppBlack), shape = RoundedCornerShape(0.dp)
                        ) {
                            Text(if (isKannada) "ಉಳಿಸಿ" else "SAVE", color = AppWhite, fontWeight = FontWeight.Black)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FormInputTeam(label: String, placeholder: String, value: String, keyboardType: KeyboardType, onValueChange: (String) -> Unit) {
    Column(Modifier.padding(vertical = 8.dp)) {
        Text(text = label, fontSize = 9.sp, fontWeight = FontWeight.Black, color = Color.DarkGray)
        Spacer(Modifier.height(4.dp))
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            textStyle = TextStyle(color = AppBlack, fontWeight = FontWeight.Bold, fontSize = 15.sp),
            modifier = Modifier.fillMaxWidth().background(Color.White).border(2.dp, AppBlack).padding(12.dp),
            decorationBox = { innerTextField ->
                if (value.isEmpty()) {
                    Text(placeholder, color = Color.LightGray, fontSize = 14.sp, fontWeight = FontWeight.Black)
                }
                innerTextField()
            }
        )
    }
}

@Composable
fun HeaderSectionTeam(isKannada: Boolean, onToggleLanguage: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().background(AppYellow).padding(16.dp).statusBarsPadding(),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(45.dp).background(AppBlack), contentAlignment = Alignment.Center) {
                Text("NM", color = AppYellow, fontWeight = FontWeight.Black, fontSize = 18.sp)
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(if (isKannada) "ಪ್ಲಾಟ್" else "PLOT", fontWeight = FontWeight.Black, fontSize = 20.sp, color = AppBlack)
                Text(if (isKannada) "ನಿರ್ಮಾಣ ಸಹಾಯಕ" else "CONSTRUCTION ASSISTANT", fontSize = 10.sp, color = AppBlack, fontWeight = FontWeight.Bold, modifier = Modifier.drawBehind { drawLine(AppBlack, Offset(0f, size.height), Offset(size.width, size.height), 2.dp.toPx()) })
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(
                color = AppBlack, shape = RoundedCornerShape(2.dp), modifier = Modifier.height(32.dp).clickable { onToggleLanguage() }
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 12.dp)) {
                    Text(if (isKannada) "ENG" else "ಕನ್ನಡ", color = AppYellow, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }

            Surface(
                color = AppBlack, shape = RoundedCornerShape(2.dp), modifier = Modifier.size(32.dp).clickable { /* Handle Logout */ }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.ExitToApp, null, tint = AppRed, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}