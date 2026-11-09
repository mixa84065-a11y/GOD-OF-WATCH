package com.godofwatch.app

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color as AndroidColor
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.os.Build
import android.provider.MediaStore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Creates a 348x442 background image for the GTS 2 custom-photo watch-face flow.
 * Zepp adds its own clock/widgets on top; these PNGs are not firmware .bin packages.
 */
fun saveWatchFaceBackground(context: Context, faceKey: String, displayName: String): Boolean {
    val bitmap = renderWatchFaceBackground(faceKey)
    val resolver = context.contentResolver
    val filename = "GOD_OF_WATCH_${faceKey.uppercase(Locale.ROOT)}.png"
    val values = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, filename)
        put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/GOD_OF_WATCH")
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }
    }
    val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values) ?: return false
    return try {
        resolver.openOutputStream(uri)?.use { output ->
            if (!bitmap.compress(Bitmap.CompressFormat.PNG, 100, output)) error("PNG export failed")
        } ?: error("Cannot open image output")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val ready = ContentValues().apply { put(MediaStore.Images.Media.IS_PENDING, 0) }
            resolver.update(uri, ready, null, null)
        }
        true
    } catch (_: Exception) {
        resolver.delete(uri, null, null)
        false
    } finally {
        bitmap.recycle()
    }
}

