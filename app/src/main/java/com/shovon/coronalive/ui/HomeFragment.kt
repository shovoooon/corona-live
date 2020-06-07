package com.shovon.coronalive.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.MPPointF
import com.shovon.coronalive.*
import com.shovon.coronalive.model.WorldResponse
import com.shovon.coronalive.network.RetrofitClient
import com.shovon.coronalive.viewmodel.HomeViewModel
import kotlinx.android.synthetic.main.home_fragment.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class HomeFragment : Fragment() {

    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.home_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)

        refreshLayout.setOnRefreshListener {
            if (isOnline(activity!!)) {
                callWorldInfo()
            } else {
                toast(activity!!, "No Internet Connection!!")
            }
        }
        putValues()
        if (isOnline(activity!!)) {
            callWorldInfo()
        } else {
            toast(activity!!, "No Internet Connection!!")
        }

        fab.setOnClickListener { share() }

    }


    private fun setupChart() {
        pieChart.visibility = View.VISIBLE
        pieChart.setUsePercentValues(true)
        pieChart.description.isEnabled = false
        pieChart.setExtraOffsets(5f, 10f, 5f, 5f)
        pieChart.dragDecelerationFrictionCoef = 0.95f
        pieChart.dragDecelerationFrictionCoef = 0.95f
        pieChart.centerText = "COVID-19\nPie Chart"
        pieChart.isDrawHoleEnabled = true
        pieChart.setHoleColor(Color.WHITE)
        pieChart.setTransparentCircleColor(Color.WHITE)
        pieChart.setTransparentCircleAlpha(110)
        pieChart.holeRadius = 40f
        pieChart.transparentCircleRadius = 61f
        pieChart.setDrawCenterText(true)
        pieChart.isRotationEnabled = true
        pieChart.isHighlightPerTapEnabled = true
        pieChart.animateY(1400, Easing.EaseInOutQuad)

        val entries: ArrayList<PieEntry> = ArrayList()
        val rRate = getData("recoveryRate", activity)!!.toFloat()
        val aRate = getData("affectedRate", activity)!!.toFloat()
        val dRate = getData("deathRate", activity)!!.toFloat()

        entries.add(PieEntry(rRate, "Recovery"))
        entries.add(PieEntry(aRate, "Active Cases"))
        entries.add(PieEntry(dRate, "Death"))

        val dataSet = PieDataSet(entries, "")
        dataSet.setDrawIcons(false)
        dataSet.iconsOffset = MPPointF(0f, 40f)
        dataSet.selectionShift = 5f
        dataSet.sliceSpace = 3f


        val colors: ArrayList<Int> = ArrayList()
        for (c in ColorTemplate.MATERIAL_COLORS) colors.add(c)
        dataSet.colors = colors


        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter(pieChart))
        data.setValueTextSize(12f)
        data.setValueTextColor(Color.WHITE)
        pieChart.data = data
        pieChart.highlightValues(null)

        pieChart.invalidate()

    }


    private fun callWorldInfo() {

        refreshLayout.isRefreshing = true
        RetrofitClient.instance.worldInfo("")
            .enqueue(object : Callback<WorldResponse> {
                override fun onFailure(call: Call<WorldResponse>, t: Throwable) {
                    Log.v("error_home", t.message.toString())
                    Toast.makeText(activity, getString(R.string.error_msg), Toast.LENGTH_SHORT)
                        .show()
                }

                override fun onResponse(
                    call: Call<WorldResponse>,
                    response: Response<WorldResponse>
                ) {
                    try {
                        if (refreshLayout.isRefreshing){
                            refreshLayout?.isRefreshing = false
                        }

                        val totalCases = response.body()?.cases!!.toString()
                        val totalRecovered = response.body()?.recovered!!.toString()
                        val totalDeath = response.body()?.deaths!!.toString()
                        val deathRate = String.format("%.2f", ((totalDeath.toFloat() * 100) / totalCases.toFloat()))
                        val recoveryRate = String.format("%.2f", ((totalRecovered.toFloat() * 100) / totalCases.toFloat()))
                        val activeCases = String.format("%.2f", (100 - (deathRate.toFloat() + recoveryRate.toFloat())))

                        saveData("tCases", totalCases, activity)
                        saveData("recovered", totalRecovered, activity)
                        saveData("dead", totalDeath, activity)
                        saveData("deathRate", deathRate, activity)
                        saveData("recoveryRate", recoveryRate, activity)
                        saveData("affectedRate", activeCases, activity)

                        putValues()
                        setupChart()

                    }catch (e:Exception){ Log.v("error", e.message!!)}
                }

            })
    }

    private fun putValues() {
        try {
            val totalCases = getData("tCases", activity).toString()
            val totalRecovered = getData("recovered", activity).toString()
            val totalDead = getData("dead", activity).toString()
            val deathRate = getData("deathRate", activity).toString()

            if (totalCases != "null"){
                tv_totalCases.text = totalCases
            }else{
                tv_totalCases.text = "0"
            }
            if (totalRecovered != "null"){
                tv_recovered.text = totalRecovered
            }else{
                tv_recovered.text = "0"
            }

            if (totalDead != "null"){
                tv_dead.text = totalDead
            }else{
                tv_dead.text = "0"
            }

            if (deathRate != "null"){
                tv_deadRate.text = "$deathRate%"
            }else{
                tv_deadRate.text = "0%"
            }
        }catch (e:Exception){ Log.v("error", e.message!!)}
    }

    private fun share() {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(
            Intent.EXTRA_TEXT,
            "Corona Live Update: \nTotal Cases: ${tv_totalCases.text} \nTotal Recovered: ${tv_recovered.text} \nTotal Deaths: ${tv_dead.text} \n \n" + getString(
                R.string.share_msg
            )
        )
        sendIntent.type = "text/plain"
        startActivity(Intent.createChooser(sendIntent, "Share via"))
    }

}

