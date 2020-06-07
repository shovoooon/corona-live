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
import com.shovon.coronalive.model.States
import kotlinx.android.synthetic.main.home_fragment.view.*
import kotlinx.android.synthetic.main.item.view.*
import kotlinx.android.synthetic.main.item.view.tv_itemTodayCases
import kotlinx.android.synthetic.main.item_state.view.*


class StatesAdapter(val context: FragmentActivity?,
                    private val product: List<States>) : RecyclerView.Adapter<StatesAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_state, parent, false)
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

        var currentProduct: States? = null
        var currentPosition: Int = 0

//        init {
//            itemView.setOnClickListener {
//                onItemClicked.showCountryInfo(
//                    currentProduct?.active!!,
//                    currentProduct?.cases!!,
//                    currentProduct?.casesPerOneMillion!!,
//                    currentProduct?.country!!,
//                    currentProduct?.countryInfo?.flag!!,
//                    currentProduct?.critical!!,
//                    currentProduct?.deaths!!,
//                    currentProduct?.deathsPerOneMillion!!,
//                    currentProduct?.recovered!!,
//                    currentProduct?.tests!!,
//                    currentProduct?.testsPerOneMillion!!,
//                    currentProduct?.todayCases!!,
//                    currentProduct?.todayDeaths!!,
//                    currentProduct?.updated!!
//                )
//            }
//        }

        fun setData(product: States?, position: Int) {
            product?.let {

                itemView.itemState.text = product.state
                itemView.stateTCases.text = product.cases.toString()
                if (product.todayCases != 0) {
                    itemView.stateTodayCases.text = "+" + product.todayCases.toString()
                }else{
                    itemView.stateTodayCases.text = ""
                }
                itemView.stateTodayDead.text = product.todayDeaths.toString()
                if (product.todayDeaths != 0) {
                    itemView.stateTodayDead.text = "+" + product.todayDeaths.toString()
                }else{
                    itemView.stateTodayDead.text = ""
                }
                itemView.stateActive.text = product.active.toString()

                itemView.stateTasted.text = product.tests.toString()
                itemView.stateTastedM.text = product.testsPerOneMillion.toString()
                itemView.stateTDead.text = product.deaths.toString()

            }

            this.currentProduct = product
            this.currentPosition = position
        }
    }
}
