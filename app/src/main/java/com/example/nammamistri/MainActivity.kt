package com.example.nammamistri

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.nammamistri.screens.*
import com.example.nammamistri.ui.theme.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NammaMistriTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = AppBlack) {
                    RootAppFlow()
                }
            }
        }
    }
}

@Composable
fun RootAppFlow() {
    var currentScreen by remember { mutableStateOf("Login") }

    when (currentScreen) {
        "Login" -> {
            LoginScreen(
                onLoginSuccess = { currentScreen = "MainApp" },
                onGoogleLoginClick = { /* Disabled */ },
                onSignUpClick = { currentScreen = "SignUp" }
            )
        }
        "SignUp" -> {
            SignUpScreen(
                onNavigateBack = { currentScreen = "Login" },
                onSignUpSuccess = { currentScreen = "MainApp" }
            )
        }
        "MainApp" -> {
            MainAppScreen(onLogout = { currentScreen = "Login" })
        }
    }
}

@Composable
fun MainAppScreen(onLogout: () -> Unit) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    var selectedSiteId by remember { mutableStateOf<String?>(null) }

    // 🔥 REMOVED the masterSitesList from here because SitesScreen handles its own hard drive memory now!

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = AppBlack) {
                BottomNavItem(navController, currentDestination, "sites", Icons.Default.LocationOn, "SITES")
                BottomNavItem(navController, currentDestination, "calculator", Icons.Default.Calculate, "CALC")
                BottomNavItem(navController, currentDestination, "team", Icons.Default.Groups, "TEAM")
                BottomNavItem(navController, currentDestination, "rates", Icons.Default.TrendingUp, "RATES")
            }
        }
    ) { innerPadding ->
        NavHost(navController = navController, startDestination = "sites", modifier = Modifier.padding(innerPadding)) {
            composable("sites") {
                // 🔥 FIXED: Removed the 'sites =' parameter to match the updated SitesScreen
                SitesScreen(
                    onLogout = onLogout,
                    onSiteSelected = { siteId ->
                        selectedSiteId = siteId
                        navController.navigate("calculator") {
                            popUpTo("sites") { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
            composable("calculator") { if (selectedSiteId == null) LockedScreen("CALCULATOR LOCKED", "PLEASE SELECT A PLOT") else CalculatorScreen() }
            composable("team") { if (selectedSiteId == null) LockedScreen("TEAM LOCKED", "PLEASE SELECT A PLOT") else TeamScreen() }
            composable("rates") { RatesScreen() }
        }
    }
}

@Composable
fun LockedScreen(title: String, subtitle: String) {
    Column(modifier = Modifier.fillMaxSize().background(AppBlack), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(Icons.Default.Lock, contentDescription = "Locked", tint = AppYellow, modifier = Modifier.size(64.dp))
        Spacer(Modifier.height(24.dp))
        Text(title, color = AppYellow, fontSize = 24.sp, fontWeight = FontWeight.Black)
        Spacer(Modifier.height(8.dp))
        Text(subtitle, color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun RowScope.BottomNavItem(navController: NavHostController, destination: NavDestination?, route: String, icon: ImageVector, label: String) {
    NavigationBarItem(
        selected = destination?.hierarchy?.any { it.route == route } == true,
        onClick = {
            navController.navigate(route) {
                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                launchSingleTop = true
                restoreState = true
            }
        },
        icon = { Icon(icon, contentDescription = label) },
        label = { Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold) },
        colors = NavigationBarItemDefaults.colors(selectedIconColor = AppBlack, selectedTextColor = AppYellow, unselectedIconColor = AppWhite, unselectedTextColor = AppWhite, indicatorColor = AppYellow)
    )
}