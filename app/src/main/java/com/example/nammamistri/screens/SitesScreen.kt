package com.example.nammamistri.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.nammamistri.model.SiteProject
import com.example.nammamistri.model.SiteStatus
import com.example.nammamistri.ui.theme.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.NumberFormat
import java.util.Locale

@Composable
fun SitesScreen(onLogout: () -> Unit, onSiteSelected: (String) -> Unit) {
    val context = LocalContext.current
    var isKannada by remember { mutableStateOf(false) }
    var showAddSiteDialog by remember { mutableStateOf(false) }

    // 🔥 LOAD FROM HARD DRIVE: This grabs your saved sites the moment the screen opens!
    val sites = remember {
        mutableStateListOf<SiteProject>().apply { addAll(loadSitesLocally(context)) }
    }

    Column(modifier = Modifier.fillMaxSize().background(AppBlack)) {
        HeaderSectionSites(isKannada, { isKannada = !isKannada }, onLogoutClick = onLogout)

        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(bottom = 32.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isKannada) "ಸೈಟ್‌ಗಳು" else "Sites",
                    color = AppWhite, fontSize = 32.sp, fontWeight = FontWeight.Black, fontStyle = FontStyle.Italic,
                    modifier = Modifier.drawBehind { drawLine(AppYellow, Offset(0f, size.height), Offset(size.width, size.height), 8.dp.toPx()) }
                )

                Surface(
                    color = AppYellow,
                    modifier = Modifier.size(40.dp).clickable { showAddSiteDialog = true },
                    border = BorderStroke(2.dp, AppBlack)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Add, null, tint = AppBlack, modifier = Modifier.size(24.dp))
                    }
                }
            }

            Column(modifier = Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
                if (sites.isEmpty()) {
                    Text(
                        if(isKannada) "ಇನ್ನೂ ಯಾವುದೇ ಸೈಟ್‌ಗಳಿಲ್ಲ. ಸೇರಿಸಲು '+' ಕ್ಲಿಕ್ ಮಾಡಿ!" else "No sites yet. Click '+' to add one!",
                        color = Color.Gray, modifier = Modifier.padding(16.dp))
                } else {
                    sites.forEach { site ->
                        SiteCard(
                            site = site, isKannada = isKannada,
                            onDelete = {
                                sites.remove(site)
                                saveSitesLocally(context, sites) // 🔥 SAVE ON DELETE
                                Toast.makeText(context, "Site Deleted", Toast.LENGTH_SHORT).show()
                            },
                            onUpdateStatus = { newStatus ->
                                val index = sites.indexOfFirst { it.id == site.id }
                                if (index != -1) {
                                    sites[index] = site.copy(status = newStatus.name)
                                    saveSitesLocally(context, sites) // 🔥 SAVE ON UPDATE
                                }
                            },
                            onMapsClick = { Toast.makeText(context, "Opening Map...", Toast.LENGTH_SHORT).show() },
                            onSelect = { onSiteSelected(site.id) }
                        )
                    }
                }
            }
            Spacer(Modifier.height(32.dp))
        }
    }

    if (showAddSiteDialog) {
        AddSiteDialog(
            isKannada = isKannada,
            onDismiss = { showAddSiteDialog = false },
            onSave = { newSite ->
                newSite.id = java.util.UUID.randomUUID().toString()
                sites.add(newSite)
                saveSitesLocally(context, sites) // 🔥 SAVE ON NEW SITE
                Toast.makeText(context, "Site Saved Locally!", Toast.LENGTH_SHORT).show()
                showAddSiteDialog = false
            }
        )
    }
}

// --- LOCAL STORAGE HELPER FUNCTIONS ---

// Converts the list to JSON and saves it to the phone's internal storage
private fun saveSitesLocally(context: Context, sites: List<SiteProject>) {
    val prefs = context.getSharedPreferences("NammaMistriPrefs", Context.MODE_PRIVATE)
    val jsonString = Gson().toJson(sites)
    prefs.edit().putString("saved_sites", jsonString).apply()
}

