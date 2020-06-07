package com.shovon.coronalive.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.shovon.coronalive.R
import com.shovon.coronalive.model.CountriesResponse
import com.shovon.coronalive.model.CountryInfo
import kotlinx.android.synthetic.main.home_fragment.view.*
import kotlinx.android.synthetic.main.item.view.*


class CountriesAdapter(val context: FragmentActivity?,
                       private val product: List<CountriesResponse>,
private val onItemClicked: OnItemClicked) : RecyclerView.Adapter<CountriesAdapter.MyViewHolder>() {

    interface OnItemClicked {
        fun showCountryInfo(
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
        )

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return product.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val product = product[position]
        holder.setData(product, position)
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var currentProduct: CountriesResponse? = null
        var currentPosition: Int = 0

        init {
            itemView.setOnClickListener {
                onItemClicked.showCountryInfo(
                    currentProduct?.active!!,
                    currentProduct?.cases!!,
                    currentProduct?.casesPerOneMillion!!,
                    currentProduct?.country!!,
                    currentProduct?.countryInfo?.flag!!,
                    currentProduct?.critical!!,
                    currentProduct?.deaths!!,
                    currentProduct?.deathsPerOneMillion!!,
                    currentProduct?.recovered!!,
                    currentProduct?.tests!!,
                    currentProduct?.testsPerOneMillion!!,
                    currentProduct?.todayCases!!,
                    currentProduct?.todayDeaths!!,
                    currentProduct?.updated!!
                )
            }
        }

        fun setData(product: CountriesResponse?, position: Int) {
            product?.let {

                itemView.tv_itemCountry.text = product.country
                itemView.tv_itemTCases.text = product.cases.toString()
                if (product.todayCases != 0) {
                    itemView.tv_itemTodayCases.text = "+" + product.todayCases.toString()
                }else{
                    itemView.tv_itemTodayCases.text = ""
                }
                itemView.tv_itemTDead.text = product.deaths.toString()
                if (product.todayDeaths != 0) {
                    itemView.tv_itemTodayDead.text = "+" + product.todayDeaths.toString()
                }else{
                    itemView.tv_itemTodayDead.text = ""
                }
                itemView.tv_itemRecovered.text = product.recovered.toString()

                if (product.country.length < 18){
                    itemView.itemFlag.visibility = VISIBLE
                    Glide.with(context!!).load(product.countryInfo.flag).into(itemView.itemFlag)
                }else{
                    itemView.itemFlag.visibility = GONE
                }
            }

            this.currentProduct = product
            this.currentPosition = position
        }
    }
}
