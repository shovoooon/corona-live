package com.shovon.coronalive.adapter

import android.app.AlertDialog
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.shovon.coronalive.MapsActivity
import com.shovon.coronalive.R
import com.shovon.coronalive.model.ContactsDataModel
import com.shovon.coronalive.model.GetLocationResponse
import com.shovon.coronalive.network.RetrofitClient
import com.shovon.coronalive.toast
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.item_contact.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ContactsAdapter(val context: FragmentActivity?, private val product: List<ContactsDataModel>, private val onItemClicked: OnItemClicked) : RecyclerView.Adapter<ContactsAdapter.MyViewHolder>() {

    companion object {
        val TAG: String = ContactsAdapter::class.java.simpleName
    }

    interface OnItemClicked {
        fun onDeleteClicked(id: Int)
        fun onContactClicked(name:String,phone: String)
    }

    private lateinit var dialog:AlertDialog

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_contact, parent, false)
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

        var currentProduct: ContactsDataModel? = null
        var currentPosition: Int = 0

        init {
            itemView.setOnClickListener {
                Log.v("step", "itemView setOnClickListener")
                val phone = currentProduct?.phone.toString().replace(" ", "")
                getContact(currentProduct?.name.toString(),phone)
            }

            itemView.itemContactDelete.setOnClickListener {
                deleteContact(currentProduct?.id!!)
            }
        }

        fun setData(product: ContactsDataModel?, position: Int) {
            product?.let {
                itemView.itemContactName.text = product.name
            }

            this.currentProduct = product
            this.currentPosition = position
        }

    }

    private fun getContact(name:String,phone: String) {
        onItemClicked.onContactClicked(name,phone)
    }

    private fun deleteContact(id: Int) {

        onItemClicked.onDeleteClicked(id)
    }

    private fun dialogShow() {
        dialog = SpotsDialog.Builder().setContext(context).build()
        dialog.setCancelable(false)
        dialog.show()
    }
}