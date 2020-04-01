package com.shovon.coronalive

import android.app.ProgressDialog
import android.content.Context
import android.net.ConnectivityManager
import android.preference.PreferenceManager
import android.widget.Toast


fun toast(context: Context, msg:String){
    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
}


fun isOnline(context: Context): Boolean {
    val cm =
        context.getSystemService(Context.CONNECTIVITY_SERVICE)
            as ConnectivityManager
    val networkInfo = cm.activeNetworkInfo
    return networkInfo != null && networkInfo.isConnected
}

fun packageTrue (context: Context): Boolean {
    val packageName = context.packageName
    return packageName == "com.shovon.onetouch"
}


fun isVPN(context: Context): Boolean {
    val cm =
        context.getSystemService(Context.CONNECTIVITY_SERVICE)
                as ConnectivityManager
    return cm.getNetworkInfo(ConnectivityManager.TYPE_VPN).isConnectedOrConnecting
}


fun saveData(
    key: String?,
    value: String?,
    context: Context?
) {
    val preferences = PreferenceManager.getDefaultSharedPreferences(context)
    val editor = preferences.edit()
    editor.putString(key, value)
    editor.apply()
}

fun getData(key: String?, context: Context?): String? {
    val preferences = PreferenceManager.getDefaultSharedPreferences(context)
    return preferences.getString(key, null)
}

fun saveIntData (
key: String?,
value: Int,
context: Context?
) {
    val preferences = PreferenceManager.getDefaultSharedPreferences(context)
    val editor = preferences.edit()
    editor.putInt(key, value)
    editor.apply()

}

fun getIntData(key: String?, context: Context?): Int {
    val preferences = PreferenceManager.getDefaultSharedPreferences(context)
    return preferences.getInt(key, 0)
}