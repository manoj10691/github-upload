package com.vportals.app.home

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.res.ResourcesCompat
import coil.load
import com.google.gson.JsonObject
import com.vportals.app.R
import com.vportals.app.service.ApiServiceInterface
import com.vportals.app.service.ApiUtils
import com.vportals.app.service.AppPreferences
import com.vportals.app.service.ProgressDialogUtil
import com.vportals.app.survey.StartSurveyActivity
import com.vportals.app.voting.VotingSuccessActivity
import kotlinx.android.synthetic.main.activity_ballot_otp.*
import okhttp3.FormBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat


class BallotOtpActivity : AppCompatActivity() {

    private lateinit var apiServiceInterface: ApiServiceInterface
    //private var otpTextView: AppCompatEditText? = null
    private var strOtp: String = ""
    private var vID = ""
    private var vAddress = ""
    private var vUnit = ""
    private var vToken = ""
    private var vName = ""
    private var vDate = ""
    private var vTypeID = ""

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ballot_otp)
        apiServiceInterface = ApiUtils.getApiService(this)


        val intent = intent

        vName = intent.getStringExtra("vName").toString()
        vDate = intent.getStringExtra("vDate").toString()
        vID = intent.getStringExtra("votId").toString()
        vAddress = intent.getStringExtra("vAddress").toString()
        vUnit = intent.getStringExtra("vUnit").toString()
        vTypeID = intent.getStringExtra("vTypeID").toString()

        println(vDate)
        //Log.d("response7", vDate);
        titlemorning.text = vName

        if(!vDate.isNullOrEmpty()){
            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            val formatter = SimpleDateFormat("MM/dd/yyyy")

            val output =  formatter.format(parser.parse(vDate))

            titledate.text = "Closes: ".plus(output)
        }
        else {
            if(vTypeID == "1")
            titledate.text = "No Close Date"
            else
            titledate.text = ""
        }
        val strTitle = "Enter the 6 digit authentication passcode sent to ${AppPreferences.emailId.toString()} in order to vote on this ballot."
        ltitle.text = strTitle

        ivulogo.load(AppPreferences.logoUrl.toString())
        //otpTextView = otp_view
