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

private val Night = Color(0xFF101014)
private val Panel = Color(0xFF1B1B22)
private val Gold = Color(0xFFD6AD55)
private val Muted = Color(0xFF9A9AA5)

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
    Text("ГАЛЕРЕЯ • ФОРМАТ AMAZFIT GTS 2", color = Gold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
    Text("Макетная область каждого циферблата учитывает пропорции экрана 348 × 442 px.", color = Muted, fontSize = 11.sp)
    Spacer(Modifier.height(12.dp))
    data class FaceStyle(val title: String, val subtitle: String, val accent: Color, val symbol: String, val time: String)
    val faces = listOf(
        FaceStyle("GOLD ELITE", "Чёрный фон • золотые акценты", Color(0xFFD6AD55), "♛", "10:08"),
        FaceStyle("DARK DIGITAL", "Крупные цифровые часы", Color(0xFFB7C4D8), "◷", "21:45"),
        FaceStyle("MINIMAL", "Чистый минимализм", Color(0xFFE6E6E6), "◌", "09:30"),
        FaceStyle("TRADING LION", "Золотой лев • рыночная тема", Color(0xFFE2B34F), "♌", "22:47"),
        FaceStyle("MARKET NIGHT", "Ночные графики и свечи", Color(0xFF56C7B5), "▥", "08:15"),
        FaceStyle("GOD OF TRADING", "Фирменный чёрно-золотой стиль", Gold, "G", "12:30"),
        FaceStyle("ДЕТЕКТИВ СОВА", "Подозрительная Сова с шишкой", Color(0xFFE7B65C), "🦉", "21:47")
    )
    faces.forEach { face ->
        Card(Modifier.fillMaxWidth().padding(bottom = 12.dp), colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(20.dp)) {
            Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                Column(
                    Modifier.width(142.dp).height(180.dp).background(Color(0xFF08080B), RoundedCornerShape(22.dp)).padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(face.symbol, color = face.accent, fontSize = 25.sp, fontWeight = FontWeight.Bold)
                    Text(face.time, color = face.accent, fontSize = 25.sp, fontWeight = FontWeight.Black)
                    Text("ПТ • 07.11", color = Color.White, fontSize = 8.sp)
                    Text("🔋 78%   👟 8.6K", color = face.accent, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    Text("♥ 72 bpm", color = Color.White, fontSize = 9.sp)
                }
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(face.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(face.subtitle, color = Muted, fontSize = 11.sp)
                    Text("348 × 442 px • GTS 2", color = face.accent, fontSize = 10.sp)
                    Text("Предпросмотр макета", color = Muted, fontSize = 10.sp)
                }
            }
        }
    }
    Text("Это адаптированные по пропорциям экранные макеты. Установка на часы появится после проверки и реализации совместимого формата GTS 2.", color = Muted, fontSize = 11.sp)
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
