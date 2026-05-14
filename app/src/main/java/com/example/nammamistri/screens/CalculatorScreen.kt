package com.example.nammamistri.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
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
import com.example.nammamistri.ui.theme.*
import com.example.nammamistri.logic.CalculatorLogic
import com.example.nammamistri.logic.MaterialRates
import java.text.NumberFormat
import java.util.Locale

@Composable
fun CalculatorScreen() {
    // 1. UI States
    var isKannada by remember { mutableStateOf(false) }
    var showResults by remember { mutableStateOf(false) }

    // 2. Input States
    var length by remember { mutableStateOf("15") }
    var width by remember { mutableStateOf("10") }
    var height by remember { mutableStateOf("10") }

    // 3. Dropdown State
    var expanded by remember { mutableStateOf(false) }
    val thicknessOptions = listOf("9 INCH (Full Wall)", "4.5 INCH (Half Wall)")
    val thicknessOptionsKan = listOf("9 ಇಂಚು (ಪೂರ್ಣ ಗೋಡೆ)", "4.5 ಇಂಚು (ಅರ್ಧ ಗೋಡೆ)")
    var selectedThicknessIndex by remember { mutableStateOf(0) }

    // 4. Slider States
    var wastage by remember { mutableStateOf(5f) }
    var laborOverhead by remember { mutableStateOf(15f) }

    // 5. Result States for the UI
    var bricksReq by remember { mutableStateOf("0") }
    var cementReq by remember { mutableStateOf("0 BAGS") }
    var sandReq by remember { mutableStateOf("0 CFT") }
    var matCostStr by remember { mutableStateOf("₹0") }
    var wasLabCostStr by remember { mutableStateOf("₹0") }
    var totalCostStr by remember { mutableStateOf("₹0") }

    // THE MATH FUNCTION
    fun runCalculation() {
        // Parse user inputs safely
        val l = length.toDoubleOrNull() ?: 0.0
        val w = width.toDoubleOrNull() ?: 0.0
        val h = height.toDoubleOrNull() ?: 0.0
        val thicknessInches = if (selectedThicknessIndex == 0) 9.0 else 4.5

        // Fetch live rates exactly as typed from GlobalRates (No multipliers)
        val currentRates = MaterialRates(
            bricks = GlobalRates.brickPrice.toDoubleOrNull() ?: 10.0,
            cement = GlobalRates.cementPrice.toDoubleOrNull() ?: 444.0,
            sand = GlobalRates.sandPrice.toDoubleOrNull() ?: 43.0
        )

        // RUN THE EXTERNAL LOGIC
        val result = CalculatorLogic.calculateMaterials(
            length = l,
            width = w,
            height = h,
            thickness = thicknessInches,
            rates = currentRates,
            wastagePercent = wastage.toDouble(),
            laborPercent = laborOverhead.toDouble()
        )

        // Format the results for the UI
        val format = NumberFormat.getNumberInstance(Locale("en", "IN"))

        bricksReq = format.format(result.bricks)
        cementReq = "${format.format(result.cementBags)} ${if(isKannada) "ಚೀಲಗಳು" else "BAGS"}"
        sandReq = "${format.format(result.sandCft)} CFT"

        matCostStr = "₹${format.format(result.materialCost)}"
        wasLabCostStr = "₹${format.format(result.wastageCost + result.laborCost)}"
        totalCostStr = "₹${format.format(result.totalCost)}"

        // Reveal the results card!
        showResults = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBlack)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 32.dp)
    ) {
        // --- HEADER ---
        HeaderSectionCalc(isKannada = isKannada, onToggleLanguage = { isKannada = !isKannada })

        // --- CALCULATOR CARD ---
        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp)) {
            Box(modifier = Modifier.matchParentSize().offset(8.dp, 8.dp).background(AppYellow))

            Column(
                modifier = Modifier.background(AppWhite).border(3.dp, AppBlack).padding(24.dp)
            ) {
                Text(
                    text = if (isKannada) "ಕ್ಯಾಲ್ಕುಲೇಟರ್" else "CALCULATOR",
                    fontWeight = FontWeight.Black, fontSize = 28.sp, fontStyle = FontStyle.Italic, color = AppBlack,
                    modifier = Modifier.drawBehind { drawLine(AppYellow, Offset(0f, size.height), Offset(size.width, size.height), 6.dp.toPx()) }
                )
                Text(if (isKannada) "— ಸಾಮಾನ್ಯ" else "— GENERAL", color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)

                Spacer(Modifier.height(24.dp))

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Box(Modifier.weight(1f)) { CalcInput(if (isKannada) "ಉದ್ದ (FT)" else "LENGTH (FT)", length) { length = it; showResults = false } }
                    Box(Modifier.weight(1f)) { CalcInput(if (isKannada) "ಅಗಲ (FT)" else "WIDTH (FT)", width) { width = it; showResults = false } }
                }

                Spacer(Modifier.height(16.dp))
                Row(Modifier.fillMaxWidth()) {
                    Box(Modifier.weight(0.47f)) { CalcInput(if (isKannada) "ಎತ್ತರ (FT)" else "HEIGHT (FT)", height) { height = it; showResults = false } }
                    Spacer(Modifier.weight(0.53f))
                }

                Spacer(Modifier.height(16.dp))

                Text(if (isKannada) "ಗೋಡೆಯ ದಪ್ಪ (IN)" else "WALL THICKNESS (IN)", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color.Gray)
                Spacer(Modifier.height(4.dp))
                Box(modifier = Modifier.fillMaxWidth().background(Color.White).border(2.dp, AppBlack).clickable { expanded = true }.padding(12.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(if (isKannada) thicknessOptionsKan[selectedThicknessIndex] else thicknessOptions[selectedThicknessIndex], fontWeight = FontWeight.Bold, fontSize = 14.sp, color = AppBlack)
                        Icon(Icons.Default.KeyboardArrowDown, null, tint = AppBlack)
                    }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        thicknessOptions.indices.forEach { index ->
                            DropdownMenuItem(
                                text = { Text(if (isKannada) thicknessOptionsKan[index] else thicknessOptions[index], fontWeight = FontWeight.Bold) },
                                onClick = { selectedThicknessIndex = index; expanded = false; showResults = false }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))
                CalcSlider(if (isKannada) "ವ್ಯರ್ಥ (%): ${wastage.toInt()}%" else "WASTAGE (%): ${wastage.toInt()}%", wastage, 0f..20f) { wastage = it; showResults = false }
                Spacer(Modifier.height(8.dp))
                CalcSlider(if (isKannada) "ಕಾರ್ಮಿಕ ವೆಚ್ಚ (%): ${laborOverhead.toInt()}%" else "LABOR OVERHEAD (%): ${laborOverhead.toInt()}%", laborOverhead, 0f..50f) { laborOverhead = it; showResults = false }

                Spacer(Modifier.height(32.dp))

                // CALCULATE BUTTON
                Box(modifier = Modifier.fillMaxWidth().height(56.dp).clickable { runCalculation() }) {
                    Box(modifier = Modifier.matchParentSize().offset(4.dp, 4.dp).background(AppBlack))
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = AppYellow,
                        border = BorderStroke(3.dp, AppBlack),
                        shape = RoundedCornerShape(0.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(if (isKannada) "ಲೆಕ್ಕ ಹಾಕಿ" else "CALCULATE", color = AppBlack, fontWeight = FontWeight.Black, fontSize = 16.sp)
                        }
                    }
                }
            }
        }

        // --- RESULTS CARD ---
        if (showResults) {
            val NeonGreen = Color(0xFF00FF7F)

            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Box(modifier = Modifier.matchParentSize().offset(8.dp, 8.dp).background(NeonGreen))

                Column(modifier = Modifier.background(Color(0xFF121212)).border(3.dp, AppBlack).padding(24.dp)) {

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (isKannada) "ಫಲಿತಾಂಶಗಳು" else "RESULTS",
                            fontWeight = FontWeight.Black, fontSize = 24.sp, fontStyle = FontStyle.Italic, color = AppYellow,
                            modifier = Modifier.drawBehind { drawLine(AppYellow, Offset(0f, size.height), Offset(size.width, size.height), 4.dp.toPx()) }
                        )
                        Surface(color = NeonGreen, shape = RoundedCornerShape(2.dp)) {
                            Text("SUCCESS", color = AppBlack, fontWeight = FontWeight.Black, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                        }
                    }

                    Spacer(Modifier.height(32.dp))

                    ResultRow(if (isKannada) "ಬೇಕಾದ ಇಟ್ಟಿಗೆಗಳು" else "BRICKS REQUIRED", bricksReq)
                    Divider(color = Color.DarkGray, thickness = 1.dp, modifier = Modifier.padding(vertical = 12.dp))
                    ResultRow(if (isKannada) "ಸಿಮೆಂಟ್ ಚೀಲಗಳು" else "CEMENT BAGS", cementReq)
                    Divider(color = Color.DarkGray, thickness = 1.dp, modifier = Modifier.padding(vertical = 12.dp))
                    ResultRow(if (isKannada) "ಮರಳು (CFT)" else "SAND (CFT)", sandReq)

                    Spacer(Modifier.height(24.dp))
                    Divider(color = AppYellow, thickness = 2.dp, modifier = Modifier.padding(vertical = 12.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(if (isKannada) "ವಸ್ತುಗಳ ವೆಚ್ಚ" else "MATERIAL COST", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color.Gray)
                        Text(matCostStr, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AppWhite)
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(if (isKannada) "ವ್ಯರ್ಥ + ಕಾರ್ಮಿಕರು" else "WASTAGE + LABOR", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color.Gray)
                        Text(wasLabCostStr, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AppWhite)
                    }

                    Spacer(Modifier.height(24.dp))

                    Box(modifier = Modifier.fillMaxWidth().background(Color(0xFF0A0A0A)).padding(16.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(if (isKannada) "ಅಂದಾಜು ವೆಚ್ಚ" else "ESTIMATED COST", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color.Gray)
                            Text(totalCostStr, fontSize = 42.sp, fontWeight = FontWeight.Black, color = NeonGreen)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ResultRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.width(4.dp).height(24.dp).background(Color.DarkGray))
            Spacer(Modifier.width(8.dp))
            Text(label, fontSize = 10.sp, fontWeight = FontWeight.Black, color = AppYellow)
        }
        Text(value, fontSize = 24.sp, fontWeight = FontWeight.Black, color = AppYellow)
    }
}

