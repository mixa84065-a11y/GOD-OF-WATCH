package com.godofwatch.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
    var selectedTab by remember { mutableStateOf("Циферблаты") }
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
            Column(Modifier.fillMaxSize().padding(padding).padding(20.dp)) {
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
    Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(24.dp)) {
        Column(Modifier.fillMaxWidth().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.Watch, null, tint = Gold, modifier = Modifier.size(54.dp))
            Spacer(Modifier.height(12.dp))
            Text("Amazfit GTS 2", fontSize = 21.sp, fontWeight = FontWeight.Bold)
            Text("Подключение будет доступно после проверки BLE-протокола", color = Muted, fontSize = 12.sp)
            Spacer(Modifier.height(18.dp))
            OutlinedButton(onClick = {}, colors = ButtonDefaults.outlinedButtonColors(contentColor = Gold)) {
                Icon(Icons.Default.Bluetooth, null)
                Spacer(Modifier.width(8.dp))
                Text("Подключить часы")
            }
            Text("Пока не подключено", color = Muted, fontSize = 12.sp)
        }
    }
    Spacer(Modifier.height(18.dp))
    Text("ВОЗМОЖНОСТИ", color = Gold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
    Spacer(Modifier.height(10.dp))
    FeatureCard("Галерея циферблатов", "Подборка стилей для GTS 2")
    FeatureCard("Мой конструктор", "Создавай собственный дизайн")
    FeatureCard("Передача на часы", "Станет активна после проверки совместимости")
}

@Composable
fun FacesScreen() {
    Text("ГАЛЕРЕЯ", color = Gold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
    Spacer(Modifier.height(12.dp))
    listOf("GOLD ELITE", "DARK DIGITAL", "MINIMAL").forEachIndexed { index, title ->
        Card(Modifier.fillMaxWidth().padding(bottom = 12.dp), colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(20.dp)) {
            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(76.dp, 92.dp).background(Color(0xFF08080B), RoundedCornerShape(18.dp)), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(listOf("10:08", "21:45", "09:30")[index], color = Gold, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                        Text("TUE 10", color = Color.White, fontSize = 9.sp)
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
                    Spacer(Modifier.height(24.dp))
                    Text("♥  82 bpm", color = Gold, fontSize = 13.sp)
                }
            }
            Spacer(Modifier.height(14.dp))
            Text("Редактор будет расширен: фон, цвет, шрифт и виджеты", color = Muted, fontSize = 12.sp)
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