// Reads the JSON from the phone's internal storage and converts it back to a list
private fun loadSitesLocally(context: Context): List<SiteProject> {
    val prefs = context.getSharedPreferences("NammaMistriPrefs", Context.MODE_PRIVATE)
    val jsonString = prefs.getString("saved_sites", null)

    return if (jsonString != null) {
        val type = object : TypeToken<List<SiteProject>>() {}.type
        Gson().fromJson(jsonString, type)
    } else {
        emptyList()
    }
}

// ... Keep your exact existing SiteCard, AddSiteDialog, FormInputSite, and HeaderSectionSites below this line ...

@Composable
fun SiteCard(site: SiteProject, isKannada: Boolean, onDelete: () -> Unit, onUpdateStatus: (SiteStatus) -> Unit, onMapsClick: () -> Unit, onSelect: () -> Unit) {
    val CardBg = AppWhite
    val InfoBoxBg = Color(0xFFEFEFEF)
    val NeonGreen = Color(0xFF00FF7F)
    val MaterialBlue = Color(0xFF2979FF)

    var expandedStatusMenu by remember { mutableStateOf(false) }

    val currentStatus = try { SiteStatus.valueOf(site.status) } catch (e: Exception) { SiteStatus.ACTIVE }

    val statusColor = when (currentStatus) {
        SiteStatus.ACTIVE -> NeonGreen
        SiteStatus.COMPLETED -> MaterialBlue
        SiteStatus.HALTED -> AppRed
    }

    Box(modifier = Modifier.clickable { onSelect() }) {
        Box(modifier = Modifier.matchParentSize().offset(8.dp, 8.dp).background(AppYellow))

        Column(modifier = Modifier.fillMaxWidth().background(CardBg).border(3.dp, AppBlack).padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text(
                        text = site.siteName, color = AppBlack, fontWeight = FontWeight.Black, fontSize = 24.sp, fontStyle = FontStyle.Italic,
                        modifier = Modifier.drawBehind { drawLine(AppYellow, Offset(0f, size.height), Offset(size.width, size.height), 4.dp.toPx()) }
                    )
                    Spacer(Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("${if(isKannada) "ಮಾಲೀಕರು" else "OWNER"}: ", color = Color.Gray, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                        Text(site.ownerName, color = AppBlack, fontWeight = FontWeight.Black, fontSize = 10.sp)
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(color = statusColor, border = BorderStroke(1.dp, AppBlack), shape = RoundedCornerShape(2.dp)) {
                        Text(text = currentStatus.name, color = if(currentStatus == SiteStatus.HALTED) AppWhite else AppBlack, fontWeight = FontWeight.Black, fontSize = 9.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp))
                    }
                    Box {
                        Surface(color = AppBlack, modifier = Modifier.size(28.dp).clickable { expandedStatusMenu = true }, shape = RoundedCornerShape(2.dp), border = BorderStroke(1.dp, Color.Gray)) {
                            Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.MoreVert, "Status Menu", tint = Color.Gray, modifier = Modifier.size(16.dp)) }
                        }
                        DropdownMenu(expanded = expandedStatusMenu, onDismissRequest = { expandedStatusMenu = false }, modifier = Modifier.background(AppWhite).border(1.dp, AppBlack)) {
                            DropdownMenuItem(text = { Text("ACTIVE", fontWeight = FontWeight.Black, color = Color(0xFF00C853)) }, onClick = { onUpdateStatus(SiteStatus.ACTIVE); expandedStatusMenu = false })
                            DropdownMenuItem(text = { Text("COMPLETED", fontWeight = FontWeight.Black, color = MaterialBlue) }, onClick = { onUpdateStatus(SiteStatus.COMPLETED); expandedStatusMenu = false })
                            DropdownMenuItem(text = { Text("HALTED", fontWeight = FontWeight.Black, color = AppRed) }, onClick = { onUpdateStatus(SiteStatus.HALTED); expandedStatusMenu = false })
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
            HorizontalDivider(color = Color.DarkGray, thickness = 1.dp)
            Spacer(Modifier.height(20.dp))

            Column(modifier = Modifier.fillMaxWidth().background(InfoBoxBg).border(2.dp, Color.DarkGray).padding(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    val displayLocation = listOf(site.village, site.taluk, site.district).filter { it.isNotBlank() }.joinToString(", ")
                    Text(text = if (displayLocation.isNotEmpty()) displayLocation.uppercase() else "LOCATION NOT SET", color = if(displayLocation.isEmpty()) Color.LightGray else Color.Gray, fontWeight = FontWeight.Black, fontSize = 12.sp)
                }
            }

            Spacer(Modifier.height(20.dp))

            Column(modifier = Modifier.fillMaxWidth().background(InfoBoxBg).border(2.dp, Color.DarkGray).padding(12.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(text = site.gps.uppercase(), color = Color.Gray, fontStyle = FontStyle.Italic, fontWeight = FontWeight.Bold, fontSize = 10.sp, modifier = Modifier.drawBehind { drawLine(Color.LightGray, Offset(0f, size.height), Offset(size.width, size.height), 2.dp.toPx()) })
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Surface(color = AppBlack, border = BorderStroke(2.dp, AppBlack), shape = RoundedCornerShape(2.dp), modifier = Modifier.clickable { onMapsClick() }) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) { Text(if(isKannada) "ಮ್ಯಾಪ್‌ನಲ್ಲಿ ತೆರೆಯಿರಿ" else "OPEN IN MAPS", color = AppWhite, fontWeight = FontWeight.Black, fontSize = 10.sp) }
                        }
                        Surface(color = AppRed, border = BorderStroke(2.dp, AppBlack), shape = RoundedCornerShape(2.dp), modifier = Modifier.clickable { onDelete() }) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) { Text(if(isKannada) "ಅಳಿಸಿ" else "DELETE", color = AppWhite, fontWeight = FontWeight.Black, fontSize = 10.sp) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddSiteDialog(isKannada: Boolean, onDismiss: () -> Unit, onSave: (SiteProject) -> Unit) {
    var siteName by remember { mutableStateOf("") }
    var ownerName by remember { mutableStateOf("") }
    var village by remember { mutableStateOf("") }
    var taluk by remember { mutableStateOf("") }
    var district by remember { mutableStateOf("") }
    var budget by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Box(modifier = Modifier.fillMaxWidth(0.95f).fillMaxHeight(0.85f).padding(10.dp)) {
            Box(modifier = Modifier.matchParentSize().offset(8.dp, 8.dp).background(AppYellow))
            Column(modifier = Modifier.fillMaxSize().background(AppWhite).border(3.dp, AppBlack).padding(20.dp)) {
                Text(text = if (isKannada) "ಹೊಸ ಸೈಟ್ ವಿವರಗಳು" else "NEW SITE DETAILS", fontWeight = FontWeight.Black, fontSize = 22.sp, fontStyle = FontStyle.Italic, color = AppBlack, modifier = Modifier.drawBehind { drawLine(AppYellow, Offset(0f, size.height), Offset(size.width, size.height), 6.dp.toPx()) })
                Spacer(Modifier.height(16.dp))
                Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())) {
                    FormInputSite(if(isKannada) "ಸೈಟ್ ಹೆಸರು" else "SITE NAME", "e.g. Villa 404", siteName, KeyboardType.Text) { siteName = it }
                    FormInputSite(if(isKannada) "ಮಾಲೀಕರ ಹೆಸರು" else "OWNER NAME", "e.g. Ramesh Kumar", ownerName, KeyboardType.Text) { ownerName = it }
                    FormInputSite(if(isKannada) "ಗ್ರಾಮ/ಬಡಾವಣೆ" else "VILLAGE/LOCALITY", "e.g. Whitefield", village, KeyboardType.Text) { village = it }
                    FormInputSite(if(isKannada) "ತಾಲ್ಲೂಕು" else "TALUK", "e.g. KR Puram", taluk, KeyboardType.Text) { taluk = it }
                    FormInputSite(if(isKannada) "ಜಿಲ್ಲೆ" else "DISTRICT", "e.g. Bengaluru Urban", district, KeyboardType.Text) { district = it }
                    FormInputSite(if(isKannada) "ಅಂದಾಜು ಬಜೆಟ್ (₹)" else "EST. BUDGET (₹)", "e.g. 1500000", budget, KeyboardType.Number) { budget = it }
                }
                Spacer(Modifier.height(16.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f).height(48.dp), border = BorderStroke(2.dp, AppBlack), shape = RoundedCornerShape(0.dp)) { Text(if(isKannada) "ರದ್ದುಮಾಡಿ" else "CANCEL", color = AppBlack, fontWeight = FontWeight.Black) }
                    Box(modifier = Modifier.weight(1f).height(48.dp)) {
                        Box(modifier = Modifier.matchParentSize().offset(4.dp, 4.dp).background(Color(0xFF00FF7F)))
                        Button(
                            onClick = {
                                if (siteName.isNotBlank() && budget.isNotBlank()) {
                                    val newSite = SiteProject(siteName = siteName.uppercase(), ownerName = ownerName.uppercase(), village = village, taluk = taluk, district = district, budget = budget, status = SiteStatus.ACTIVE.name)
                                    onSave(newSite)
                                }
                            }, modifier = Modifier.fillMaxSize(), colors = ButtonDefaults.buttonColors(containerColor = AppBlack), shape = RoundedCornerShape(0.dp)
                        ) { Text(if(isKannada) "ಉಳಿಸಿ" else "SAVE", color = AppWhite, fontWeight = FontWeight.Black) }
                    }
                }
            }
        }
    }
}

