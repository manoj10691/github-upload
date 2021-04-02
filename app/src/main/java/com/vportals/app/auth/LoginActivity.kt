package com.vportals.app.auth

import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.andrefrsousa.superbottomsheet.SuperBottomSheetFragment
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.JsonObject
import com.vportals.app.*
import com.vportals.app.home.HomeActivity
import com.vportals.app.service.ApiServiceInterface
import com.vportals.app.service.ApiUtils
import com.vportals.app.service.AppPreferences
import com.vportals.app.service.ProgressDialogUtil
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.edtEmail
import kotlinx.android.synthetic.main.activity_login.edtPortal
import kotlinx.android.synthetic.main.fragment_help_bottom.view.*
import kotlinx.android.synthetic.main.fragment_help_bottom.*
import kotlinx.android.synthetic.main.fragment_help_bottom.intro_pager as digital_pager
import kotlinx.android.synthetic.main.fragment_help_bottom.into_tab_layout as pager_tab_layout
import kotlinx.android.synthetic.main.fragment_home.*
import okhttp3.FormBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginActivity : AppCompatActivity() {

    private lateinit var apiServiceInterface: ApiServiceInterface

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_login)
        if (AppPreferences.signIn) {
            val intent = Intent(this@LoginActivity, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        apiServiceInterface = ApiUtils.getApiService(this)

    btnLogin.setOnClickListener {
            val portal = edtPortal.text.toString()
            val email = edtEmail.text.toString()
            val password = edtPassword.text.toString()
            //validate form
            if (validateLogin(portal, email, password)) {
                //do auth
                doLogin(portal, email, password)
            }
            else{
                ltvrperror.visibility = View.VISIBLE
                ltvrperror.text = "Please enter the valid login details"
                edtPortal.setBackgroundResource(R.drawable.edit_text_error)
                edtPortal.setTextColor(Color.RED)
                edtEmail.setBackgroundResource(R.drawable.edit_text_error)
                edtEmail.setTextColor(Color.RED)
                edtPassword.setBackgroundResource(R.drawable.edit_text_error)
                edtPassword.setTextColor(Color.RED)
            }
        }
    forgot.setOnClickListener {

            val intent = Intent(this@LoginActivity, ResetActivity::class.java)
            startActivity(intent)
            finish()
        }
    helpbutton.setOnClickListener {


        val sheet = BottomSheetFragment()

            sheet.show(supportFragmentManager, "BottomSheetFragment")

        }

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
                edtEmail.hint = getString(R.string.enter_your_email)
        }

        edtPassword.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus)
                edtPassword.hint = ""
            else
                edtPassword.hint = getString(R.string.enter_your_password)
        }

        remember_me.setOnClickListener {
            remember.isChecked = !remember.isChecked
        }

    }

    private fun validateLogin(portal: String?, email: String?, password: String?): Boolean {
        if (portal == null || portal.trim { it <= ' ' }.isEmpty()) {
            return false
        }
        if (email == null || email.trim { it <= ' ' }.isEmpty()) {
            return false
        }
        if (password == null || password.trim { it <= ' ' }.isEmpty()) {
            return false
        }
        return true
    }

    private fun doLogin(portal: String, email: String, password: String) {
        val dialog = ProgressDialogUtil.setProgressDialog(this, "Processing...")
        dialog.show()
        val formBody: RequestBody = FormBody.Builder()
            .add("PortalNumber", portal)
            .add("UserName", email)
            .add("Password", password)
            .add("deviceToken", "52.170.151.93")
            .build()
        val call: Call<JsonObject> = apiServiceInterface.login(formBody)
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(
                call: Call<JsonObject>,
                response: Response<JsonObject>
            ) {
                dialog.cancel()
                if (response.isSuccessful) {
                    val jsonObject: JsonObject? = response.body()
                    Log.d("jsonObjectM", jsonObject?.get("Flag").toString())
                    var flag = jsonObject?.get("Flag").toString();
                    var message: String = jsonObject?.get("Message").toString()
                    var flagCheck: Boolean? = flag.toBoolean() ?: false;
                    if (flagCheck == true) {

                        Log.d("jsonObject", jsonObject.toString())


                        val dObject = jsonObject?.getAsJsonObject("Data")

                        Log.d(
                            "jsonObject",
                            dObject?.get("FirstName")?.asString!!
                        )

                        var fname:String = dObject?.get("FirstName")?.asString!!
                        var lname:String = dObject?.get("LastName")?.asString!!
                        var logo:String = dObject?.get("AssociationLogoURL")?.asString!!
                        var aname:String = dObject?.get("AssociationName")?.asString!!
                        var sectorid:String = dObject?.get("SectorID")?.asString!!
                        var email:String = dObject?.get("EmailAddress")?.asString!!
                        var phone:String = dObject?.get("PhoneNo")?.asString!!
                        var auth:String = dObject?.get("AuthToken")?.asString!!

                        if (remember.isChecked) {
                            AppPreferences.signIn = true
                        }
                        AppPreferences.firstName = fname.trim()
                        AppPreferences.lastName = lname.trim()
                        AppPreferences.fullName = fname.plus(" ").plus(lname)
                        AppPreferences.emailId = email.trim()
                        AppPreferences.phoneNo = phone.trim()
                        AppPreferences.logoUrl = ApiUtils.baseCDNUrl.plus(logo.trim())
                        AppPreferences.aName = aname.trim()
                        AppPreferences.sectorId = sectorid.trim()
                        AppPreferences.authToken = auth.trim()

                        val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                        startActivity(intent)
                        finish()

                    } else {
                        Log.d("jsonObject", "null")
                        ltvrperror.visibility = View.VISIBLE
                        ltvrperror.text = message.replace("\"", "")
                        edtPortal.setBackgroundResource(R.drawable.edit_text_error)
                        edtEmail.setBackgroundResource(R.drawable.edit_text_error)
                        edtPassword.setBackgroundResource(R.drawable.edit_text_error)

                        edtPortal.setTextColor(Color.RED)
                        edtEmail.setTextColor(Color.RED)
                        edtPassword.setTextColor(Color.RED)

                    }
                } else {
                    Log.d("jsonObject", response.message())
                    Toast.makeText(
                        this@LoginActivity,
                        "Error! Please try again!",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                dialog.cancel()
                Log.d("jsonObject", t.message.toString())
                Toast.makeText(this@LoginActivity, t.message, Toast.LENGTH_LONG).show()
            }
        })
    }

}
class BottomSheetFragment : SuperBottomSheetFragment() {

    private val mPageNumbers = 2

    private fun getListOfPagerContents(): List<Array<String>> {

        val ar1 = arrayOf(getString(R.string.intro_title_1), getString(R.string.intro_sub_title_1),"R" )
        val ar2 = arrayOf(getString(R.string.intro_title_2), getString(R.string.intro_sub_title_2) ,"G")
        return listOf(ar1,ar2)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    val pagerAdapter =
        ViewPagerAdapter(this,getListOfPagerContents(),mPageNumbers)
    digital_pager.adapter = pagerAdapter

    TabLayoutMediator(pager_tab_layout, digital_pager)
    { tab, position ->}.attach()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_help_bottom, container, false)

    }

    private val height = Resources.getSystem().getDisplayMetrics().heightPixels

    private val maxHeight = (height * 0.80).toInt()

    override fun getPeekHeight() = maxHeight

    override fun getCornerRadius() = context!!.resources.getDimension(R.dimen.sheet_rounded_corner)

    override fun getExpandedHeight() = maxHeight

    override fun getDim(): Float {
        return 0.0f
    }
    override fun animateStatusBar(): Boolean {
        return false
    }

    override fun isSheetAlwaysExpanded() = true
}