@Composable
fun CalcInput(label: String, value: String, onValueChange: (String) -> Unit) {
    Column {
        Text(label, fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color.Gray)
        Spacer(Modifier.height(4.dp))
        BasicTextField(
            value = value, onValueChange = onValueChange, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            textStyle = TextStyle(color = AppBlack, fontWeight = FontWeight.Black, fontSize = 16.sp),
            modifier = Modifier.fillMaxWidth().background(Color.White).border(2.dp, AppBlack).padding(12.dp)
        )
    }
}

@Composable
fun CalcSlider(label: String, value: Float, range: ClosedFloatingPointRange<Float>, onValueChange: (Float) -> Unit) {
    Column {
        Text(label, fontSize = 10.sp, fontWeight = FontWeight.Black, color = AppBlack)
        Slider(
            value = value, onValueChange = onValueChange, valueRange = range,
            colors = SliderDefaults.colors(thumbColor = AppYellow, activeTrackColor = AppBlack, inactiveTrackColor = Color.LightGray),
            modifier = Modifier.height(24.dp)
        )
    }
}

@Composable
fun HeaderSectionCalc(isKannada: Boolean, onToggleLanguage: () -> Unit) {
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
                color = AppBlack, shape = RoundedCornerShape(2.dp), modifier = Modifier.size(32.dp).clickable { /* Handle Logout Later */ }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.ExitToApp, null, tint = AppRed, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}