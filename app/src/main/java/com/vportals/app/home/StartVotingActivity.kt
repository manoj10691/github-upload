package com.vportals.app.home


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
import com.vportals.app.voting.VoteOptionsActivity
import kotlinx.android.synthetic.main.activity_start_voting.*
import kotlinx.android.synthetic.main.activity_start_voting.ivulogo
import kotlinx.android.synthetic.main.activity_start_voting.titledate
import kotlinx.android.synthetic.main.activity_start_voting.titlemorning
import kotlinx.android.synthetic.main.fragment_home.*
import okhttp3.FormBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class StartVotingActivity : AppCompatActivity() {

    private lateinit var apiServiceInterface: ApiServiceInterface

    private var vToken = ""
    private var vName = ""
    private var vDate = ""
    private var vID = ""
    private var vTypeID = ""
    private var vAddress = ""
    private var vUnit = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_voting)
        apiServiceInterface = ApiUtils.getApiService(this)


        getDateTime()

        val intent = intent
        vName = intent.getStringExtra("vName").toString()
        vToken = intent.getStringExtra("vToken").toString()
        vDate = intent.getStringExtra("vDate").toString()
        vID = intent.getStringExtra("vID").toString()
        vTypeID = intent.getStringExtra("vTypeID").toString()
        vAddress = intent.getStringExtra("vAddress").toString()
        vUnit = intent.getStringExtra("vUnit").toString()
//        println(vToken)
        titlemorning.text = vName

        if(!vDate.isNullOrEmpty()){
            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            val formatter = SimpleDateFormat("MM/dd/yyyy")

            val output =  formatter.format(parser.parse(vDate))

            titledate.text = "Closes: ".plus(output)
        }
        else {
            titledate.text = "No Close Date"
        }

        ivulogo.load(AppPreferences.logoUrl.toString())


        startBtn.setOnClickListener {

            val intent = Intent(
                this@StartVotingActivity,
                VoteOptionsActivity::class.java
            )
            intent.putExtra("vName", vName)
            intent.putExtra("vToken", vToken)
            intent.putExtra("vDate", vDate)
            intent.putExtra("vID", vID)
            intent.putExtra("vTypeID", vTypeID)
            startActivity(intent)
            finish()
        }
    }
    private fun getDayOfMonthSuffix(n: Int): String? {

        return if (n in 11..13) {
            "th"
        } else when (n % 10) {
            1 -> "st"
            2 -> "nd"
            3 -> "rd"
            else -> "th"
        }
    }
    private fun getDateTime() {
        // Friday, July 31, 2020
        val dayDateFormat: DateFormat = SimpleDateFormat("d")
        val monthDateFormat: DateFormat = SimpleDateFormat("MMMM")
        val yearDateFormat: DateFormat = SimpleDateFormat("yyyy")
        val date = Date()
        val today = dayDateFormat.format(date).toInt()
        val ndate = today.toString()+getDayOfMonthSuffix(today)
        val mday = monthDateFormat.format(date)
        val yday = yearDateFormat.format(date)
        val finalDate = "Dated this $ndate day of $mday, $yday"
        tvbtnstv.text = finalDate
          //  return dateFormat.format(date)
    }
}

