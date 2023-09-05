package com.example.bluetoothchatapp.utils

import android.app.Activity
import android.content.Context
import android.os.Build
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.navigation.NavController

fun isAndroidVersion12OrHigher(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
}

fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}

private fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(InputMethodManager::class.java)
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

val NavController.currentRoute get() = currentDestination?.route