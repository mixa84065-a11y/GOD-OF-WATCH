package com.godofwatch.app

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private data class Quote(val pair: String, val value: Double, val updatedAt: String, val source: String)
private data class QuoteSnapshot(val quotes: List<Quote>, val fetchedAt: Long)

private fun requestJson(url: String): JSONObject {
    val connection = (URL(url).openConnection() as HttpURLConnection)
    connection.connectTimeout = 12000
    connection.readTimeout = 12000
    connection.requestMethod = "GET"
    connection.setRequestProperty("Accept", "application/json")
    return try {
        val code = connection.responseCode
        val stream = if (code in 200..299) connection.inputStream else connection.errorStream
        val body = stream?.bufferedReader()?.use { it.readText() }.orEmpty()
        if (code !in 200..299) error("HTTP $code: $body")
        JSONObject(body).also { json ->
            if (json.optString("result") == "error") error(json.optString("message", "Ошибка источника"))
        }
    } finally {
        connection.disconnect()
    }
}

private fun fetchQuotes(): QuoteSnapshot {
    // One USD basket avoids a separate GBP-base request that can be rejected by some providers.
    val fx = requestJson("https://api.exchangerate.dev/v1/latest/USD?symbols=RUB,EUR,GBP")
    val rates = fx.optJSONObject("rates") ?: error("Источник не вернул rates")
    fun rate(symbol: String): Double =
        rates.optDouble(symbol).takeIf { it > 0.0 } ?: error("Нет курса $symbol")

    val usdRub = rate("RUB")
    val usdEur = rate("EUR")
    val usdGbp = rate("GBP")
    val fxTime = fx.optString("data_updated_at", fx.optString("timestamp", "время источника неизвестно"))
    val quotes = mutableListOf(
        Quote("USD/RUB", usdRub, fxTime, "ExchangeRate.dev"),
        Quote("EUR/RUB", usdRub / usdEur, fxTime, "Расчёт через USD"),
        Quote("GBP/RUB", usdRub / usdGbp, fxTime, "Расчёт через USD"),
        Quote("USD/EUR", usdEur, fxTime, "ExchangeRate.dev"),
        Quote("EUR/USD", 1.0 / usdEur, fxTime, "Расчётный обратный курс")
    )
    // Crypto is independent: if CoinGecko is unavailable, try Coinbase.
    val btc = runCatching {
        val crypto = requestJson("https://api.coingecko.com/api/v3/simple/price?ids=bitcoin&vs_currencies=usd")
        crypto.optJSONObject("bitcoin")?.optDouble("usd")?.takeIf { it > 0.0 }
            ?: error("CoinGecko не вернул BTC/USD")
    }.recoverCatching {
        val coinbase = requestJson("https://api.coinbase.com/v2/exchange-rates?currency=USD")
        val btcPerUsd = coinbase.optJSONObject("data")?.optJSONObject("rates")?.optDouble("BTC")
            ?.takeIf { it > 0.0 } ?: error("Coinbase не вернул BTC/USD")
        1.0 / btcPerUsd
    }.getOrNull()
    if (btc != null) quotes.add(Quote("BTC/USD", btc, "обновлено сейчас", "CoinGecko / Coinbase"))
    return QuoteSnapshot(quotes, System.currentTimeMillis())
}

private fun formatRate(value: Double): String =
    DecimalFormat("#,##0.####").format(value)

@Composable
fun CurrencyRatesPanel() {
    var snapshot by remember { mutableStateOf<QuoteSnapshot?>(null) }
    var loading by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf<String?>(null) }
    var lastAttempt by remember { mutableStateOf<Long?>(null) }

    suspend fun refresh() {
        if (loading) return
        loading = true
        lastAttempt = System.currentTimeMillis()
        try {
            val fresh = withContext(Dispatchers.IO) { fetchQuotes() }
            snapshot = fresh
            errorText = null
        } catch (e: Exception) {
            errorText = e.message ?: "Не удалось получить котировки"
        } finally {
            loading = false
        }
    }

    LaunchedEffect(Unit) {
        while (isActive) {
            refresh()
            delay(5 * 60 * 1000L)
        }
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B1B22)),
        shape = RoundedCornerShape(22.dp)
    ) {
        Column(Modifier.fillMaxWidth().padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("РЫНОК • КОТИРОВКИ", color = Color(0xFFD6AD55), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text("Проверка каждые 5 минут • FX-данные могут отставать до 15 минут", color = Color(0xFF9A9AA5), fontSize = 11.sp)
                }
            }
            Spacer(Modifier.height(10.dp))
            if (snapshot == null && loading) {
                LinearProgressIndicator(Modifier.fillMaxWidth(), color = Color(0xFFD6AD55))
                Text("Загружаем котировки…", color = Color(0xFF9A9AA5), fontSize = 12.sp)
            }
            snapshot?.quotes?.forEach { quote ->
                Row(Modifier.fillMaxWidth().padding(vertical = 7.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(quote.pair, modifier = Modifier.weight(1f), color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Text(formatRate(quote.value), color = Color(0xFFD6AD55), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
                HorizontalDivider(color = Color.White.copy(alpha = .07f))
            }
            if (errorText != null) {
                Text("Ошибка обновления: $errorText", color = Color(0xFFFF7777), fontSize = 11.sp)
                Text("Показываем последние успешно полученные значения, если они есть.", color = Color(0xFF9A9AA5), fontSize = 10.sp)
            }
            snapshot?.let {
                Text("Получено приложением: " + SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(it.fetchedAt)),
                    color = Color(0xFF9A9AA5), fontSize = 10.sp)
                Text("FX: справочные/индикативные курсы, бесплатный поток с задержкой до 15 минут. Источник: ExchangeRate.dev. Не для сделок.",
                    color = Color(0xFF9A9AA5), fontSize = 10.sp)
            }
            if (snapshot == null && errorText != null) {
                Text("Нет данных. Проверь интернет и повтори попытку.", color = Color(0xFFFF7777), fontSize = 11.sp)
            }
        }
    }
}
