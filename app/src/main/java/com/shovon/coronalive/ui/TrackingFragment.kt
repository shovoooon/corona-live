package com.shovon.coronalive.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.shovon.coronalive.*
import com.shovon.coronalive.adapter.ContactsAdapter
import com.shovon.coronalive.database.DbManager
import com.shovon.coronalive.model.ContactsDataModel
import com.shovon.coronalive.model.GetLocationResponse
import com.shovon.coronalive.model.UpdateLocationResponse
import com.shovon.coronalive.network.RetrofitClient
import com.shovon.coronalive.viewmodel.TrackingViewModel
import kotlinx.android.synthetic.main.tracking_fragment.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class TrackingFragment : Fragment() , ContactsAdapter.OnItemClicked {

    companion object {
        fun newInstance() = TrackingFragment()
    }

    private lateinit var viewModel: TrackingViewModel
    var contactList = ArrayList<ContactsDataModel>()
    private val CONTACT_CODE:Int = 100
    private val LOCATION_CODE:Int = 400
    private lateinit var myLocationListener: LocationListener

    val DB_NAME = "corona_live.db"
    val TABLE_NAME = "contacts"
    val NAME = "name"
    val PHONE = "phone"
    val DB_VERSION = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.tracking_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(TrackingViewModel::class.java)
        rv_contacts.layoutManager = LinearLayoutManager(activity!!)


        showContacts()

        fab_add.setOnClickListener {
            checkContactsPermission()
        }

    }



    private fun checkContactsPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(activity!!, Manifest.permission.READ_CONTACTS)
            != PackageManager.PERMISSION_GRANTED){
                requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS), CONTACT_CODE)
            }else{
                chooseContact()
            }
        }else{
            chooseContact()
        }
    }

    override fun onResume() {
        showContacts()
        super.onResume()
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        Log.v("step", "request permission result")
        if (requestCode == CONTACT_CODE) {
            Log.v("step", requestCode.toString())
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.v("step", "permission granted")
                chooseContact()
            } else {
                Log.v("step", "did not given permission")
            }
        }
    }

    private fun chooseContact() {
        val intent = Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
        startActivityForResult(intent, 200)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == 200) {
            if (resultCode == Activity.RESULT_OK) {
                displayContact(data)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }


    private fun callGetLocation(name: String,phone: String?) {

        Log.v("step", "callGetLocation $phone")

        val dialog = ProgressDialog(activity!!)
        dialog.setMessage("Please wait...")
        dialog.setCancelable(false)
        dialog.show()


        RetrofitClient.instance.getLocation(phone!!)
            .enqueue(object :Callback<GetLocationResponse>{
                override fun onFailure(call: Call<GetLocationResponse>, t: Throwable) {
                    dialog.dismiss()
                    Log.v("step", "onFailure")
                    toast(context!!, getString(R.string.error_msg))
                }

                override fun onResponse(
                    call: Call<GetLocationResponse>,
                    response: Response<GetLocationResponse>
                ) {
                    Log.v("step", "onResponse")
                    dialog.dismiss()
                    if (!response.body()?.error!!){

                        Log.v("step", "not error response")
                        context?.startActivity(Intent(context, MapsActivity::class.java)
                            .putExtra("lat", response.body()?.lat.toString())
                            .putExtra("lon", response.body()?.lon.toString())
                            .putExtra("lastSeen", response.body()?.lastSeen.toString())
                            .putExtra("name", name)

                        )
                    }else{
                        toast(context!!, "User not registered")
                    }
                }

            })

    }


    private fun displayContact(data: Intent?) {
        val cursor: Cursor?

        try {
            val uri = data!!.data
            cursor = activity!!.contentResolver.query(uri!!, null, null, null, null)
            cursor?.moveToFirst()
            val phoneIndex = cursor?.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            val nameIndex =
                cursor?.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)

            val number = cursor?.getString(phoneIndex!!)
            val contactName = cursor?.getString(nameIndex!!)
            Log.v("name", contactName.toString())
            Log.v("number", number.toString())

            if (number.toString().length > 10) {
                if (!number.toString().startsWith("+", false)) {
                    toast(activity!!, "Add country-code to contact number")
                } else {
                    addContact(contactName!!, number!!)
                }
            }else{
                toast(activity!!, "Wrong contact number")
            }


        } catch (e: java.lang.Exception) {

        }
    }

    private fun addContact(name: String, phone: String) {
        val dbManager = DbManager(activity!!)
        val values = ContentValues()
        values.put(NAME, name)
        values.put(PHONE, phone)


        val ID = dbManager.insert(values)
        if (ID > 0) {
            toast(activity!!, "Contact added")
            showContacts()
        } else {
            toast(activity!!, "Error adding contact")
        }
    }

    private fun showContacts() {
        contactList.clear()
        val dbManager = DbManager(activity!!)
        val cursor = dbManager.showData
        if (cursor.count == 0) {
            tv_NoContact.visibility = View.VISIBLE
        } else {
            tv_NoContact.visibility = View.GONE
            while (cursor.moveToNext()) {

                val id = cursor.getInt(0)
                val name = cursor.getString(1)
                val phone = cursor.getString(2)

                contactList.add(
                    ContactsDataModel(
                        id,
                        name,
                        phone
                    )
                )
            }

        }
            rv_contacts.adapter =
                ContactsAdapter(activity!!, contactList, this)
    }

    override fun onDeleteClicked(id: Int) {
        val dbManager = DbManager(activity!!)
        dbManager.deleteContact(id)
        showContacts()
    }

    override fun onContactClicked(name:String,phone: String) {
        callGetLocation(name,phone)
    }



}
