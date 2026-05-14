package com.example.nammamistri.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// --- THE SHARED MEMORY (Single Source of Truth) ---
object GlobalRates {
    var cementPrice by mutableStateOf("447")
    var brickPrice by mutableStateOf("10")
    var sandPrice by mutableStateOf("47")
}

@Composable
fun RatesScreen() {
    var isKannada by remember { mutableStateOf(false) }

    val syncTime = remember {
        SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBlack)
            .verticalScroll(rememberScrollState())
    ) {
        HeaderSectionRates(isKannada = isKannada, onToggleLanguage = { isKannada = !isKannada })

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isKannada) "ದರಗಳು" else "Rates",
                color = AppWhite, fontSize = 32.sp, fontWeight = FontWeight.Black, fontStyle = FontStyle.Italic,
                modifier = Modifier.drawBehind { drawLine(AppYellow, Offset(0f, size.height), Offset(size.width, size.height), 8.dp.toPx()) }
            )
        }

        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            Box(modifier = Modifier.matchParentSize().offset(8.dp, 8.dp).background(AppYellow))

            Column(modifier = Modifier.background(AppWhite).border(3.dp, AppBlack).padding(24.dp)) {
                Text(
                    text = if (isKannada) "ಪ್ರಸ್ತುತ ಮಾರುಕಟ್ಟೆ ದರಗಳು" else "CURRENT MARKET RATES",
                    fontWeight = FontWeight.Black, fontSize = 14.sp, color = Color.Gray
                )

                Spacer(Modifier.height(8.dp))
                Divider(color = AppBlack, thickness = 2.dp)
                Spacer(Modifier.height(24.dp))

                // READS AND WRITES DIRECTLY TO GLOBAL RATES
                RateInputRow(
                    label = if (isKannada) "ಸಿಮೆಂಟ್ ಬೆಲೆ (ಪ್ರತಿ ಚೀಲಕ್ಕೆ)" else "CEMENT PRICE (PER BAG)",
                    value = GlobalRates.cementPrice,
                    onValueChange = { GlobalRates.cementPrice = it }
                )
                Spacer(Modifier.height(16.dp))
                RateInputRow(
                    label = if (isKannada) "ಇಟ್ಟಿಗೆ ಬೆಲೆ (1000 ಕ್ಕೆ)" else "BRICK PRICE (PER 1000)",
                    value = GlobalRates.brickPrice,
                    onValueChange = { GlobalRates.brickPrice = it }
                )
                Spacer(Modifier.height(16.dp))
                RateInputRow(
                    label = if (isKannada) "ಮರಳು ಬೆಲೆ (ಪ್ರತಿ CFT)" else "SAND PRICE (PER CFT)",
                    value = GlobalRates.sandPrice,
                    onValueChange = { GlobalRates.sandPrice = it }
                )

                Spacer(Modifier.height(32.dp))
                Divider(color = AppBlack, thickness = 2.dp)
                Spacer(Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Text(text = if (isKannada) "ಸಿಂಕ್ ಆಗಿದೆ: $syncTime" else "SYNCED: $syncTime", fontWeight = FontWeight.Black, fontStyle = FontStyle.Italic, fontSize = 10.sp, color = Color.Gray)
                }
            }
        }
        Spacer(Modifier.height(32.dp))
    }
}

@Composable
fun RateInputRow(label: String, value: String, onValueChange: (String) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(text = label, color = Color.Gray, fontWeight = FontWeight.Black, fontSize = 14.sp, modifier = Modifier.weight(1f))
        Spacer(Modifier.width(16.dp))
        BasicTextField(
            value = value,
            onValueChange = { newValue -> if (newValue.isEmpty() || newValue.all { it.isDigit() }) onValueChange(newValue) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            textStyle = TextStyle(color = AppBlack, fontWeight = FontWeight.Black, fontSize = 24.sp, textAlign = TextAlign.Center),
            modifier = Modifier.width(100.dp).background(Color(0xFFF3F3F3)).border(3.dp, AppBlack).padding(vertical = 12.dp)
        )
    }
}

@Composable
fun HeaderSectionRates(isKannada: Boolean, onToggleLanguage: () -> Unit) {
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