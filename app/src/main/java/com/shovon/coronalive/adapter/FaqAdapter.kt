package com.shovon.coronalive.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.shovon.coronalive.R
import com.shovon.coronalive.model.FaqResponse
import kotlinx.android.synthetic.main.item_faq.view.*


class FaqAdapter(val context: FragmentActivity?, private val product: List<FaqResponse>) : RecyclerView.Adapter<FaqAdapter.MyViewHolder>() {

    companion object {
        val TAG: String = FaqAdapter::class.java.simpleName
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_faq, parent, false)
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

        var currentProduct: FaqResponse? = null
        var currentPosition: Int = 0

//        init {
//            itemView.setOnClickListener {
//                context?.startActivity(Intent(context, MessageActivity::class.java)
//                    .putExtra("id", currentProduct?.opposite_id)
//                    .putExtra("username", currentProduct?.username)
//                    .putExtra("imageUrl", currentProduct?.imageUrl))
//            }
//        }

        fun setData(product: FaqResponse?, position: Int) {
            product?.let {

                itemView.faqTitle.text = product.title
                itemView.faqDes.text = product.des

                Glide.with(itemView.context).load(product.icon).into(itemView.faqImg)
            }

            this.currentProduct = product
            this.currentPosition = position
        }
    }
}