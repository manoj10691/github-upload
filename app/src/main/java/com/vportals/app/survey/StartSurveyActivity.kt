package com.vportals.app.survey


import android.content.Intent
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import coil.load
import com.google.gson.JsonObject
import com.vportals.app.R
import com.vportals.app.model.VOptionResponse
import com.vportals.app.service.ApiServiceInterface
import com.vportals.app.service.ApiUtils
import com.vportals.app.service.AppPreferences
import com.vportals.app.service.ProgressDialogUtil
import com.vportals.app.voting.VoteCloseActivity
import com.vportals.app.voting.VoteOptionsActivity
import kotlinx.android.synthetic.main.activity_start_survey.*
import kotlinx.android.synthetic.main.activity_start_survey.ltitle
import kotlinx.android.synthetic.main.activity_start_survey.ivulogo
import kotlinx.android.synthetic.main.activity_start_survey.titledate
import kotlinx.android.synthetic.main.activity_start_survey.titlemorning
import kotlinx.android.synthetic.main.fragment_home.*
import okhttp3.FormBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class StartSurveyActivity : AppCompatActivity() {

    private lateinit var apiServiceInterface: ApiServiceInterface
    private var voteList: VOptionResponse? = null
    private var vToken = ""
    private var vName = ""
    private var vDate = ""
    private var vID = ""
    private var vTypeID = ""
    private var vAddress = ""
    private var vUnit = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_survey)
        apiServiceInterface = ApiUtils.getApiService(this)

        val intent = intent
        vName = intent.getStringExtra("vName").toString()
        vToken = intent.getStringExtra("vToken").toString()
        vDate = intent.getStringExtra("vDate").toString()
        vID = intent.getStringExtra("vID").toString()
        vTypeID = intent.getStringExtra("vTypeID").toString()
        vAddress = intent.getStringExtra("vAddress").toString()
        vUnit = intent.getStringExtra("vUnit").toString()
//        println(vToken)
        btitlemorning.text = vName

        if(!vDate.isNullOrEmpty()){
            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            val formatter = SimpleDateFormat("MM/dd/yyyy")

            val output =  formatter.format(parser.parse(vDate))

            btitledate.text = "Closes: ".plus(output)
        }
        else {
            btitledate.text = ""
        }

        ivulogo.load(AppPreferences.logoUrl.toString())

        titlemorning.text = getGreetingMessage()
        titledate.text = getDateTime()

        startBtn.setOnClickListener {

            val intent = Intent(
                this@StartSurveyActivity,
                SurveyOptionsActivity::class.java
            )
            intent.putExtra("vName", vName)
            intent.putExtra("vToken", vToken)
            intent.putExtra("vDate", vDate)
            intent.putExtra("vID", vID)
            intent.putExtra("vTypeID", vTypeID)
            startActivity(intent)
            finish()
        }
        ivulogo2.setOnClickListener {

            val intent = Intent(
                this@StartSurveyActivity,
                VoteCloseActivity::class.java
            )
            intent.putExtra("vName", vName)
            intent.putExtra("vDate", vDate)
            intent.putExtra("vSurvey", "yes")
            startActivity(intent)
            finish()
        }

        //getSVotingData()
    }
/*
    private fun getSVotingData() {
        val dialog = ProgressDialogUtil.setProgressDialog(this@StartSurveyActivity, "Processing...")
        dialog.show()
        val formBody: RequestBody = FormBody.Builder()
            .add("VoteID", vID)
            .build()

        val call = apiServiceInterface!!.getVotingOption(
            AppPreferences.authToken.toString(),
            vToken,
            formBody
        )

        call.enqueue(object : Callback<VOptionResponse> {
            override fun onResponse(
                call: Call<VOptionResponse>,
                response: Response<VOptionResponse>
            ) {
                dialog.cancel()
                Log.d("jsonObject2", response.toString())
                if (response.isSuccessful) {
                    // vData = arrayListOf<VOptionGroup>()
                    voteList = response.body()
//                    Log.d("jsonObject1", voteList?.Message.toString())
//                    Log.d("jsonObject2", voteList?.Data.toString())

                    //vData.addAll(voteList!!.Data!!.voteOptionGroups)
                    if (voteList!!.Data!!.voteOptionGroups.isNotEmpty()) {
                        // recyclerView.visibility = View.VISIBLE
                    if(voteList!!.Data!!.voteOptionGroups[0].GroupType == "csbnr"){
                        ltitle.text = voteList!!.Data!!.voteOptionGroups[0].GroupName
                    }

                    }


                } else {
                    Log.d("jsonObject", response.message())
                }

            }

            override fun onFailure(call: Call<VOptionResponse>, t: Throwable) {
                dialog.cancel()
                Log.d("jsonObject", t.message.toString())
                //Toast.makeText(this@LoginActivity, t.message, Toast.LENGTH_LONG).show()
            }
        })
    }
*/
    private fun getDateTime(): String? {
        // Friday, July 31, 2020
        val dateFormat: DateFormat = SimpleDateFormat("EEEE, MMMM d, yyyy")
        val date = Date()
        return dateFormat.format(date)
    }
    private fun getGreetingMessage():String{
        val c = Calendar.getInstance()
        val timeOfDay = c.get(Calendar.HOUR_OF_DAY)
        val strFName: String? = AppPreferences.firstName;
        return when (timeOfDay) {
            in 0..11 -> "Good Morning, ".plus(strFName)
            in 12..15 -> "Good Afternoon, ".plus(strFName)
            in 16..20 -> "Good Evening, ".plus(strFName)
            in 21..23 -> "Good Night, ".plus(strFName)
            else -> {
                "Hello, ".plus(strFName)
            }
        }
    }
}