@Composable
fun FormInputSite(label: String, placeholder: String, value: String, keyboardType: KeyboardType, onValueChange: (String) -> Unit) {
    Column(Modifier.padding(vertical = 8.dp)) {
        Text(text = label, fontSize = 9.sp, fontWeight = FontWeight.Black, color = Color.DarkGray)
        Spacer(Modifier.height(4.dp))
        BasicTextField(
            value = value, onValueChange = onValueChange, keyboardOptions = KeyboardOptions(keyboardType = keyboardType), textStyle = TextStyle(color = AppBlack, fontWeight = FontWeight.Bold, fontSize = 15.sp), modifier = Modifier.fillMaxWidth().background(Color.White).border(2.dp, AppBlack).padding(12.dp),
            decorationBox = { innerTextField -> if (value.isEmpty()) Text(placeholder, color = Color.LightGray, fontSize = 14.sp, fontWeight = FontWeight.Black); innerTextField() }
        )
    }
}

@Composable
fun HeaderSectionSites(isKannada: Boolean, onToggleLanguage: () -> Unit, onLogoutClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().background(AppYellow).padding(16.dp).statusBarsPadding(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(45.dp).background(AppBlack), contentAlignment = Alignment.Center) { Text("NM", color = AppYellow, fontWeight = FontWeight.Black, fontSize = 18.sp) }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(if (isKannada) "ಪ್ಲಾಟ್" else "PLOT", fontWeight = FontWeight.Black, fontSize = 20.sp, color = AppBlack)
                Text(if (isKannada) "ನಿರ್ಮಾಣ ಸಹಾಯಕ" else "CONSTRUCTION ASSISTANT", fontSize = 10.sp, color = AppBlack, fontWeight = FontWeight.Bold, modifier = Modifier.drawBehind { drawLine(AppBlack, Offset(0f, size.height), Offset(size.width, size.height), 2.dp.toPx()) })
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(color = AppBlack, shape = RoundedCornerShape(2.dp), modifier = Modifier.height(32.dp).clickable { onToggleLanguage() }, border = BorderStroke(1.dp, AppYellow)) { Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 12.dp)) { Text(if (isKannada) "ENG" else "ಕನ್ನಡ", color = AppYellow, fontWeight = FontWeight.Bold, fontSize = 12.sp) } }
            Surface(color = AppBlack, shape = RoundedCornerShape(2.dp), modifier = Modifier.size(32.dp).clickable { onLogoutClick() }, border = BorderStroke(1.dp, Color.Gray)) { Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.ExitToApp, null, tint = AppRed, modifier = Modifier.size(18.dp)) } }
        }
    }
}