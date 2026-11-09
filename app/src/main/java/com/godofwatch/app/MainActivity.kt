package com.godofwatch.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Watch
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers

private val Night = Color(0xFF101014)
private val Panel = Color(0xFF1B1B22)
private val Gold = Color(0xFFD6AD55)
private val Muted = Color(0xFF9A9AA5)

private data class Quote(val pair: String, val value: String)

private fun loadQuotes(): List<Quote> {
    fun getJson(address: String): org.json.JSONObject {
        val connection = java.net.URL(address).openConnection() as java.net.HttpURLConnection
        connection.connectTimeout = 10000
        connection.readTimeout = 10000
        connection.setRequestProperty("User-Agent", "GODOFWATCH/1.0")
        return try {
            org.json.JSONObject(connection.inputStream.bufferedReader().use { it.readText() })
        } finally {
            connection.disconnect()
        }
    }

    val rates = getJson("https://open.er-api.com/v6/latest/USD").getJSONObject("rates")
    val rub = rates.getDouble("RUB")
    val eur = rates.getDouble("EUR")
    val gbp = rates.getDouble("GBP")
    val btcJson = getJson("https://api.coingecko.com/api/v3/simple/price?ids=bitcoin&vs_currencies=usd")
    val btc = btcJson.getJSONObject("bitcoin").getDouble("usd")
    return listOf(
        Quote("USD/RUB", "%.2f".format(java.util.Locale.US, rub)),
        Quote("EUR/RUB", "%.2f".format(java.util.Locale.US, rub / eur)),
        Quote("GBP/RUB", "%.2f".format(java.util.Locale.US, rub / gbp)),
        Quote("BTC/USD", "%,.0f".format(java.util.Locale.US, btc)),
        Quote("USD/EUR", "%.4f".format(java.util.Locale.US, eur)),
        Quote("EUR/USD", "%.4f".format(java.util.Locale.US, 1.0 / eur))
    )
}

@Composable
fun CurrencyRatesPanel() {
    var quotes by remember { mutableStateOf<List<Quote>>(emptyList()) }
    var updatedAt by remember { mutableStateOf("Загрузка котировок…") }
    var error by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (true) {
            try {
                val fresh = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) { loadQuotes() }
                quotes = fresh
                updatedAt = "Обновлено: " + java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())
                error = false
            } catch (_: Exception) {
                if (quotes.isEmpty()) updatedAt = "Нет связи с источником котировок"
                error = true
            }
            kotlinx.coroutines.delay(5 * 60 * 1000L)
        }
    }

    Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(20.dp)) {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("КОТИРОВКИ", color = Gold, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Spacer(Modifier.weight(1f))
                Text("каждые 5 мин", color = Muted, fontSize = 10.sp)
            }
            if (quotes.isEmpty()) {
                Text(updatedAt, color = if (error) Color(0xFFFF7777) else Muted, fontSize = 12.sp)
            } else {
                quotes.chunked(2).forEach { row ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        row.forEach { quote ->
                            Column(Modifier.weight(1f).background(Night, RoundedCornerShape(12.dp)).padding(10.dp)) {
                                Text(quote.pair, color = Muted, fontSize = 10.sp)
                                Text(quote.value, color = Gold, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        if (row.size == 1) Spacer(Modifier.weight(1f))
                    }
                }
                Text(if (error) "$updatedAt • ошибка обновления" else updatedAt, color = if (error) Color(0xFFFF7777) else Muted, fontSize = 10.sp)
            }
            Text("Котировки загружаются из открытых интернет-источников. Это справочные значения, не цены брокера.", color = Muted, fontSize = 10.sp)
        }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { GodOfWatchApp() }
    }
}

@Composable
fun GodOfWatchApp() {
    var selectedTab by remember { mutableStateOf("Часы") }
    MaterialTheme(colorScheme = darkColorScheme(primary = Gold, background = Night, surface = Panel,
        onPrimary = Color.Black, onBackground = Color.White, onSurface = Color.White)) {
        Scaffold(containerColor = Night, bottomBar = {
            NavigationBar(containerColor = Panel) {
                listOf("Часы", "Циферблаты", "Конструктор").forEach { item ->
                    NavigationBarItem(selected = selectedTab == item, onClick = { selectedTab = item },
                        icon = { Icon(when (item) {
                            "Часы" -> Icons.Default.Watch
                            "Конструктор" -> Icons.Default.Palette
                            else -> Icons.Default.Bluetooth
                        }, contentDescription = item) },
                        label = { Text(item) },
                        colors = NavigationBarItemDefaults.colors(selectedIconColor = Gold,
                            selectedTextColor = Gold, indicatorColor = Gold.copy(alpha = .14f),
                            unselectedIconColor = Muted, unselectedTextColor = Muted))
                }
            }
        }) { padding ->
            Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(padding).padding(20.dp)) {
                Text("GOD OF WATCH", color = Gold, fontSize = 24.sp, fontWeight = FontWeight.Black)
                Text("ТВОИ ЧАСЫ. ТВОЙ СТИЛЬ.", color = Muted, fontSize = 11.sp, letterSpacing = 2.sp)
                Spacer(Modifier.height(24.dp))
                when (selectedTab) {
                    "Часы" -> WatchScreen()
                    "Конструктор" -> EditorScreen()
                    else -> FacesScreen()
                }
            }
        }
    }
}

