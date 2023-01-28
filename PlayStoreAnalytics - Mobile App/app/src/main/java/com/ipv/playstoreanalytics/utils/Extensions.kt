package com.ipv.playstoreanalytics.utils;

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import java.io.Serializable

/**
 * Extension method to provide quicker access to the [LayoutInflater] from [Context].
 */
fun Context.getLayoutInflater() =
        getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

/**
 * Extension method to provide quicker access to the [LayoutInflater] from a [View].
 */
fun View.getLayoutInflater() = context.getLayoutInflater()


/**
 * Extension method to provide simpler access to {@link ContextCompat#getColor(int)}.
 */
fun Context.getColorCompat(color: Int) = ContextCompat.getColor(this, color)


/**
 * Extension method to get serializable
 */

fun <T : Serializable?> Bundle.getSerializableCompat(key: String, clazz: Class<T>): T {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) getSerializable(key, clazz)!! else (getSerializable(key) as T)
}

fun isOnline(context: Context): Boolean {
                val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

                return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                        manager.getNetworkCapabilities(manager.activeNetwork)?.let {
                                it.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                                        it.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                                        it.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) ||
                                        it.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) ||
                                        it.hasTransport(NetworkCapabilities.TRANSPORT_VPN)
                        } ?: false
                else
                        @Suppress("DEPRECATION")
                        manager.activeNetworkInfo?.isConnectedOrConnecting == true


}

/**
 * extension to convert Int value in PX density
 */
val Int.toPX: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()


fun View.closeKeyboardForce() {
    (context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.also {
        it.hideSoftInputFromWindow(applicationWindowToken, 0)
    }
}


@BindingAdapter("android:appImage")
fun setAppImage(view: ImageView, imageUrl: String?) {
    Glide.with(view)
        .load(imageUrl)
        .into(view)
}

