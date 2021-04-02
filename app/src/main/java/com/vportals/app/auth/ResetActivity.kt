package com.vportals.app.auth

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.google.gson.JsonObject
import com.vportals.app.*
import com.vportals.app.home.HomeActivity
import com.vportals.app.service.ApiServiceInterface
import com.vportals.app.service.ApiUtils
import com.vportals.app.service.ProgressDialogUtil
import kotlinx.android.synthetic.main.activity_login.*
import okhttp3.FormBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlinx.android.synthetic.main.activity_reset.*
import kotlinx.android.synthetic.main.activity_reset.edtEmail
import kotlinx.android.synthetic.main.activity_reset.edtPortal

class ResetActivity : AppCompatActivity() {

    private lateinit var apiServiceInterface: ApiServiceInterface

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_reset)

        apiServiceInterface = ApiUtils.getApiService(this)
        btnSend.setOnClickListener {
            val portal = edtPortal.text.toString()
            val email = edtEmail.text.toString()
            //validate form
            if (validatReset(portal, email)) {
                //do auth
                doReset(portal, email)
            }
        }
        tvReturnToLogin.setOnClickListener {
            val intent = Intent(this@ResetActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        btnRetrunToLogin.setOnClickListener {
            val intent = Intent(this@ResetActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        edtPortal.addTextChangedListener(object: TextWatcher {
                override fun onTextChanged(s:CharSequence, start:Int, before:Int, count:Int) {
                    checkBtn()
                }
                override fun beforeTextChanged(s:CharSequence, start:Int, count:Int, after:Int) {
                }
                override fun afterTextChanged(s: Editable) {
                }
            })

            edtEmail.addTextChangedListener(object: TextWatcher {

            override fun onTextChanged(s:CharSequence, start:Int, before:Int, count:Int) {
               checkBtn()
            }
            override fun beforeTextChanged(s:CharSequence, start:Int, count:Int, after:Int) {
            }
            override fun afterTextChanged(s: Editable) {
            }
        })

        checkBtn()

        edtPortal.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus)
                edtPortal.hint = ""
            else
                edtPortal.hint = getString(R.string.enter_portal_number)
        }

        edtEmail.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus)
                edtEmail.hint = ""
            else
                edtEmail.hint = getString(R.string.e_g_john_example_com)
        }

    }
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun checkBtn() {
        if(edtPortal.text.toString().trim().isNotEmpty() && edtEmail.text.toString().trim().isNotEmpty() ){
            btnSend.isEnabled = true
            btnSend.backgroundTintList = ColorStateList.valueOf(ResourcesCompat.getColor(resources,
                R.color.button_bg, theme))

        }
        else{
            btnSend.isEnabled = false
            btnSend.backgroundTintList = ColorStateList.valueOf(ResourcesCompat.getColor(resources,
                R.color.button_bg_disable, theme))
        }
    }

    private fun validatReset(portal: String?, email: String?): Boolean {
        if (portal == null || portal.trim { it <= ' ' }.isEmpty()) {
            Toast.makeText(this, "Portal Number is required", Toast.LENGTH_LONG).show()
            return false
        }
        if (email == null || email.trim { it <= ' ' }.isEmpty()) {
            Toast.makeText(this, "Email is required", Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }

    private fun doReset(portal: String, email: String) {
        val dialog = ProgressDialogUtil.setProgressDialog(this, "Processing...")
        dialog.show()
        val formBody: RequestBody = FormBody.Builder()
            .add("PortalNumber", portal)
            .add("EmailAddress", email)
            .add("deviceID", "13456")
            .build()
        val call: Call<JsonObject> = apiServiceInterface.reset(formBody)
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(
                call: Call<JsonObject>,
                response: Response<JsonObject>
            ) {
                dialog.cancel()
                if (response.isSuccessful) {
                    Log.d("response", response.body().toString());
                    val jsonObject: JsonObject? = response.body()
                    Log.d("jsonObjectM", jsonObject?.get("Flag").toString())
                    var message: String = jsonObject?.get("message").toString()
                    var flag = jsonObject?.get("Flag").toString();
                    var flagCheck: Boolean? = flag.toBoolean() ?: false;
                    if (flagCheck == true) {
                        llr.visibility = View.GONE
                        llrs.visibility = View.VISIBLE
                        btnSend.visibility = View.GONE
                        btnRetrunToLogin.visibility = View.VISIBLE
                        tvemailsentdesc.text = message.replace("\"", "")
//                        Log.d("jsonObject", jsonObject.toString())
//                        Log.d("jsonObject", jsonObject?.getAsJsonObject("Data")?.get("FirstName").toString())
//                        var fname = jsonObject?.getAsJsonObject("Data")?.get("FirstName").toString();
//                        var lname = jsonObject?.getAsJsonObject("Data")?.get("LastName").toString();
//                        val intent = Intent(this@ResetActivity, HomeActivity::class.java)
//                        intent.putExtra("fullname", fname.plus(" ").plus(lname))
//                        startActivity(intent)
                    } else {
                        Log.d("jsonObject", "null")

                        tvrpr.visibility = View.GONE
                        tveyp.visibility = View.GONE
                        tvrperror.visibility = View.VISIBLE
                        tvrperror.text = message.replace("\"", "")
                        edtPortal.setBackgroundResource(R.drawable.edit_text_error)
                        edtEmail.setBackgroundResource(R.drawable.edit_text_error)
                        edtPortal.setTextColor(Color.RED)
                        edtEmail.setTextColor(Color.RED)
//                        Toast.makeText(
//                            this@ResetActivity,
//                            message,
//                            Toast.LENGTH_LONG
//                        ).show()
                    }
                } else {
                    Log.d("jsonObject", response.message())
                    Toast.makeText(
                        this@ResetActivity,
                        "Error! Please try again!",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                dialog.cancel()
                Log.d("jsonObject", t.message.toString())
                Toast.makeText(this@ResetActivity, t.message, Toast.LENGTH_LONG).show()
            }
        })
    }
}
