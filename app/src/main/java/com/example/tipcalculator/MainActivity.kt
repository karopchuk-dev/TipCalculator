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
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

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

    var tipPercent by remember { mutableFloatStateOf(0f) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val orderSum = orderSumText.replace(',', '.').toDoubleOrNull() ?: 0.0
    val dishCount = dishCountText.toIntOrNull() ?: 0

    val tip = calculateTip(orderSum, tipPercent.roundToInt())
    val discountPct = discountPercent(dishCount)
    val discount = calculateDiscount(orderSum, discountPct)
    val total = calculateTotal(orderSum, tip, discount)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
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

            Spacer(Modifier.height(24.dp))
            Text("Чаевые: ${tipPercent.toInt()}%")
            Slider(
                value = tipPercent,
                onValueChange = { newValue ->
                    if (newValue != tipPercent) {
                        tipPercent = newValue
                        val newTip = calculateTip(orderSum, newValue.roundToInt())
                        scope.launch {
                            snackbarHostState.currentSnackbarData?.dismiss()
                            snackbarHostState.showSnackbar("Сумма чаевых: ${formatMoney(newTip)}")
                        }
                    }
                },
                valueRange = 0f..25f,
                steps = 4
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("0")
                Text("25")
            }
            Spacer(Modifier.height(24.dp))
            DiscountRadioGroup(selectedPercent = discountPct)
            Spacer(Modifier.height(24.dp))
            Text(
                text = "Скидка: ${formatMoney(discount)}",
                style = MaterialTheme.typography.titleMedium
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

@Composable
fun DiscountRadioGroup(selectedPercent: Int) {
    val options = listOf(3, 5, 7, 10)

    Row(verticalAlignment = Alignment.Top) {
        Text("Скидка:", modifier = Modifier.padding(top = 2.dp, end = 8.dp))
        options.forEach { percent ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 10.dp)
            ) {
                RadioButton(
                    selected = percent == selectedPercent,
                    onClick = null
                )
                Text("$percent%")
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun TipScreenPreview() {
    TipCalculatorTheme {
        TipScreen()
    }
}

fun calculateTip(orderSum: Double, tipPercent: Int): Double {
    return orderSum * tipPercent / 100
}

fun discountPercent(dishCount: Int): Int {
    return when {
        dishCount <= 0 -> 0
        dishCount <= 2 -> 3
        dishCount <= 5 -> 5
        dishCount <= 10 -> 7
        else -> 10
    }
}

fun calculateDiscount(orderSum: Double, percent: Int): Double {
    return orderSum * percent / 100
}

fun calculateTotal(orderSum: Double, tip: Double, discount: Double): Double {
    return orderSum + tip - discount
}

fun formatMoney(value: Double): String {
    return "%.2f".format(value)
}