@Composable
fun WatchScreen() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(24.dp)) {
            Column(Modifier.fillMaxWidth().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Watch, null, tint = Gold, modifier = Modifier.size(54.dp))
                Spacer(Modifier.height(12.dp))
                Text("Amazfit GTS 2", fontSize = 21.sp, fontWeight = FontWeight.Bold)
                Text("Подключение будет доступно после проверки протокола часов", color = Muted, fontSize = 12.sp)
                Spacer(Modifier.height(18.dp))
                OutlinedButton(onClick = {}, colors = ButtonDefaults.outlinedButtonColors(contentColor = Gold)) {
                    Icon(Icons.Default.Bluetooth, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Подключить часы")
                }
                Text("Пока не подключено", color = Muted, fontSize = 12.sp)
            }
        }
        CurrencyRatesPanel()
        Text("ВОЗМОЖНОСТИ", color = Gold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        FeatureCard("Галерея циферблатов", "Подборка стилей для GTS 2")
        FeatureCard("Мой конструктор", "Создавай собственный дизайн")
        FeatureCard("Передача на часы", "Станет активна после проверки совместимости")
    }
}

@Composable
fun FacesScreen() {
    Text("ГАЛЕРЕЯ", color = Gold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
    Spacer(Modifier.height(12.dp))
    listOf("GOLD ELITE", "DARK DIGITAL", "MINIMAL", "TRADING LION", "MARKET NIGHT", "GOD OF TRADING").forEachIndexed { index, title ->
        Card(Modifier.fillMaxWidth().padding(bottom = 12.dp), colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(20.dp)) {
            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(76.dp, 92.dp).background(Color(0xFF08080B), RoundedCornerShape(18.dp)), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(listOf("10:08", "21:45", "09:30", "22:47", "08:15", "12:30")[index], color = Gold, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                        Text("78%  •  8.6K", color = Color.White, fontSize = 8.sp)
                        Text("♥ 72", color = Gold, fontSize = 9.sp)
                    }
                }
                Spacer(Modifier.width(16.dp))
                Column(Modifier.weight(1f)) {
                    Text(title, color = Color.White, fontWeight = FontWeight.Bold)
                    Text("Концепт циферблата", color = Muted, fontSize = 12.sp)
                    Text("Предпросмотр", color = Gold, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun EditorScreen() {
    Text("КОНСТРУКТОР", color = Gold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
    Spacer(Modifier.height(12.dp))
    Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(24.dp)) {
        Column(Modifier.fillMaxWidth().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(Modifier.size(190.dp, 230.dp).background(Color.Black, RoundedCornerShape(32.dp)), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("10:08", color = Gold, fontSize = 38.sp, fontWeight = FontWeight.Light)
                    Text("TUESDAY 10", color = Color.White, fontSize = 11.sp)
                    Spacer(Modifier.height(18.dp))
                    Text("🔋 78%   👟 8.6K", color = Gold, fontSize = 12.sp)
                    Text("♥ 72 bpm", color = Gold, fontSize = 13.sp)
                    Spacer(Modifier.height(12.dp))
                    Text("BTC/USD  •  USD/RUB", color = Color.White, fontSize = 9.sp)
                }
            }
            Spacer(Modifier.height(14.dp))
            Text("Настройка фона, цвета, шрифта, показателей и валютных пар", color = Muted, fontSize = 12.sp)
        }
    }
}

@Composable
fun FeatureCard(title: String, subtitle: String) {
    Card(Modifier.fillMaxWidth().padding(bottom = 10.dp), colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(16.dp)) {
        Column(Modifier.padding(16.dp)) {
            Text(title, fontWeight = FontWeight.SemiBold)
            Text(subtitle, color = Muted, fontSize = 12.sp)
        }
    }
}
