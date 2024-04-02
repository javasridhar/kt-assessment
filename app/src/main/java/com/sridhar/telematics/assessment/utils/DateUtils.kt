package com.sridhar.telematics.assessment.utils

import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@BindingAdapter("locationDateTime")
fun formatDate(view: View, date: String) {
    var outputDate: String? = null
    try {
        val formatter = SimpleDateFormat("MMM-dd-yyyy hh:mm:ss", Locale.getDefault())
        outputDate = formatter.format(Date(date.toLong()))
        (view as TextView).text = outputDate

    } catch (e: ParseException) {
        e.printStackTrace()
    }
}