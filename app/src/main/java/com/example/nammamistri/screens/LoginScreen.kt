package com.example.nammamistri.screens

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
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
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onGoogleLoginClick: () -> Unit,
    onSignUpClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize().background(AppBlack).padding(24.dp).verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(64.dp))
        Box(modifier = Modifier.size(80.dp).background(AppYellow), contentAlignment = Alignment.Center) {
            Text("NM", color = AppBlack, fontWeight = FontWeight.Black, fontSize = 32.sp)
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text("ನಮ್ಮ ಮಿಸ್ತ್ರಿ", color = AppYellow, fontWeight = FontWeight.Black, fontSize = 18.sp)
        Text(text = "NAMMA MISTRI", color = AppWhite, fontWeight = FontWeight.Black, fontSize = 38.sp, fontStyle = FontStyle.Italic, modifier = Modifier.drawBehind { drawLine(AppYellow, Offset(0f, size.height), Offset(size.width, size.height), 8.dp.toPx()) })

        Spacer(modifier = Modifier.height(64.dp))

        AuthInputField("EMAIL", "mistri@example.com", email, KeyboardType.Email, false) { email = it }
        Spacer(modifier = Modifier.height(24.dp))
        AuthInputField("PASSWORD", "••••••••", password, KeyboardType.Password, true) { password = it }

        Spacer(modifier = Modifier.height(48.dp))

        // DUMMY LOGIN
        Button(
            onClick = {
                if (email.isNotBlank() && password.isNotBlank()) {
                    onLoginSuccess() // Navigate instantly
                } else {
                    Toast.makeText(context, "Please enter details to proceed", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp), colors = ButtonDefaults.buttonColors(containerColor = AppYellow), shape = RoundedCornerShape(0.dp)
        ) {
            Text("LOGIN", color = AppBlack, fontWeight = FontWeight.Black, fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = { Toast.makeText(context, "Google Sign-In is disabled.", Toast.LENGTH_SHORT).show() },
            modifier = Modifier.fillMaxWidth().height(56.dp), border = BorderStroke(2.dp, AppWhite), shape = RoundedCornerShape(0.dp)
        ) {
            Text("CONTINUE WITH GOOGLE", color = AppWhite, fontWeight = FontWeight.Black)
        }

        Spacer(modifier = Modifier.height(32.dp))
        Text(text = "DON'T HAVE AN ACCOUNT? SIGN UP", color = AppYellow, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.clickable { onSignUpClick() })
    }
}

@Composable
private fun AuthInputField(label: String, placeholder: String, value: String, keyboardType: KeyboardType, isPassword: Boolean, onValueChange: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(label, color = Color.Gray, fontWeight = FontWeight.Black, fontSize = 10.sp)
        Spacer(modifier = Modifier.height(4.dp))
        BasicTextField(
            value = value, onValueChange = onValueChange, keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
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