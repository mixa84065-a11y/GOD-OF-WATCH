package com.godofwatch.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Watch
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Night = Color(0xFF101014)
private val Panel = Color(0xFF1B1B22)
private val Gold = Color(0xFFD6AD55)
private val Muted = Color(0xFF9A9AA5)
private val Red = Color(0xFFE87979)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { GodOfWatchApp() }
    }
}

@Composable
fun GodOfWatchApp() {
    var selectedTab by remember { mutableStateOf("Главная") }
    var showPulse by remember { mutableStateOf(false) }
    var showEditor by remember { mutableStateOf(false) }
    MaterialTheme(colorScheme = darkColorScheme(
        primary = Gold, background = Night, surface = Panel,
        onPrimary = Color.Black, onBackground = Color.White, onSurface = Color.White
    )) {
        Scaffold(
            containerColor = Night,
            bottomBar = {
                NavigationBar(containerColor = Panel) {
                    listOf("Главная", "Циферблаты", "Настройки").forEach { item ->
                        NavigationBarItem(
                            selected = selectedTab == item,
                            onClick = { selectedTab = item; showPulse = false; showEditor = false },
                            icon = {
                                Icon(when (item) {
                                    "Главная" -> Icons.Default.Watch
                                    "Циферблаты" -> Icons.Default.Palette
                                    else -> Icons.Default.Settings
                                }, contentDescription = item)
                            },
                            label = { Text(item) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Gold, selectedTextColor = Gold,
                                indicatorColor = Gold.copy(alpha = .14f),
                                unselectedIconColor = Muted, unselectedTextColor = Muted
                            )
                        )
                    }
                }
            }
        ) { padding ->
            Column(
                Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                    .padding(padding).padding(horizontal = 18.dp, vertical = 16.dp)
            ) {
                Text("GOD OF WATCH", color = Gold, fontSize = 24.sp, fontWeight = FontWeight.Black)
                Text("ТВОИ ЧАСЫ. ТВОЙ СТИЛЬ.", color = Muted, fontSize = 10.sp, letterSpacing = 2.sp)
                Spacer(Modifier.height(18.dp))
                when {
                    selectedTab == "Главная" && showPulse -> PulseDetails(onBack = { showPulse = false })
                    selectedTab == "Главная" -> WatchScreen(onPulseClick = { showPulse = true })
                    selectedTab == "Циферблаты" && showEditor -> EditorScreen()
                    selectedTab == "Циферблаты" -> FacesScreen(onOpenEditor = { showEditor = true })
                    else -> WatchSettingsScreen()
                }
            }
        }
    }
}

@Composable
fun WatchScreen(onPulseClick: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // Watch preview: this is a visual preview, not a claim that the watch is connected.
        Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(24.dp)) {
            Column(
                Modifier.fillMaxWidth().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("МОИ ЧАСЫ", color = Gold, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.3.sp)
                        Text("Amazfit GTS 2", fontSize = 19.sp, fontWeight = FontWeight.Bold)
                    }
                    Text("● Не подключены", color = Muted, fontSize = 10.sp)
                }
                Spacer(Modifier.height(14.dp))
                Box(
                    Modifier.width(174.dp).height(220.dp)
                        .background(Color(0xFF08080B), RoundedCornerShape(34.dp))
                        .border(2.dp, Gold.copy(alpha = .75f), RoundedCornerShape(34.dp))
                        .padding(14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                        Text("GOD OF WATCH", color = Gold, fontSize = 9.sp, letterSpacing = 1.4.sp)
                        Spacer(Modifier.height(12.dp))
                        Text("10:08", color = Color.White, fontSize = 37.sp, fontWeight = FontWeight.Light)
                        Text("TUESDAY  •  10", color = Muted, fontSize = 9.sp)
                        Spacer(Modifier.height(18.dp))
                        Text("♧  8 642", color = Gold, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text("♥  -- bpm", color = Color.White, fontSize = 12.sp)
                        Spacer(Modifier.height(10.dp))
                        Text("BATTERY  --%", color = Muted, fontSize = 9.sp)
                    }
                }
                Spacer(Modifier.height(10.dp))
                Text("Предпросмотр текущего дизайна", color = Muted, fontSize = 11.sp)
                Spacer(Modifier.height(8.dp))
                OutlinedButton(
                    onClick = {},
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Gold)
                ) {
                    Icon(Icons.Default.Bluetooth, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Подключить часы")
                }
            }
        }

        Text("ПОКАЗАТЕЛИ ЗДОРОВЬЯ", color = Gold, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                HealthTile("Шаги", "--", "шагов", "👟", Modifier.weight(1f))
                HealthTile("Сон", "--", "за ночь", "🌙", Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                HealthTile("Кислород", "--", "SpO₂", "💧", Modifier.weight(1f))
                HealthTile("Пульс", "--", "уд/мин • график", "♥", Modifier.weight(1f), onClick = onPulseClick)
            }
        }
        Text("ФИНАНСОВЫЕ КОТИРОВКИ", color = Gold, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        CurrencyRatesPanel()
        Text("УПРАВЛЕНИЕ ЧАСАМИ", color = Gold, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ActionTile("Циферблаты", "Выбрать дизайн", "◈", Modifier.weight(1f))
            ActionTile("Настройки", "Функции и часы", "⚙", Modifier.weight(1f))
        }
        Text(
            "Данные здоровья появятся после подключения и проверки доступной синхронизации с Amazfit/Zepp.",
            color = Muted, fontSize = 10.sp
        )
    }
}

