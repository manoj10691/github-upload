package com.vportals.app.voting

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.vportals.app.R
import com.vportals.app.auth.LoginActivity
import com.vportals.app.home.FragmentHome
import com.vportals.app.home.HomeActivity
import com.vportals.app.service.ApiServiceInterface
import com.vportals.app.service.ApiUtils
import com.vportals.app.service.AppPreferences
import com.vportals.app.service.ProgressDialogUtil
import kotlinx.android.synthetic.main.activity_voting_success.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VotingSuccessActivity : AppCompatActivity() {

    private lateinit var apiServiceInterface: ApiServiceInterface
    private var vName = ""
    private var vDate = ""
    private var vUnit = ""

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->

        when (item.itemId) {
            R.id.ab_profile -> {

                return@OnNavigationItemSelectedListener false
            }
            R.id.ab_home -> {
                finish()
                return@OnNavigationItemSelectedListener true
            }
            R.id.ab_logout -> {
                logout()
//                replaceFragment(FragmentHome())
                return@OnNavigationItemSelectedListener false
            }
        }
        false
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_voting_success)
        apiServiceInterface = ApiUtils.getApiService(this)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            window.statusBarColor = Color.BLACK
        }
        val intent = intent
        vName = intent.getStringExtra("vName").toString()
        vDate = intent.getStringExtra("vDate").toString()
        vUnit = intent.getStringExtra("vUnit").toString()
//        if (!AppPreferences.signIn) {
//            val intent = Intent(this@HomeActivity, LoginActivity::class.java)
//            startActivity(intent)
//            finish()
//        }

        bottomBarV.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        //bottomBarV.selectedItemId = 3
        bottomBarV.getMenu().setGroupCheckable(0, false, true);
        replaceFragment(FragmentVotingSuccess())
    }
    private fun replaceFragment(fragment: Fragment) {
        val bundle = Bundle()
        bundle.putString("v_name",vName)
        bundle.putString("v_date",vDate)
        bundle.putString("v_unit",vUnit)
        fragment.arguments = bundle
        val fragmentTransition = supportFragmentManager.beginTransaction()
        fragmentTransition.replace(R.id.fragmentContainer, fragment)
        fragmentTransition.commit()
    }

    private fun logout(){
        val alertDialog: AlertDialog.Builder = AlertDialog.Builder(this@VotingSuccessActivity)
        alertDialog.setTitle(R.string.dialogTitle)
        alertDialog.setMessage(R.string.dialogMessage)
        alertDialog.setPositiveButton(
            "Yes"
        ) { _, _ ->
            val dialog = ProgressDialogUtil.setProgressDialog(this, "Processing...")
            dialog.show()
            val call: Call<JsonObject> = apiServiceInterface.logout(AppPreferences.authToken.toString())
            call.enqueue(object : Callback<JsonObject> {
                override fun onResponse(
                    call: Call<JsonObject>,
                    response: Response<JsonObject>
                ) {
                    dialog.cancel()
                    if (response.isSuccessful) {
                        //Log.d("jsonObject", response.body().toString())
            AppPreferences.signIn = false
            val intent = Intent(this@VotingSuccessActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
                    } else {
                        Log.d("jsonObject", response.message())
                        Toast.makeText(
                            this@VotingSuccessActivity,
                            "Error! Please try again!",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    dialog.cancel()
                    Log.d("jsonObject", t.message.toString())
                    Toast.makeText(this@VotingSuccessActivity, t.message, Toast.LENGTH_LONG).show()
                }
            })
        }
        alertDialog.setNegativeButton(
            "No"
        ) { _, _ -> }
        val alert: AlertDialog = alertDialog.create()
        alert.setCanceledOnTouchOutside(false)
        alert.show()

    }
}
