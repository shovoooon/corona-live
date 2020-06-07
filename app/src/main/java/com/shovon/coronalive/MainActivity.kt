package com.shovon.coronalive

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.format.DateFormat
import android.util.Log
import android.view.Menu
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.shovon.coronalive.adapter.ViewPagerAdapter
import com.shovon.coronalive.model.Search
import com.shovon.coronalive.network.MyService
import com.shovon.coronalive.network.RetrofitClient
import com.shovon.coronalive.ui.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog.view.*
import kotlinx.android.synthetic.main.dialog.view.tv_dialogCountry
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class MainActivity : AppCompatActivity() {

    private var CLOSE_APP = false
    private val LOCATION_CODE:Int = 400
    private lateinit var myLocationListener: LocationListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(HomeFragment(), "")
        adapter.addFragment(CountriesFragment(), "")
        adapter.addFragment(FAQFragment(), "")

        if (getData("phone", this).isNullOrEmpty()){
            adapter.addFragment(SignFragment(), "")
        }else{
            adapter.addFragment(TrackingFragment(), "")
        }

        view_pager.adapter = adapter
        tabs.setupWithViewPager(view_pager)


        tabs.getTabAt(0)?.setIcon(R.drawable.ic_world)
        tabs.getTabAt(1)?.setIcon(R.drawable.ic_flag)
        tabs.getTabAt(2)?.setIcon(R.drawable.ic_faq)
        tabs.getTabAt(3)?.setIcon(R.drawable.ic_my_location)


        if (!getData("phone", this).isNullOrEmpty()){
            if (locationPermissionGranted()){
                startBackgroundService()
                getLocation()
            }
        }


    }

    override fun onResume() {

        if (!getData("phone", this).isNullOrEmpty()){
            if (locationPermissionGranted()){
                startBackgroundService()
                getLocation()
            }
        }

        super.onResume()
    }

    private fun startBackgroundService() {

        val intent = Intent(baseContext, MyService::class.java)
        startService(intent)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        val item = menu?.findItem(R.id.menuSearch)
        val searchView = item?.actionView as androidx.appcompat.widget.SearchView

        searchView.setOnQueryTextListener(object :androidx.appcompat.widget.SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    search(query)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
//                    fetchSearch(newText)
                }
                return false

            }

        })

        return super.onCreateOptionsMenu(menu)
    }

    private fun search(query: String) {
        progress_bar.visibility = VISIBLE

        RetrofitClient.instance.search(query)
            .enqueue(object :Callback<Search>{
                override fun onFailure(call: Call<Search>, t: Throwable) {
                    progress_bar.visibility = GONE
                    toast(this@MainActivity, getString(R.string.error_msg))
                }

                override fun onResponse(call: Call<Search>, response: Response<Search>) {
                    progress_bar.visibility = GONE
                    if (response.body()?.country != null){
                        val mView = layoutInflater.inflate(R.layout.dialog, null)
                        Glide.with(this@MainActivity).load(response.body()?.countryInfo?.flag.toString()).into(mView.dialogFlag)
                        mView.tv_dialogCountry.text = response.body()?.country
                        mView.tv_dialogTotalCases.text = response.body()?.cases.toString()
                        mView.tv_dialogTodayCases.text = response.body()?.todayCases.toString()
                        mView.tv_dialogRecovered.text = response.body()?.recovered.toString()
                        mView.tv_dialogTDead.text = response.body()?.deaths.toString()
                        mView.tv_dialogTodayDead.text = response.body()?.todayDeaths.toString()
                        mView.tv_dialogActiveCases.text = response.body()?.active.toString()
                        mView.tv_dialogCritical.text = response.body()?.critical.toString()
                        mView.tv_dialogCPM.text = response.body()?.casesPerOneMillion.toString()
                        val totalCases = response.body()?.cases!!.toString()
                        val totalDeath = response.body()?.deaths!!.toString()
                        val deathRate = String.format("%.2f", ((totalDeath.toFloat() * 100) / totalCases.toFloat()))
                        mView.tv_dialogDRate.text = deathRate.toString()+"%"

                        mView.tv_dialogTPerM.text = response.body()?.testsPerOneMillion.toString()
                        mView.tv_dialogTested.text = response.body()?.tests.toString()

                        val cal = Calendar.getInstance(Locale.ENGLISH)
                        cal.timeInMillis = response.body()?.updated!!
                        val date =
                            DateFormat.format("dd MMMM yyyy hh:mm a", cal).toString()
                        mView.dialog_update.text = "Updated: $date"

                        val dialog = Dialog(this@MainActivity)
                        dialog.setTitle(getString(R.string.app_name))
                        dialog.setCancelable(false)
                        dialog.setContentView(mView)
                        mView.btn_dialogClose.setOnClickListener { dialog.dismiss() }
                        dialog.show()

                    }else{
                        toast(this@MainActivity, "Country not found")
                    }
                }

            })
    }



    override fun onBackPressed() {
        if (CLOSE_APP) {
            finishAffinity()
            finish()
        } else {
            CLOSE_APP = true
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show()
        }
    }

    private fun locationPermissionGranted(): Boolean {
        val granted: Boolean
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
                granted = false
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_CODE
                )
            } else {
                granted = true
            }
        } else {
            granted = true
        }
        return granted
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == LOCATION_CODE){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                startBackgroundService()
            }else{
                if (locationPermissionGranted()) return
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    //getting my location
    @SuppressLint("MissingPermission")
    private fun getLocation() {

        Log.v("step", "get location")

        myLocationListener = object : LocationListener {
            override fun onLocationChanged(location: Location?) {
                saveData("lat", location!!.latitude.toString(), this@MainActivity)
                saveData("lon", location.longitude.toString(), this@MainActivity)

                Log.v("lat", location!!.latitude.toString())
                Log.v("lon", location!!.longitude.toString())
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                getLocation()
            }

            override fun onProviderEnabled(provider: String?) {
                getLocation()
            }

            override fun onProviderDisabled(provider: String?) {
                showDialog()

            }


        }

        val locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager.requestLocationUpdates(
            LocationManager.NETWORK_PROVIDER, 10000, 5f,
            myLocationListener
        )
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER, 10000, 5f,
            myLocationListener
        )

    }

    private fun showDialog() {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Location/GPS")
        dialog.setMessage("Please turn on your location or GPS")
        dialog.setPositiveButton("Turn On"){ _, _ ->
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        }
        dialog.setNegativeButton("Close"){ _, _ ->
            getLocation()
        }
        dialog.show()
    }

}
