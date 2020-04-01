package com.shovon.coronalive.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.shovon.coronalive.R
import com.shovon.coronalive.model.CountriesResponse
import kotlinx.android.synthetic.main.home_fragment.view.*
import kotlinx.android.synthetic.main.item.view.*


class CountriesAdapter(val context: FragmentActivity?, private val product: List<CountriesResponse>) : RecyclerView.Adapter<CountriesAdapter.MyViewHolder>() {

    companion object {
        val TAG: String = CountriesAdapter::class.java.simpleName
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

//        init {
//            itemView.setOnClickListener {
//                context?.startActivity(Intent(context, MessageActivity::class.java)
//                    .putExtra("id", currentProduct?.opposite_id)
//                    .putExtra("username", currentProduct?.username)
//                    .putExtra("imageUrl", currentProduct?.imageUrl))
//            }
//        }

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
            }

            this.currentProduct = product
            this.currentPosition = position
        }
    }
}