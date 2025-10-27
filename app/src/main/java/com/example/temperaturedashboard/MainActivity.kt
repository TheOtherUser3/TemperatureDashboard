package com.example.temperaturedashboard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.temperaturedashboard.ui.theme.TemperatureDashboardTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt
import kotlin.random.Random

// ------------------ DATA MODEL ------------------
data class TemperatureReading(val timestamp: String, val value: Float)

// ------------------ VIEWMODEL ------------------
class TemperatureViewModel : ViewModel() {
    private val _readings = MutableStateFlow<List<TemperatureReading>>(emptyList())
    val readings: StateFlow<List<TemperatureReading>> = _readings.asStateFlow()

    private val _isRunning = MutableStateFlow(true)
    val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()

    init {
        startSimulation()
    }

    private fun startSimulation() {
        viewModelScope.launch {
            while (true) {
                if (_isRunning.value) {
                    val value = Random.nextDouble(65.0, 85.0).toFloat()
                    val timestamp = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
                    val newReading = TemperatureReading(timestamp, value)
                    _readings.value = (listOf(newReading) + _readings.value).take(20)
                }
                delay(2000L)
            }
        }
    }

    fun toggleRunning() {
        _isRunning.value = !_isRunning.value
    }

    // --- Summary stats ---
    val current get() = _readings.value.firstOrNull()?.value
    val average get() = if (_readings.value.isNotEmpty()) _readings.value.map { it.value }.average().toFloat() else null
    val min get() = _readings.value.minByOrNull { it.value }?.value
    val max get() = _readings.value.maxByOrNull { it.value }?.value
}

// ------------------ ACTIVITY ------------------
class MainActivity : ComponentActivity() {
    private val viewModel: TemperatureViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TemperatureDashboardTheme {
                TemperatureDashboardApp(viewModel)
            }
        }
    }
}

// ------------------ APP ------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemperatureDashboardApp(viewModel: TemperatureViewModel) {
    val readings by viewModel.readings.collectAsState()
    val isRunning by viewModel.isRunning.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Temperature Dashboard") }
            )
        }
    ) { innerPadding ->
        DashboardScreen(
            modifier = Modifier.padding(innerPadding),
            readings = readings,
            isRunning = isRunning,
            current = viewModel.current,
            average = viewModel.average,
            min = viewModel.min,
            max = viewModel.max,
            onToggle = { viewModel.toggleRunning() }
        )
    }
}

// ------------------ MAIN SCREEN ------------------
@Composable
fun DashboardScreen(
    modifier: Modifier = Modifier,
    readings: List<TemperatureReading>,
    isRunning: Boolean,
    current: Float?,
    average: Float?,
    min: Float?,
    max: Float?,
    onToggle: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // ---- Stats ----
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Current: ${fmt(current)} °F", fontWeight = FontWeight.Bold)
                Text("Average: ${fmt(average)} °F")
                Text("Min: ${fmt(min)} °F")
                Text("Max: ${fmt(max)} °F")
                Text("Status: ${if (isRunning) "Streaming" else "Paused"}",
                    color = if (isRunning) Color(0xFF4CAF50) else Color(0xFFF44336))
            }
        }

        // ---- Chart ----
        if (readings.isNotEmpty()) {
            TemperatureChart(readings)
        }

        // ---- Buttons ----
        Button(
            onClick = onToggle,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isRunning)
                    MaterialTheme.colorScheme.error
                else
                    MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(if (isRunning) "Pause" else "Resume")
        }

        // ---- List ----
        Text("Recent Readings:", style = MaterialTheme.typography.titleMedium)
        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(readings) { reading ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0x11000000))
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(reading.timestamp)
                    Text("${reading.value.roundToInt()}°F")
                }
            }
        }
    }
}

// ------------------ CHART ------------------
@Composable
fun TemperatureChart(readings: List<TemperatureReading>) {
    val values = readings.map { it.value }.reversed()
    val maxVal = values.maxOrNull() ?: 0f
    val minVal = values.minOrNull() ?: 0f
    val range = (maxVal - minVal).coerceAtLeast(1f)

    Card(Modifier.fillMaxWidth().height(150.dp)) {
        Canvas(modifier = Modifier.fillMaxSize().padding(8.dp)) {
            val stepX = size.width / (values.size.coerceAtLeast(2) - 1)
            for (i in 1 until values.size) {
                val y1 = size.height - (values[i - 1] - minVal) / range * size.height
                val y2 = size.height - (values[i] - minVal) / range * size.height
                drawLine(
                    color = Color(0xFF2196F3),
                    start = androidx.compose.ui.geometry.Offset((i - 1) * stepX, y1),
                    end = androidx.compose.ui.geometry.Offset(i * stepX, y2),
                    strokeWidth = 4f,
                    cap = StrokeCap.Round
                )
            }
        }
    }
}

// ------------------ HELPER ------------------
fun fmt(value: Float?): String = value?.let { String.format("%.1f", it) } ?: "--"