private fun renderWatchFaceBackground(key: String): Bitmap {
    val w = 348
    val h = 442
    val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val p = Paint(Paint.ANTI_ALIAS_FLAG)
    fun color(hex: String) = AndroidColor.parseColor(hex)
    fun line(x1: Float, y1: Float, x2: Float, y2: Float, c: Int, width: Float = 2f) {
        p.color = c; p.style = Paint.Style.STROKE; p.strokeWidth = width
        canvas.drawLine(x1, y1, x2, y2, p); p.style = Paint.Style.FILL
    }
    fun text(s: String, x: Float, y: Float, size: Float, c: Int, bold: Boolean = false) {
        p.color = c; p.textSize = size
        p.typeface = if (bold) android.graphics.Typeface.create("sans-serif", android.graphics.Typeface.BOLD)
                     else android.graphics.Typeface.create("sans-serif-light", android.graphics.Typeface.NORMAL)
        p.textAlign = Paint.Align.CENTER
        canvas.drawText(s, x, y, p)
    }
    val black = color("#08090D")
    val gold = color("#D6AD55")
    val white = color("#F2F2F2")
    val muted = color("#69717D")
    canvas.drawColor(black)
    when (key) {
        "gold_elite" -> {
            p.color = color("#17130C"); canvas.drawCircle(174f, 205f, 145f, p)
            p.color = gold; p.style = Paint.Style.STROKE; p.strokeWidth = 2f
            canvas.drawCircle(174f, 205f, 142f, p); canvas.drawCircle(174f, 205f, 132f, p)
            p.style = Paint.Style.FILL
            for (i in 0 until 60) {
                val a = Math.PI * 2 * i / 60
                val r1 = if (i % 5 == 0) 119f else 125f
                val r2 = 130f
                line((174 + kotlin.math.cos(a)*r1).toFloat(), (205 + kotlin.math.sin(a)*r1).toFloat(),
                    (174 + kotlin.math.cos(a)*r2).toFloat(), (205 + kotlin.math.sin(a)*r2).toFloat(), gold, if (i%5==0) 2.5f else 1f)
            }
            text("G O D   O F   W A T C H", 174f, 54f, 13f, gold, true)
            text("ELITE EDITION", 174f, 390f, 12f, gold, true)
        }
        "dark_digital" -> {
            for (x in 24..324 step 25) line(x.toFloat(), 25f, x.toFloat(), 417f, color("#18202A"), 1f)
            for (y in 25..417 step 25) line(24f, y.toFloat(), 324f, y.toFloat(), color("#18202A"), 1f)
            text("DARK / DIGITAL", 174f, 58f, 15f, color("#AFC4DB"), true)
            text("SYSTEM ONLINE", 174f, 390f, 11f, muted)
            line(50f, 365f, 298f, 365f, color("#3D526A"), 2f)
        }
        "minimal" -> {
            p.color = color("#17191D"); canvas.drawCircle(174f, 220f, 118f, p)
            p.color = white; p.style = Paint.Style.STROKE; p.strokeWidth = 1.5f
            canvas.drawCircle(174f, 220f, 118f, p); p.style = Paint.Style.FILL
            text("M I N I M A L", 174f, 62f, 13f, white)
            text("LESS  /  MORE", 174f, 390f, 11f, muted)
        }
        "trading_lion" -> {
            text("TRADING", 174f, 58f, 14f, gold, true)
            text("LION", 174f, 78f, 12f, white, true)
            p.color = color("#241B0D")
            val mane = Path()
            mane.moveTo(174f, 112f); mane.cubicTo(70f, 115f, 55f, 245f, 174f, 337f)
            mane.cubicTo(293f, 245f, 278f, 115f, 174f, 112f); mane.close()
            canvas.drawPath(mane, p)
            p.color = gold; p.style = Paint.Style.STROKE; p.strokeWidth = 3f
            canvas.drawPath(mane, p); p.style = Paint.Style.FILL
            line(130f, 190f, 155f, 202f, gold, 4f); line(218f, 190f, 193f, 202f, gold, 4f)
            p.color = gold; canvas.drawCircle(143f, 197f, 5f, p); canvas.drawCircle(205f, 197f, 5f, p)
            line(174f, 210f, 174f, 246f, gold, 3f)
            line(174f, 246f, 153f, 261f, gold, 3f); line(174f, 246f, 195f, 261f, gold, 3f)
            text("DISCIPLINE • FOCUS", 174f, 390f, 11f, gold, true)
        }
        "market_night" -> {
            text("MARKET NIGHT", 174f, 58f, 15f, color("#56C7B5"), true)
            for (i in 0 until 8) {
                val x = 56f + i * 34f
                val top = floatArrayOf(185f, 160f, 205f, 135f, 175f, 115f, 145f, 95f)[i]
                val bottom = top + 48f
                val c = if (i % 2 == 0) color("#56C7B5") else color("#D66B75")
                line(x, top-18f, x, bottom+20f, c, 2f)
                p.color = c; canvas.drawRect(x-8f, top, x+8f, bottom, p)
            }
            line(38f, 340f, 310f, 340f, color("#284A4A"), 1f)
            text("READ THE MARKET", 174f, 390f, 11f, muted)
        }
        "god_of_trading" -> {
            text("GOD", 174f, 155f, 62f, gold, true)
            text("OF", 174f, 205f, 24f, white)
            text("TRADING", 174f, 260f, 42f, gold, true)
            line(70f, 285f, 278f, 285f, gold, 2f)
            text("TORQUE • TIMING • CONTROL", 174f, 390f, 10f, muted)
        }
        "detective_owl" -> {
            text("ДЕТЕКТИВ", 174f, 58f, 16f, gold, true)
            // Temporary original owl emblem; replace with the user's reference image for an exact character likeness.
            p.color = color("#4A3525")
            canvas.drawCircle(174f, 218f, 92f, p)
            p.color = color("#D7B783")
            canvas.drawCircle(138f, 202f, 34f, p); canvas.drawCircle(210f, 202f, 34f, p)
            p.color = black
            canvas.drawCircle(145f, 204f, 12f, p); canvas.drawCircle(203f, 204f, 12f, p)
            p.color = gold
            val beak = Path().apply { moveTo(174f, 218f); lineTo(157f, 243f); lineTo(191f, 243f); close() }
            canvas.drawPath(beak, p)
            p.color = color("#B77A36")
            val cone = Path().apply { moveTo(174f, 285f); lineTo(155f, 323f); lineTo(174f, 315f); lineTo(193f, 323f); close() }
            canvas.drawPath(cone, p)
            text("С ШИШКОЙ", 174f, 390f, 12f, gold, true)
        }
    }
    // Keep the central time area intentionally clear: Zepp places its clock on the photo face.
    return bitmap
}