@Composable
private fun HealthTile(
    title: String, value: String, unit: String, symbol: String,
    modifier: Modifier = Modifier, onClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier.then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        colors = CardDefaults.cardColors(containerColor = Panel),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(symbol, fontSize = 17.sp)
                Spacer(Modifier.width(7.dp))
                Text(title, color = Muted, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }
            Text(value, color = Color.White, fontSize = 25.sp, fontWeight = FontWeight.Bold)
            Text(unit, color = if (onClick != null) Gold else Muted, fontSize = 10.sp)
            if (onClick != null) Text("Открыть график  →", color = Gold, fontSize = 10.sp)
        }
    }
}

@Composable
private fun ActionTile(title: String, subtitle: String, symbol: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(18.dp)) {
        Column(Modifier.fillMaxWidth().padding(14.dp)) {
            Text(symbol, color = Gold, fontSize = 23.sp)
            Spacer(Modifier.height(5.dp))
            Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text(subtitle, color = Muted, fontSize = 10.sp)
        }
    }
}

@Composable
private fun PulseDetails(onBack: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        TextButton(onClick = onBack, colors = ButtonDefaults.textButtonColors(contentColor = Gold)) {
            Text("← Назад")
        }
        Text("ПУЛЬС", color = Gold, fontSize = 13.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
        Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(22.dp)) {
            Column(Modifier.fillMaxWidth().padding(16.dp)) {
                Text("--", fontSize = 38.sp, fontWeight = FontWeight.Bold)
                Text("уд/мин • нет данных измерений", color = Muted, fontSize = 11.sp)
                Spacer(Modifier.height(16.dp))
                PulseLineChart()
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("00:00", color = Muted, fontSize = 10.sp)
                    Text("06:00", color = Muted, fontSize = 10.sp)
                    Text("12:00", color = Muted, fontSize = 10.sp)
                    Text("18:00", color = Muted, fontSize = 10.sp)
                }
            }
        }
        Text("История измерений будет отображаться здесь после получения реальных данных с часов. Линия на этом экране — только образец оформления, не медицинские данные.", color = Muted, fontSize = 11.sp)
        Text("Периоды: День • Неделя • Месяц", color = Gold, fontSize = 12.sp)
    }
}

@Composable
private fun PulseLineChart() {
    Canvas(Modifier.fillMaxWidth().height(150.dp)) {
        val gridColor = Color.White.copy(alpha = .10f)
        for (i in 0..3) {
            val y = size.height * i / 3f
            drawLine(gridColor, Offset(0f, y), Offset(size.width, y), strokeWidth = 1.dp.toPx())
        }
        val points = listOf(.66f, .60f, .69f, .48f, .53f, .37f, .44f, .29f, .39f, .25f, .34f, .20f)
        val path = Path()
        points.forEachIndexed { index, point ->
            val p = Offset(size.width * index / (points.size - 1), size.height * point)
            if (index == 0) path.moveTo(p.x, p.y) else path.lineTo(p.x, p.y)
        }
        drawPath(path, Gold, style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round))
    }
}

