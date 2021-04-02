package com.vportals.app.home

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
import com.vportals.app.service.ApiServiceInterface
import com.vportals.app.service.ApiUtils
import com.vportals.app.service.AppPreferences
import com.vportals.app.service.ProgressDialogUtil
import kotlinx.android.synthetic.main.activity_home.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeActivity : AppCompatActivity() {

    private lateinit var apiServiceInterface: ApiServiceInterface

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->

        when (item.itemId) {
            R.id.ab_profile -> {
//
//                val obj = JsonObject()
//                val objArr = JsonArray()
//                val jsSo = JsonObject()
//                val jsSoArr = JsonArray()
//
////            jsSo.addProperty("VoteOptionID", vodata?.VoteOptionID)
////            jsSo.addProperty("VOID", vID)
//            jsSo.addProperty("Rating", 0)
//            jsSo.addProperty("Comment", "")
////            obj.put("VOGID", "0")
//            jsSoArr.add(jsSo)
//            obj.add("SelectedOptions", jsSoArr)
//                //objArr.put(obj)
//                //jsonAr.add(obj)
//                Log.d("jsonObject77", obj.toString())
                //replaceFragment(FragmentProfile())
                return@OnNavigationItemSelectedListener false
            }
            R.id.ab_home -> {
                replaceFragment(FragmentHome())
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
        setContentView(R.layout.activity_home)
        apiServiceInterface = ApiUtils.getApiService(this)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            window.statusBarColor = Color.BLACK
        }
//        if (!AppPreferences.signIn) {
//            val intent = Intent(this@HomeActivity, LoginActivity::class.java)
//            startActivity(intent)
//            finish()
//        }

        bottomBar.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        bottomBar.selectedItemId = R.id.ab_home
        replaceFragment(FragmentHome())

    }
    private fun replaceFragment(fragment: Fragment) {
        val fragmentTransition = supportFragmentManager.beginTransaction()
        fragmentTransition.replace(R.id.fragmentContainer, fragment)
        fragmentTransition.commit()
    }

    private fun logout(){
        val alertDialog: AlertDialog.Builder = AlertDialog.Builder(this@HomeActivity)
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
            val intent = Intent(this@HomeActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
                    } else {
                        Log.d("jsonObject", response.message())
                        Toast.makeText(
                            this@HomeActivity,
                            "Error! Please try again!",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    dialog.cancel()
                    Log.d("jsonObject", t.message.toString())
                    Toast.makeText(this@HomeActivity, t.message, Toast.LENGTH_LONG).show()
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
