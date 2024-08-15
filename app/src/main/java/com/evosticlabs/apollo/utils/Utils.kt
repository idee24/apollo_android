package com.evosticlabs.apollo.utils

import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.text.format.DateFormat
import android.util.Base64
import android.util.DisplayMetrics
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.io.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.temporal.TemporalField
import java.util.*


fun View.hideKeyboard() {
    val hideAction = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    hideAction.hideSoftInputFromWindow(windowToken, 0)
}



fun encodeImage(bm: Bitmap): String? {
    val byteArrayOutputStream = ByteArrayOutputStream()
    getResizedBitmap(bm, 500)?.compress(Bitmap.CompressFormat.PNG, 70, byteArrayOutputStream)
    val b = byteArrayOutputStream.toByteArray()
    return Base64.encodeToString(b, Base64.DEFAULT)
}

fun getResizedBitmap(image: Bitmap, maxSize: Int): Bitmap? {
    var width = image.width
    var height = image.height
    val bitmapRatio = width.toFloat() / height.toFloat()
    if (bitmapRatio > 1) {
        width = maxSize
        height = (width / bitmapRatio).toInt()
    } else {
        height = maxSize
        width = (height * bitmapRatio).toInt()
    }
    return Bitmap.createScaledBitmap(image, width, height, true)
}

fun getRandomString(length: Int) : String {
    val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
    return (1..length)
        .map { allowedChars.random() }
        .joinToString("")
}



fun getUploadDateString(rawDate: String): String {

    val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return try {
        val calendar = Calendar.getInstance()
        calendar.time = format.parse(rawDate.substringBefore("'T'")) ?: Date()
        "Uploaded at: ${rawDate.substringAfter("T").substringBeforeLast(":")}, on ${DateFormat.format("MMMM", calendar.time)} ${calendar.get(Calendar.DAY_OF_MONTH)} "
    } catch (e: ParseException) {
        e.printStackTrace()
        ""
    }
}

fun getFormattedMonthAndYear(rawDate: String): String {

    val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return try {
        val calendar = Calendar.getInstance()
        calendar.time = format.parse(rawDate.substringBefore("'T'")) ?: Date()
        "${DateFormat.format("MMMM", calendar.time)} ${calendar.get(Calendar.YEAR)}"

    } catch (e: ParseException) {
        e.printStackTrace()
        ""
    }
}

fun getMessagingDate(rawDate: String): String {
    val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return try {
        val calendar = Calendar.getInstance()
        var time = ""
        if (rawDate.contains("T")) {
            calendar.time = format.parse(rawDate.substringBefore("'T'")) ?: Date()
            time = rawDate.substringAfter("T").substringBeforeLast(":")
        }
        else {
            calendar.time = format.parse(rawDate) ?: Date()
        }
        "${DateFormat.format("MMMM", calendar.time)} ${calendar.get(Calendar.DAY_OF_MONTH)}, $time"
    } catch (e: ParseException) {
        e.printStackTrace()
        ""
    }
}

fun getDate(rawDate: String): String {
    val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return try {
        val calendar = Calendar.getInstance()
        if (rawDate.contains("T")) {
            calendar.time = format.parse(rawDate.substringBefore("'T'")) ?: Date()
        }
        else {
            calendar.time = format.parse(rawDate) ?: Date()
        }
        "${DateFormat.format("EEEEEEE", calendar.time)}, ${calendar.get(Calendar.DAY_OF_MONTH)} ${DateFormat.format("MMMM", calendar.time)}"
    } catch (e: ParseException) {
        e.printStackTrace()
        ""
    }
}

fun getDateObject(rawDate: String): Date {
    val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return try {
        format.parse(rawDate) ?: Date()
    } catch (e: ParseException) {
        e.printStackTrace()
        Date()
    }
}

fun getApDisplayDate(date: Date): String {
    val calendar = Calendar.getInstance()
    calendar.time = date

    return "${DateFormat.format("EEEE", calendar.time)}, ${calendar.get(Calendar.DAY_OF_MONTH)} ${DateFormat.format("MMMM", calendar.time)}, ${calendar.get(Calendar.YEAR)}"
}

fun getDateShort(rawDate: String): String {
    val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return try {
        val calendar = Calendar.getInstance()
        if (rawDate.contains("T")) {
            calendar.time = format.parse(rawDate.substringBefore("'T'")) ?: Date()
        }
        else {
            calendar.time = format.parse(rawDate) ?: Date()
        }
        "${DateFormat.format("MMM", calendar.time)} ${calendar.get(Calendar.DAY_OF_MONTH)},"
    } catch (e: ParseException) {
        e.printStackTrace()
        ""
    }
}

fun getDownsizedImageBytes(
    fullBitmap: Bitmap?,
    scaleWidth: Int,
    scaleHeight: Int
): ByteArray? {
    val scaledBitmap = Bitmap.createScaledBitmap(fullBitmap!!, scaleWidth, scaleHeight, true)

    // 2. Instantiate the downsized image content as a byte[]
    val baos = ByteArrayOutputStream()
    scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
    return baos.toByteArray()
}

fun getDefaultMonthList(): List<String> {
    val calender = Calendar.getInstance()
    val calenderYear = calender.get(Calendar.YEAR)
    val calenderMonths = listOf(
        "$calenderYear-01",
        "$calenderYear-02",
        "$calenderYear-03",
        "$calenderYear-04",
        "$calenderYear-05",
        "$calenderYear-06",
        "$calenderYear-07",
        "$calenderYear-08",
        "$calenderYear-09",
        "$calenderYear-10",
        "$calenderYear-11",
        "$calenderYear-12"
    )
    return calenderMonths.subList(calender.get(Calendar.MONTH), calenderMonths.size)
}

fun convertPixelsToDp(px: Float, context: Context): Float {
    return px / (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}

fun ordinal_suffix_of(i: Int): String{
    var j = i % 10
    var k = i % 100
    if (j == 1 && k != 11) {
        return "st"
    }
    if (j == 2 && k != 12) {
        return "nd"
    }
    if (j == 3 && k != 13) {
        return "rd"
    }
    return "th"
}

/**
 * Launches map intent with given coordinates
 */
fun Context.launchMapsIntent(lat: String, long: String) {
    val gmmIntentUri = Uri.parse("google.navigation:q=$lat,$long&mode=d")
    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
    mapIntent.setPackage("com.google.android.apps.maps")
    startActivity(mapIntent)
}




/**
 * Launches link intent with the given [link]
 */
fun Context.launchLinkIntent(link: String) {
    val linkIntent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
    startActivity(linkIntent)
}

/**
 * Launches email app
 */
fun Context.launchEmailApp() {
    val mailIntent = Intent(Intent.ACTION_MAIN)
    mailIntent.addCategory(Intent.CATEGORY_APP_EMAIL)
    startActivity(mailIntent)
}

fun getFileFromBase64(context: Context, imageData: String?): File? {
    val imgBytesData = Base64.decode(
        imageData,
        Base64.DEFAULT
    )
    val file: File = File.createTempFile("image", null, context.cacheDir)
    val fileOutputStream: FileOutputStream
    try {
        fileOutputStream = FileOutputStream(file)
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
        return null
    }
    val bufferedOutputStream = BufferedOutputStream(
        fileOutputStream
    )
    try {
        bufferedOutputStream.write(imgBytesData)
    } catch (e: IOException) {
        e.printStackTrace()
        return null
    } finally {
        try {
            bufferedOutputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    return file
}