@Composable
private fun WatchSettingsScreen() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("НАСТРОЙКИ ЧАСОВ", color = Gold, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        FeatureCard("Управление функциями", "12 согласованных функций и переключатели")
        FeatureCard("Подключение Amazfit", "Состояние соединения и доступные разрешения")
        FeatureCard("Синхронизация", "Проверка совместимости передачи данных")
        FeatureCard("Оформление приложения", "Чёрный фон и золотые акценты")
        Text("Пункты показаны как разделы интерфейса; управление устройством активируется по мере реализации и проверки совместимости.", color = Muted, fontSize = 11.sp)
    }
}

@Composable
fun FacesScreen(onOpenEditor: () -> Unit) {
    Text("ГАЛЕРЕЯ • AMAZFIT GTS 2", color = Gold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
    Text("Предпросмотры адаптированы под пропорции экрана 348 × 442 px.", color = Muted, fontSize = 11.sp)
    Spacer(Modifier.height(12.dp))
    data class FaceStyle(val title: String, val subtitle: String, val accent: Color, val symbol: String, val time: String)
    val faces = listOf(
        FaceStyle("GOLD ELITE", "Чёрный фон • золотые акценты", Gold, "♛", "10:08"),
        FaceStyle("DARK DIGITAL", "Крупные цифровые часы", Color(0xFFB7C4D8), "◷", "21:45"),
        FaceStyle("MINIMAL", "Чистый минимализм", Color(0xFFE6E6E6), "◌", "09:30"),
        FaceStyle("TRADING LION", "Золотой лев • рыночная тема", Color(0xFFE2B34F), "♌", "22:47"),
        FaceStyle("MARKET NIGHT", "Ночные графики и свечи", Color(0xFF56C7B5), "▥", "08:15"),
        FaceStyle("GOD OF TRADING", "Фирменный чёрно-золотой стиль", Gold, "G", "12:30"),
        FaceStyle("ДЕТЕКТИВ СОВА", "Подозрительная Сова с шишкой", Color(0xFFE7B65C), "🦉", "21:47")
    )
    faces.forEach { face ->
        Card(Modifier.fillMaxWidth().padding(bottom = 10.dp), colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(20.dp)) {
            Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                Column(
                    Modifier.width(124.dp).height(166.dp).background(Color(0xFF08080B), RoundedCornerShape(22.dp)).padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(face.symbol, color = face.accent, fontSize = 23.sp, fontWeight = FontWeight.Bold)
                    Text(face.time, color = face.accent, fontSize = 23.sp, fontWeight = FontWeight.Black)
                    Text("ПТ • 07.11", color = Color.White, fontSize = 8.sp)
                    Text("🔋 --%   👟 --", color = face.accent, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    Text("♥ -- bpm", color = Color.White, fontSize = 9.sp)
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
    OutlinedButton(onClick = onOpenEditor, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.outlinedButtonColors(contentColor = Gold)) {
        Icon(Icons.Default.Palette, contentDescription = null)
        Spacer(Modifier.width(8.dp))
        Text("Открыть конструктор")
    }
    Text("Установка циферблатов на часы требует отдельной проверки совместимого формата и протокола GTS 2.", color = Muted, fontSize = 11.sp)
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
                    Text("🔋 --%   👟 --", color = Gold, fontSize = 12.sp)
                    Text("♥ -- bpm", color = Gold, fontSize = 13.sp)
                    Spacer(Modifier.height(12.dp))
                    Text("BTC/USD  •  USD/RUB", color = Color.White, fontSize = 9.sp)
                }
            }
            Spacer(Modifier.height(14.dp))
            Text("Предпросмотр конструктора. Редактирование элементов будет добавлено следующим этапом.", color = Muted, fontSize = 12.sp)
        }
    }
}

@Composable
fun FeatureCard(title: String, subtitle: String) {
    Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(16.dp)) {
        Column(Modifier.padding(16.dp)) {
            Text(title, fontWeight = FontWeight.SemiBold)
            Text(subtitle, color = Muted, fontSize = 12.sp)
        }
    }
}
