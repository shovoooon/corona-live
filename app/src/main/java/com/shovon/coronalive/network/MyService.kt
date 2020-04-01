package com.shovon.coronalive.network

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import com.shovon.coronalive.getData
import com.shovon.coronalive.model.UpdateLocationResponse
import com.shovon.coronalive.saveData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyService() : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    companion object{
        private val LOCATION_CODE:Int = 400
        private lateinit var myLocationListener: LocationListener
    }

    override fun onCreate() {
        super.onCreate()


    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        getLocation()

        return super.onStartCommand(intent, flags, startId)
    }



    //getting my location
    @SuppressLint("MissingPermission")
    private fun getLocation() {

        Log.v("step", "get location")

        myLocationListener = object : LocationListener {
            override fun onLocationChanged(location: Location?) {
                saveData("lat", location!!.latitude.toString(), this@MyService)
                saveData("lon", location.longitude.toString(), this@MyService)

                Log.v("lat", location!!.latitude.toString())
                Log.v("lon", location!!.longitude.toString())
                callUpdateLocation()
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                getLocation()
            }

            override fun onProviderEnabled(provider: String?) {
                getLocation()
            }

            override fun onProviderDisabled(provider: String?) {
                //showDialog()

            }


        }

        val locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 5f, myLocationListener)
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 5f, myLocationListener)

    }

    //send my location to server
    private fun callUpdateLocation() {
        Log.v("step", "callUpdateLocation")

        val lat = getData("lat", this).toString()
        val lon = getData("lon", this).toString()
        val myNum = getData("phone", this).toString()

        RetrofitClient.instance.updateLocation(myNum, lat, lon)
            .enqueue(object : Callback<UpdateLocationResponse> {
                override fun onFailure(call: Call<UpdateLocationResponse>, t: Throwable) {
                    Log.v("step", "Failed")
                }

                override fun onResponse(
                    call: Call<UpdateLocationResponse>,
                    response: Response<UpdateLocationResponse>
                ) {
                    if (!response.body()?.error!!){
                        Log.v("step", "Success")
                    }
                }

            })
    }

}