package com.shovon.coronalive.ui

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.shovon.coronalive.*

import com.shovon.coronalive.adapter.CountriesAdapter
import com.shovon.coronalive.adapter.FaqAdapter
import com.shovon.coronalive.model.CountriesResponse
import com.shovon.coronalive.model.FaqResponse
import com.shovon.coronalive.network.RetrofitClient
import com.shovon.coronalive.viewmodel.FaqViewModel
import kotlinx.android.synthetic.main.countries_fragment.*
import kotlinx.android.synthetic.main.faq_fragment.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList


class FAQFragment : Fragment() {

    companion object {
        fun newInstance() = FAQFragment()
        private val listFaq = ArrayList<FaqResponse>()
        private lateinit var adapter: FaqAdapter
    }

    private lateinit var viewModel: FaqViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.faq_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(FaqViewModel::class.java)
        setUpRecyclerView()

        if (isOnline(activity!!)){
            fetchFaq("corona")
        }else{
            toast(activity!!, "No Internet Connection!!")
        }

        btnBangla.setOnClickListener {
            btnEnglish.isChecked = false
            if (isOnline(activity!!)){
                fetchFaq("corona_bn")
            }else{
                toast(activity!!, "No Internet Connection!!")
            }

        }
        btnEnglish.setOnClickListener {
            btnBangla.isChecked = false
            if (isOnline(activity!!)){
                fetchFaq("corona")
            }else{
                toast(activity!!, "No Internet Connection!!")
            }

        }

    }

    private fun setUpRecyclerView() {
        adapter = FaqAdapter(activity!!, listFaq)
        rv_faq.layoutManager = LinearLayoutManager(activity!!)
        rv_faq.hasFixedSize()
        rv_faq.adapter = adapter
    }


    private fun fetchFaq(table:String){

        RetrofitClient.instance.faqInfo(table)
            .enqueue(object : Callback<List<FaqResponse>> {
                override fun onFailure(call: Call<List<FaqResponse>>, t: Throwable) {
                    toast(activity!!, getString(R.string.error_msg))
                }

                override fun onResponse(
                    call: Call<List<FaqResponse>>,
                    response: Response<List<FaqResponse>>
                ) {
                    try {
                        listFaq.clear()
                        listFaq.addAll(response.body()!!)
                        adapter.notifyDataSetChanged()

                    }catch (e:Exception){ Log.v("error", e.message!!)}

                }

            })
    }


}
