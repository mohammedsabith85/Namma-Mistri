package com.example.nammamistri.screens

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nammamistri.ui.theme.*

@Composable
fun SignUpScreen(
    onNavigateBack: () -> Unit,
    onSignUpSuccess: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBlack)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // Back Button
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = AppYellow,
                modifier = Modifier.size(32.dp).clickable { onNavigateBack() }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("ಹೊಸ ಖಾತೆ", color = AppYellow, fontWeight = FontWeight.Black, fontSize = 16.sp)
        Text(
            text = "CREATE ACCOUNT",
            color = AppWhite,
            fontWeight = FontWeight.Black,
            fontSize = 32.sp,
            fontStyle = FontStyle.Italic,
            modifier = Modifier.drawBehind { drawLine(AppYellow, Offset(0f, size.height), Offset(size.width, size.height), 6.dp.toPx()) }
        )

        Spacer(modifier = Modifier.height(48.dp))

        // EMAIL INPUT
        AuthInputField("EMAIL", "email@example.com", email, KeyboardType.Email, false) { email = it }
        Spacer(modifier = Modifier.height(16.dp))

        // PASSWORD INPUT
        AuthInputField("PASSWORD", "••••••••", password, KeyboardType.Password, true) { password = it }
        Spacer(modifier = Modifier.height(16.dp))

        // CONFIRM PASSWORD INPUT
        AuthInputField("CONFIRM PASSWORD", "••••••••", confirmPassword, KeyboardType.Password, true) { confirmPassword = it }

        Spacer(modifier = Modifier.height(48.dp))

        // --- LOCAL SIGN UP BUTTON ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clickable(enabled = !isLoading) {
                    if (email.isBlank() || password.isBlank()) {
                        Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                        return@clickable
                    }
                    if (password != confirmPassword) {
                        Toast.makeText(context, "Passwords do not match!", Toast.LENGTH_SHORT).show()
                        return@clickable
                    }

                    // Instantly navigate to the main app
                    Toast.makeText(context, "Account Created Successfully!", Toast.LENGTH_SHORT).show()
                    onSignUpSuccess()
                }
        ) {
            Box(modifier = Modifier.matchParentSize().offset(4.dp, 4.dp).background(Color(0xFF00FF7F)))
            Surface(
                modifier = Modifier.fillMaxSize(), color = AppBlack, border = BorderStroke(3.dp, AppWhite), shape = RoundedCornerShape(0.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("SIGN UP", color = AppWhite, fontWeight = FontWeight.Black, fontSize = 18.sp)
                }
            }
        }
    }
}

// Reusable input field
@Composable
private fun AuthInputField(label: String, placeholder: String, value: String, keyboardType: KeyboardType, isPassword: Boolean, onValueChange: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(label, color = Color.Gray, fontWeight = FontWeight.Black, fontSize = 10.sp)
        Spacer(modifier = Modifier.height(4.dp))
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            textStyle = TextStyle(color = AppWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp),
            modifier = Modifier.fillMaxWidth().border(1.dp, Color.DarkGray).background(Color(0xFF0A0A0A)).padding(16.dp),
            decorationBox = { innerTextField ->
                if (value.isEmpty()) Text(placeholder, color = Color.DarkGray, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                innerTextField()
            }
        )
    }
}