//        otpTextView?.requestFocusOTP()
//        otpTextView?.otpListener = object : OTPListener {
//
//            override fun onInteractionListener() {
//                otpBtn.isEnabled = false
//                otpBtn.isClickable = false
//                otpBtn.backgroundTintList = ColorStateList.valueOf(
//                    ResourcesCompat.getColor(
//                        resources,
//                        R.color.button_bg_otp_disable, theme
//                    )
//                )
//                //otpBtn.setBackgroundColor(ContextCompat.getColor(this@BallotOtpActivity, R.color.button_bg_otp_disable))
//            }
//
//            override fun onOTPComplete(otp: String) {
//                otpBtn.isEnabled = true
//                otpBtn.isClickable = true
//                otpBtn.backgroundTintList = ColorStateList.valueOf(
//                    ResourcesCompat.getColor(
//                        resources,
//                        R.color.button_bg, theme
//                    )
//                )
//                strOtp = otp
////                Log.d("responsestrOtp", strOtp);
////                Log.d("responsevID", vID);
//            }
//        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            scroll.post {
                scroll.fullScroll(View.FOCUS_DOWN)
            }
        }
        else{
            Handler(Looper.getMainLooper()).postDelayed({
                /* Create an Intent that will start the Menu-Activity. */
                //scroll.scrollTo(0, otpBtn.bottom - 50)
                scroll.post {
//                scroll.fullScroll(View.FOCUS_DOWN)
                    scroll.scrollTo(0, otpBtn.getY().toInt())
                }

            }, 1000)
        }
        otp_view.isCursorVisible = false
        otp_view.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                otp_view.clearError()

                if(s.toString().length == 6){
                    otpBtn.isEnabled = true
                otpBtn.isClickable = true
                otpBtn.backgroundTintList = ColorStateList.valueOf(
                    ResourcesCompat.getColor(
                        resources,
                        R.color.button_bg, theme
                    )
                )
                strOtp = s.toString()
                }
                else{
                    otpBtn.isEnabled = false
                    otpBtn.isClickable = false
                    otpBtn.backgroundTintList = ColorStateList.valueOf(
                        ResourcesCompat.getColor(
                            resources,
                            R.color.button_bg_otp_disable, theme
                        )
                    )
                }
            }
        })

        otpBtn.setOnClickListener {
            val dialog = ProgressDialogUtil.setProgressDialog(this, "Processing...")
            dialog.show()

            val formBody: RequestBody = FormBody.Builder()
                .add("VoteID", vID)
                .add("Address", vAddress)
                .add("VotingCode", strOtp)
                .build()
//            println(formBody.toString())
//            println(vAddress)
            val call: Call<JsonObject> = apiServiceInterface.enerateVT(
                AppPreferences.authToken.toString(),
                formBody
            )
            call.enqueue(object : Callback<JsonObject> {
                override fun onResponse(
                    call: Call<JsonObject>,
                    response: Response<JsonObject>
                ) {
                    dialog.cancel()
                    if (response.isSuccessful) {

                        ltvrperror.visibility = View.GONE
                        Log.d("response", response.body().toString());
                        val jsonObject: JsonObject? = response.body()
//                        Log.d("jsonObjectM", jsonObject?.get("Flag").toString())
//                        Log.d("jsonObjectMM", jsonObject?.get("Message").toString())
//                        var message: String = jsonObject?.get("Message").toString()
                        var flag = jsonObject?.get("Flag").toString()
                        var flagCheck: Boolean? = flag.toBoolean() ?: false;
                        if (flagCheck == true) {

                            val dObject = jsonObject?.getAsJsonObject("Data")

                            Log.d(
                                "jsonObject7",
                                dObject?.get("VotingToken")?.asString!!
                            )
                            val intent = null;
                                if(vTypeID == "1") {
                                val intentV = Intent(
                                    this@BallotOtpActivity,
                                    StartVotingActivity::class.java
                                )
                                    intentV.putExtra("vName", vName)
                                    intentV.putExtra("vToken", dObject?.get("VotingToken")?.asString!!)
                                    intentV.putExtra("vDate", vDate)
                                    intentV.putExtra("vID", vID)
                                    intentV.putExtra("vTypeID", vTypeID)
                                    intentV.putExtra("vAddress", vAddress)
                                    intentV.putExtra("vUnit", vUnit)
                                    startActivity(intentV)
                                    finish()
                            }
                            else{
                                val intentS = Intent(
                                    this@BallotOtpActivity,
                                    StartSurveyActivity::class.java
                                )
                                    intentS.putExtra("vName", vName)
                                    intentS.putExtra("vToken", dObject?.get("VotingToken")?.asString!!)
                                    intentS.putExtra("vDate", vDate)
                                    intentS.putExtra("vID", vID)
                                    intentS.putExtra("vTypeID", vTypeID)
                                    intentS.putExtra("vAddress", vAddress)
                                    intentS.putExtra("vUnit", vUnit)
                                    startActivity(intentS)
                                    finish()
                            }

                        } else {
                            otp_view.setError()
                            ltvrperror.visibility = View.VISIBLE
                            var emsg = jsonObject?.get("Message").toString()
                            ltvrperror.text = emsg.replace("\"", "")
                            Log.d("jsonObject", response.message())
//                            Toast.makeText(
//                                this@BallotOtpActivity,
//                                message,
//                                Toast.LENGTH_LONG
//                            ).show()
                        }
                    } else {
                        otp_view.setError()
                        ltvrperror.visibility = View.VISIBLE
                        Log.d("jsonObject", response.message())
//                        Toast.makeText(
//                            this@BallotOtpActivity,
//                            "Error! Please try again!",
//                            Toast.LENGTH_LONG
//                        ).show()
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    dialog.cancel()
                    Log.d("jsonObject", t.message.toString())
                    Toast.makeText(this@BallotOtpActivity, t.message, Toast.LENGTH_LONG).show()
                }
            })
        }


    }

}

