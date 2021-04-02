package com.vportals.app.voting

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.vportals.app.R
import com.vportals.app.home.StartVotingActivity
import com.vportals.app.model.*
import com.vportals.app.service.ApiServiceInterface
import com.vportals.app.service.ApiUtils
import com.vportals.app.service.AppPreferences
import com.vportals.app.service.ProgressDialogUtil
import com.vportals.app.util.PdfViewerActivity
import kotlinx.android.synthetic.main.activity_ballot_otp.*
import kotlinx.android.synthetic.main.activity_start_voting.*
import kotlinx.android.synthetic.main.activity_vote_options.*
import kotlinx.android.synthetic.main.activity_vote_options.ivulogo
import kotlinx.android.synthetic.main.activity_vote_options.titledate
import kotlinx.android.synthetic.main.activity_vote_options.titlemorning
import kotlinx.android.synthetic.main.activity_vote_options.tvbtnstv
import okhttp3.FormBody
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class VoteOptionsActivity : AppCompatActivity() , CellVClickListener, CellVBClickListener {

    private lateinit var apiServiceInterface: ApiServiceInterface
    private var voteList: VOptionResponse? = null
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var mAdapter: VotingOptionAdapter
    lateinit var recyclerView: RecyclerView
    private var vToken = ""
    private var vName = ""
    private var vDate = ""
    private var vID = ""
    private var vodata: VOption? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vote_options)
        apiServiceInterface = ApiUtils.getApiService(this)


        val intent = intent
        vName = intent.getStringExtra("vName").toString()
        vToken = intent.getStringExtra("vToken").toString()
        vDate = intent.getStringExtra("vDate").toString()
        vID = intent.getStringExtra("vID").toString()
        println(vToken)
        titlemorning.text = vName

        getDateTime()

        if(!vDate.isNullOrEmpty()){
            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            val formatter = SimpleDateFormat("MM/dd/yyyy")

            val output =  formatter.format(parser.parse(vDate))

            titledate.text = "Closes: ".plus(output)
        }
        else {
            titledate.text = "No Close Date"
        }

        recyclerView = rvVoting
        recyclerView.layoutManager = LinearLayoutManager(this@VoteOptionsActivity)

        ivulogo.setOnClickListener {

            val intent = Intent(
                this@VoteOptionsActivity,
                VoteCloseActivity::class.java
            )
            intent.putExtra("vName", vName)
            intent.putExtra("vDate", vDate)
            intent.putExtra("vSurvey", "no")
            startActivity(intent)
            finish()

        }
        castBtn.setOnClickListener {

            val dialog = ProgressDialogUtil.setProgressDialog(
                this@VoteOptionsActivity,
                "Processing..."
            )
            dialog.show()


            val obj = JsonObject()
            val objArr = JsonArray()
            val jsSo = JsonObject()
            val jsSoArr = JsonArray()

            jsSo.addProperty("VoteOptionID", vodata?.VoteOptionID.toString())
            jsSo.addProperty("VOID", vID)
            jsSo.addProperty("Rating", 0)
            jsSo.addProperty("Comment", "")
            obj.addProperty("VOGID", "0")
            jsSoArr.add(jsSo)
            obj.add("SelectedOptions", jsSoArr)
            objArr.add(obj)
//            Log.d("jsonObject77", objArr.toString())
//
//            Log.d("jsonObject77",   AppPreferences.authToken.toString())
//            Log.d("jsonObject77",   vToken)

            val call = apiServiceInterface!!.submitVote(
                AppPreferences.authToken.toString(),
                vToken,
                objArr.toString()
            )

            call.enqueue(object : Callback<JsonObject> {
                override fun onResponse(
                    call: Call<JsonObject>,
                    response: Response<JsonObject>
                ) {
                    dialog.cancel()

//                    val jsonObject: JsonObject? = response.body()
                    Log.d("jsonObjectM", response.body().toString())
                    if (response.isSuccessful) {
                        val jsonObject: JsonObject? = response.body()
//                        Log.d("jsonObjectM", jsonObject?.get("Flag").toString())
//                        Log.d("jsonObjectMM", jsonObject?.get("Message").toString())
//                        var message: String = jsonObject?.get("Message").toString()
                        var flag = jsonObject?.get("Flag").toString();
                        var flagCheck: Boolean? = flag.toBoolean() ?: false;
                        if (flagCheck == true) {

                            val dObject = jsonObject?.getAsJsonObject("Data")
//                            println("dObject")
//                            println(dObject?.get("Address").toString())
                            var vUnit = dObject?.get("Address").toString()
                            if(vUnit == null)
                                vUnit = ""

                            val intent = Intent(
                                this@VoteOptionsActivity,
                                VotingSuccessActivity::class.java
                            )
                            intent.putExtra("vName", vName)
                            intent.putExtra("vDate", vDate)
                            intent.putExtra("vUnit", vUnit)
                            startActivity(intent)
                            finish()
                        } else {
                            var message: String = jsonObject?.get("Message").toString()
                            Log.d("jsonObject", response.message())
                            Toast.makeText(
                                this@VoteOptionsActivity,
                                message.replace("\"", ""),
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    } else {
                        Log.d("myLogs", response.toString());
                        Log.d("myLogs", response.errorBody().toString())
                        Log.d("myLogs", response.code().toString())
                        Log.d("myLogs", response.toString())
                    }

                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    dialog.cancel()
                    Log.d("jsonObject33", t.toString())
                    //Toast.makeText(this@LoginActivity, t.message, Toast.LENGTH_LONG).show()
                }
            })

        }
        getVotingData()
    }
    override fun onCellVClickListener(data: VOption) {
            tvbtnstv.visibility = View.GONE
            castBtn.visibility = View.VISIBLE
            tvbtnstvdate.visibility = View.VISIBLE
            //Toast.makeText(this@VoteOptionsActivity,data.FullName+" Coming soon!", Toast.LENGTH_SHORT).show()
            vodata = data
            //println(vodata!!.FullName)

    }
    override fun onCellVBClickListener(data: VOptionAttachments, fname: String) {

       // println(ApiUtils.basePdfUrl+data.FilePath)

            startActivity(
                PdfViewerActivity.launchPdfFromUrl(
                    this,
                    ApiUtils.baseCDNUrl + data.FilePath,                                // PDF URL in String format
                    false,
                    fname,                        // PDF Name/Title in String format
                    "",                  // If nothing specific, Put "" it will save to Downloads
                    true                    // This param is true by defualt.
                )
            )

    }
    private fun getVotingData() {
        val dialog = ProgressDialogUtil.setProgressDialog(this@VoteOptionsActivity, "Processing...")
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

                    voteList = response.body()
                    Log.d("jsonObject1", voteList?.Message.toString())
                    Log.d("jsonObject2", voteList?.Data.toString())
                    val vData = ArrayList<VOption>()
                    vData.addAll(voteList!!.Data!!.voteOptionGroups[0].voteOptions)
                    if (vData.size != 0) {
                        recyclerView.visibility = View.VISIBLE
                        mAdapter = VotingOptionAdapter(
                            vData,
                            this@VoteOptionsActivity,
                            this@VoteOptionsActivity
                        )
                        mAdapter.setHasStableIds(true)
                        recyclerView.adapter = mAdapter

                        val strC = vData.size.toString()
                        var strNum = ""
                        if (vData.size == 1)
                            strNum = "one"
                        else if (vData.size == 2)
                            strNum = "two"
                        else if (vData.size == 3)
                            strNum = "three"
                        else if (vData.size == 4)
                            strNum = "four"
                        else if (vData.size == 5)
                            strNum = "five"
                        else if (vData.size == 6)
                            strNum = "six"
                        else if (vData.size == 7)
                            strNum = "seven"
                        else
                            strNum = "zero"

                        titlebtm.text =
                            "The $strNum ($strC) candidates for the Board of Directors\n" +
                                    "are not in alphabetical order."
                    }
//
//                    Log.d("jsonObject3", vData.size.toString())

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
        tvbtnstvdate.text = finalDate
        //  return dateFormat.format(date)
    }
}