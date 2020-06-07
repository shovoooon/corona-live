package com.shovon.coronalive.ui

import android.app.Dialog
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.shovon.coronalive.R
import com.shovon.coronalive.adapter.CountriesAdapter
import com.shovon.coronalive.adapter.StatesAdapter
import com.shovon.coronalive.isOnline
import com.shovon.coronalive.model.CountriesResponse
import com.shovon.coronalive.model.States
import com.shovon.coronalive.network.RetrofitClient
import com.shovon.coronalive.toast
import com.shovon.coronalive.viewmodel.CountriesViewModel
import kotlinx.android.synthetic.main.countries_fragment.*
import kotlinx.android.synthetic.main.dialog.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class CountriesFragment : Fragment(), CountriesAdapter.OnItemClicked {

    private lateinit var viewModel: CountriesViewModel
    private val listCountries = ArrayList<CountriesResponse>()
    private val listStates = ArrayList<States>()
    private lateinit var adapter: CountriesAdapter
    private lateinit var tatesAdapter: StatesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.countries_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(CountriesViewModel::class.java)
        adapter = CountriesAdapter(activity!!, listCountries, this)
        tatesAdapter = StatesAdapter(activity!!, listStates)
        rv.layoutManager = LinearLayoutManager(activity!!)
        rv.hasFixedSize()
        rv.adapter = adapter


        rl_countries.setOnRefreshListener {

            if (isOnline(activity!!)){
                fetchCounties()
            }else{
                toast(activity!!, "No Internet Connection!!")
            }
        }
        if (isOnline(activity!!)){
            fetchCounties()
        }else{
            toast(activity!!, "No Internet Connection!!")
        }

        btnCountries.setOnClickListener {
            btnStates.isChecked = false
            rv.adapter = adapter
                fetchCounties()
        }

        btnStates.setOnClickListener {
                btnCountries.isChecked = false
            rv.adapter = tatesAdapter
                loadStates()
        }

    }

    private fun loadStates() {
        rl_countries.isRefreshing = true

        RetrofitClient.instance.states("states")
            .enqueue(object :Callback<List<States>>{
                override fun onFailure(call: Call<List<States>>, t: Throwable) {
                    toast(activity!!, getString(R.string.error_msg))
                }

                override fun onResponse(
                    call: Call<List<States>>,
                    response: Response<List<States>>
                ) {
                    if (response.code() == 200){
                        try {
                            if (rl_countries.isRefreshing){
                                rl_countries.isRefreshing = false
                            }
                            listStates.clear()
                            listStates.addAll(response.body()!!)
                            adapter.notifyDataSetChanged()
                            rv.adapter = tatesAdapter

                        }catch (e:Exception){ Log.v("error", e.message!!)}
                    }
                }

            })
    }

    private fun fetchCounties(){
        rl_countries.isRefreshing = true

        RetrofitClient.instance.countriesInfo("")
            .enqueue(object :Callback<List<CountriesResponse>>{
                override fun onFailure(call: Call<List<CountriesResponse>>, t: Throwable) {
                    Log.v("error_countries", t.message.toString())
                    toast(activity!!, getString(R.string.error_msg))
                }

                override fun onResponse(
                    call: Call<List<CountriesResponse>>,
                    response: Response<List<CountriesResponse>>
                ) {
                    try {
                        if (rl_countries.isRefreshing){
                            rl_countries.isRefreshing = false
                        }
                        listCountries.clear()
                        listCountries.addAll(response.body()!!)
                        adapter.notifyDataSetChanged()

                    }catch (e:Exception){ Log.v("error", e.message!!)}


                }

            })
    }

    override fun showCountryInfo(
        active: Int,
        cases: Int,
        casesPerOneMillion: Float,
        country: String,
        flag: String,
        critical: Int,
        deaths: Int,
        deathsPerOneMillion: Float,
        recovered: Int,
        tests: Int,
        testsPerOneMillion: Int,
        todayCases: Int,
        todayDeaths: Int,
        updated: Long
    ) {
        val mView = layoutInflater.inflate(R.layout.dialog, null)
        Glide.with(this).load(flag).into(mView.dialogFlag)
        mView.tv_dialogCountry.text = country
        mView.tv_dialogTotalCases.text = cases.toString()
        mView.tv_dialogTodayCases.text = todayCases.toString()
        mView.tv_dialogRecovered.text = recovered.toString()
        mView.tv_dialogTDead.text = deaths.toString()
        mView.tv_dialogTodayDead.text = todayDeaths.toString()
        mView.tv_dialogActiveCases.text = active.toString()
        mView.tv_dialogCritical.text = critical.toString()
        mView.tv_dialogCPM.text = casesPerOneMillion.toString()
        mView.tv_dialogTPerM.text = testsPerOneMillion.toString()
        mView.tv_dialogTested.text = tests.toString()
        val totalCases = cases.toString()
        val totalDeath = deaths.toString()
        val deathRate = String.format("%.2f", ((totalDeath.toFloat() * 100) / totalCases.toFloat()))
        mView.tv_dialogDRate.text = deathRate.toString()+"%"

        val cal = Calendar.getInstance(Locale.ENGLISH)
        cal.timeInMillis = updated
        val date =
            DateFormat.format("dd MMMM yyyy hh:mm a", cal).toString()
        mView.dialog_update.text = "Updated: $date"

        val dialog = Dialog(activity!!)
        dialog.setCancelable(false)
        dialog.setContentView(mView)
        mView.btn_dialogClose.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

}
