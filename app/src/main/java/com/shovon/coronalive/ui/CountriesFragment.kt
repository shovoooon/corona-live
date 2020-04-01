package com.shovon.coronalive.ui

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager

import com.shovon.coronalive.R
import com.shovon.coronalive.adapter.CountriesAdapter
import com.shovon.coronalive.isOnline
import com.shovon.coronalive.model.CountriesResponse
import com.shovon.coronalive.network.RetrofitClient
import com.shovon.coronalive.toast
import com.shovon.coronalive.viewmodel.CountriesViewModel
import kotlinx.android.synthetic.main.countries_fragment.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CountriesFragment : Fragment() {

    companion object {
        fun newInstance() = CountriesFragment()
    }

    private lateinit var viewModel: CountriesViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.countries_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(CountriesViewModel::class.java)

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

    }

    private fun fetchCounties(){
        rl_countries.isRefreshing = true

        RetrofitClient.instance.countriesInfo("")
            .enqueue(object :Callback<List<CountriesResponse>>{
                override fun onFailure(call: Call<List<CountriesResponse>>, t: Throwable) {
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
                        val countries = response.body()
                        countries?.let {
                            showCountries(it)
                        }
                    }catch (e:Exception){ Log.v("error", e.message!!)}


                }

            })
    }

    private fun showCountries(countries: List<CountriesResponse>) {
        try {
            rv.layoutManager = LinearLayoutManager(activity)
            rv.adapter = CountriesAdapter(activity, countries)
        }catch (e:Exception){ Log.v("error", e.message!!)}

    }

}
