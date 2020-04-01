package com.shovon.coronalive.ui

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.shovon.coronalive.MainActivity

import com.shovon.coronalive.R
import com.shovon.coronalive.model.RegisterResponse
import com.shovon.coronalive.network.RetrofitClient
import com.shovon.coronalive.saveData
import com.shovon.coronalive.toast
import com.shovon.coronalive.viewmodel.SignViewModel
import kotlinx.android.synthetic.main.faq_fragment.*
import kotlinx.android.synthetic.main.sign_fragment.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.TimeUnit

class SignFragment : Fragment() {

    companion object {
        fun newInstance() = SignFragment()
    }

    private lateinit var viewModel: SignViewModel
    lateinit var mAuth: FirebaseAuth
    lateinit var otpId: String
    lateinit var TOKEN: PhoneAuthProvider.ForceResendingToken
    private val LOCATION_CODE: Int = 300

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.sign_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(SignViewModel::class.java)

        mAuth = FirebaseAuth.getInstance()

        btnSign.setOnClickListener {
            val name = etName.text.toString().trim()
            val phone = etPhone.text.toString().trim().replace(" ", "")

            if (name.isNotEmpty()) {
                if (phone.length > 11) {
                    if (permissionGranted()) {
                        startVerification(name, phone)
                    }
                }else{
                    etPhone.requestFocus()
                    etPhone.error = "Invalid phone"
                    return@setOnClickListener
                }
            }else {
                etName.requestFocus()
                etName.error = "Invalid name"
                return@setOnClickListener
            }
        }
    }

    private fun permissionGranted(): Boolean {
        val granted: Boolean
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(
                    activity!!,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
                != PackageManager.PERMISSION_GRANTED
            ) {
                granted = false
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_CODE)
            } else {
                granted = true
            }
        } else {
            granted = true
        }
        return granted
    }

    private fun startVerification(name:String, phone: String) {

        val dialog = ProgressDialog(activity!!)
        dialog.setMessage("Verifying phone number...")
        dialog.setCancelable(false)
        dialog.show()

        PhoneAuthProvider.getInstance().verifyPhoneNumber(phone, 60L, TimeUnit.SECONDS, activity!!,
            object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                    dialog.dismiss()
                    toast(activity!!, "Verification completed")
                    callRegister(name, phone)

                }

                override fun onVerificationFailed(p0: FirebaseException) {
                    dialog.dismiss()
                    toast(activity!!, "Verification failed")
                }

                override fun onCodeAutoRetrievalTimeOut(p0: String) {
                    dialog.dismiss()
                    toast(activity!!, "Verification timeout")
                    super.onCodeAutoRetrievalTimeOut(p0)
                }

            })
    }

    private fun callRegister(name: String, phone: String) {
        val dialog = ProgressDialog(activity!!)
        dialog.setMessage("Signing in...")
        dialog.setCancelable(false)
        dialog.show()

        RetrofitClient.instance.register(name, phone)
            .enqueue(object : Callback<RegisterResponse> {
                override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                    dialog.dismiss()
                    toast(activity!!, getString(R.string.error_msg))
                }

                override fun onResponse(
                    call: Call<RegisterResponse>,
                    response: Response<RegisterResponse>
                ) {
                    dialog.dismiss()

                    if (!response.body()?.error!!) {
                        saveData("phone", phone, activity!!)
                        startActivity(Intent(activity!!, MainActivity::class.java))
                    } else {
                        toast(activity!!, getString(R.string.error_msg))
                    }
                }

            })

    }

}
