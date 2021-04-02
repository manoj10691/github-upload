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
import com.vportals.app.service.ApiServiceInterface
import com.vportals.app.service.ApiUtils
import com.vportals.app.service.AppPreferences
import com.vportals.app.service.ProgressDialogUtil
import com.vportals.app.voting.VoteCloseActivity
import com.vportals.app.voting.VoteOptionsActivity
import kotlinx.android.synthetic.main.activity_finish_survey.*
import okhttp3.FormBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class FinishSurveyActivity : AppCompatActivity() {

    private lateinit var apiServiceInterface: ApiServiceInterface

    private var vToken = ""
    private var vName = ""
    private var vDate = ""
    private var vID = ""
    private var vTypeID = ""
    private var vUnit = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finish_survey)
        apiServiceInterface = ApiUtils.getApiService(this)

        val intent = intent
        vName = intent.getStringExtra("vName").toString()
        vToken = intent.getStringExtra("vToken").toString()
        vDate = intent.getStringExtra("vDate").toString()
        vID = intent.getStringExtra("vID").toString()
        vTypeID = intent.getStringExtra("vTypeID").toString()
        vUnit = intent.getStringExtra("vUnit").toString()
//        println(vToken)
        btitlemorning.text = AppPreferences.fullName.toString()
        val ys = vUnit.replace("\"", "")
        val vn = vName.replace("\"", "")
        //ltitle.text = "You have completed  the survey from "+AppPreferences.fullName.toString()+".  You will receive an email receipt shortly."
        ltitle.text = "You have completed\n the survey $vn for $ys \nYou will receive an \nemail receipt shortly."
//        if(!vDate.isNullOrEmpty()){
//            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
//            val formatter = SimpleDateFormat("MM/dd/yyyy")
//
//            val output =  formatter.format(parser.parse(vDate))
//
//            btitledate.text = "Closes: ".plus(output)
//        }
//        else {
//            btitledate.text = ""
//        }

        ivulogo.load(AppPreferences.logoUrl.toString())


        startBtn.setOnClickListener {

//            val intent = Intent(
//                this@FinishSurveyActivity,
//                SurveyOptionsActivity::class.java
//            )
//            intent.putExtra("vName", vName)
//            intent.putExtra("vToken", vToken)
//            intent.putExtra("vDate", vDate)
//            intent.putExtra("vID", vID)
//            intent.putExtra("vTypeID", vTypeID)
//            startActivity(intent)
            finish()
        }
//        ivulogo2.setOnClickListener {
//
//            val intent = Intent(
//                this@FinishSurveyActivity,
//                VoteCloseActivity::class.java
//            )
//            intent.putExtra("vName", vName)
//            intent.putExtra("vDate", vDate)
//            intent.putExtra("vSurvey", "yes")
//            startActivity(intent)
//            finish()
//        }
    }

}

