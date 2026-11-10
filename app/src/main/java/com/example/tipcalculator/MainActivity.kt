package com.example.tipcalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.tipcalculator.ui.theme.TipCalculatorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TipCalculatorTheme {
                TipScreen()
            }
        }
    }
}

@Composable
fun TipScreen() {
    var orderSumText by remember { mutableStateOf("") }
    var dishCountText by remember { mutableStateOf("") }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            InputRow(
                label = "Сумма заказа:",
                value = orderSumText,
                onValueChange = { orderSumText = it },
                fieldWidth = 120.dp
            )
            Spacer(Modifier.height(12.dp))
            InputRow(
                label = "Количество блюд:",
                value = dishCountText,
                onValueChange = { dishCountText = it },
                fieldWidth = 60.dp
            )
        }
    }
}

@Composable
fun InputRow(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    fieldWidth: Dp
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(label)
        Spacer(Modifier.width(8.dp))
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .width(fieldWidth)
                .background(Color(0xFFFFB6C8))
                .padding(6.dp)
        )
    }
}

@Preview(showSystemUi = true)
@Composable
fun TipScreenPreview() {
    TipCalculatorTheme {
        TipScreen()
    }
}