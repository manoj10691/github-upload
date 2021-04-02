package com.vportals.app.survey

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
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
import com.vportals.app.voting.VoteCloseActivity
import com.vportals.app.voting.VotingOptionAdapter
import com.vportals.app.voting.VotingSuccessActivity
import kotlinx.android.synthetic.main.activity_reset.*
import kotlinx.android.synthetic.main.activity_survey_options.*
import kotlinx.android.synthetic.main.activity_survey_options.ivulogo
import kotlinx.android.synthetic.main.activity_survey_options.titledate
import kotlinx.android.synthetic.main.activity_survey_options.titlemorning
import kotlinx.android.synthetic.main.activity_vote_options.*
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


class SurveyOptionsActivity : AppCompatActivity(), CellCheckClickListener {
// , CellVClickListener, CellVBClickListener
    private lateinit var apiServiceInterface: ApiServiceInterface
    private var voteList: VOptionResponse? = null
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var mAdapter: SurveyOptionAdapter
    lateinit var viewPagerView: ViewPager2
    private var vToken = ""
    private var vName = ""
    private var vDate = ""
    private var vID = ""
    private var vTypeID = ""
    private var vodata: VOptionGroup? = null
//    private var vData = ArrayList<VOptionGroup>()
    var currentPosition = 0
    var cTopNPosition = 0
    var cTopPosition = 1
    var checkF = 0
    var checkCheck = true

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_survey_options)
        apiServiceInterface = ApiUtils.getApiService(this)


        val intent = intent
        vName = intent.getStringExtra("vName").toString()
        vToken = intent.getStringExtra("vToken").toString()
        vDate = intent.getStringExtra("vDate").toString()
        vID = intent.getStringExtra("vID").toString()
        vTypeID = intent.getStringExtra("vTypeID").toString()
        println(vTypeID)
        titlemorning.text = vName

        viewPagerView = viewPager
//        recyclerView.layoutParams = LinearLayoutManager(this@SurveyOptionsActivity)
        viewPagerView.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        //

        if(!vDate.isNullOrEmpty()){
            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            val formatter = SimpleDateFormat("MM/dd/yyyy")

            val output =  formatter.format(parser.parse(vDate))

            titledate.text = "Closes: ".plus(output)
        }
        else {
            titledate.text = ""
        }

        next.setOnClickListener {

            var chk: Boolean = false
            //checkF = 0

            for (citem in voteList!!.Data!!.voteOptionGroups) {
//                println("test - 1")
                if(cTopPosition == 1){
//                    println("MinSelectionCount 1")
//                    println(citem.MinSelectionCount)
                if (citem.GroupType == "csmc" && citem.MaxSelectionCount == 1) {
                    if (citem.MinSelectionCount != 0) {
                        for (item in citem.voteOptions) {
                            if (item.HasSelected) {
                                chk = true
                            }
                        }
                    }
                    else{
                        chk = true
                        if(checkF == 0)
                        checkF = 1
                    }
                }
                }
                else if(cTopPosition == 2) {
//                    println("MinSelectionCount 2")
//                    println(citem.MinSelectionCount)
                    if (citem.GroupType == "csmc" && citem.MaxSelectionCount != 1) {
                        if (citem.MinSelectionCount != 0) {
                            for (item in citem.voteOptions) {
                                if (item.HasSelected) {
                                    chk = true
                                }
                            }
                        }
                        else{
                            chk = true
                            if(checkF == 1)
                                checkF = 2
                        }
                    }
                }
                else if(cTopPosition == 3) {
//                    println("MinSelectionCount 3")
//                    println(citem.MinSelectionCount)
                    if (citem.GroupType == "csmcrc") {
                        if (citem.MinSelectionCount != 0) {
                            if (citem.voteOptions[0].Rating != 0) {
                                chk = true
                            }
                        }
                        else{
                            chk = true
                            if(checkF == 2)
                                checkF = 3
                        }
                    }
                }
            }

                if (cTopPosition < voteList!!.Data!!.voteOptionGroups.size) {
                    if(chk) {
                        previous.backgroundTintList = ColorStateList.valueOf(
                            ResourcesCompat.getColor(
                                resources,
                                R.color.button_bg, theme
                            )
                        )
                        cTopNPosition++
                        cTopPosition++
                        viewPagerView.currentItem = cTopNPosition
                        statusViewScroller.statusView.run {
                            currentCount = cTopPosition
                        }
                        if (cTopPosition == voteList!!.Data!!.voteOptionGroups.size) {

                            next.text = "Submit"

                        }

                    }
                    else {
//                        print("cTopPosition")
//                        print(cTopPosition)
                        Toast.makeText(
                            this@SurveyOptionsActivity,
                            "Select minimum 1 option",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    if(chk) {
//                        println(checkF)
//                        println(vTypeID)
//                        println(checkCheck)


                    val prntObjArr = JsonArray()


                    for (citem in voteList!!.Data!!.voteOptionGroups) {
                        if (citem.GroupType == "csmc" && citem.MaxSelectionCount == 1) {
                            val vogIdObj1 = JsonObject()
                            val jsSoArr1 = JsonArray()
                            val jsSo1 = JsonObject()
                            for (item in citem.voteOptions) {
                                if (item.HasSelected) {
                                    jsSo1.addProperty("VoteOptionID", item.VoteOptionID.toString())
                                    jsSo1.addProperty("VOID", item.VoteOptionID.toString())
                                    jsSo1.addProperty("Rating", 0)
                                    jsSo1.addProperty("Comment", "")
                                    jsSoArr1.add(jsSo1)
                                    checkCheck = false
                                }
                            }
                            vogIdObj1.addProperty(
                                "VOGID",
                                citem.VoteOptionGroupID.toString()
                            )
                            vogIdObj1.add("SelectedOptions", jsSoArr1)
                            prntObjArr.add(vogIdObj1)
                        }

                        if (citem.GroupType == "csmc" && citem.MaxSelectionCount != 1) {
                            val vogIdObj2 = JsonObject()
                            val jsSoArr2 = JsonArray()
                            val jsSo2 = JsonObject()
                            for (item in citem.voteOptions) {
                                if (item.HasSelected) {
                                    jsSo2.addProperty("VoteOptionID", item.VoteOptionID.toString())
                                    jsSo2.addProperty("VOID", item.VoteOptionID.toString())
                                    jsSo2.addProperty("Rating", 0)
                                    jsSo2.addProperty("Comment", "")
                                    jsSoArr2.add(jsSo2)
                                    checkCheck = false
                                }
                            }
                            vogIdObj2.addProperty(
                                "VOGID",
                                citem.VoteOptionGroupID.toString()
                            )
                            vogIdObj2.add("SelectedOptions", jsSoArr2)
                            prntObjArr.add(vogIdObj2)
                        }
                        if (citem.GroupType == "csmcrc") {
                            val vogIdObj3 = JsonObject()
                            val jsSoArr2 = JsonArray()
                            val jsSo2 = JsonObject()
                            val jsSo3 = JsonObject()
                            jsSo2.addProperty(
                                "VoteOptionID",
                                citem.voteOptions[0].VoteOptionID.toString()
                            )
                            jsSo2.addProperty("VOID", citem.voteOptions[0].VoteOptionID.toString())
                            jsSo2.addProperty("Rating", citem.voteOptions[0].Rating)
                            jsSo2.addProperty("Comment", "")
                            jsSoArr2.add(jsSo2)

                            if (citem.voteOptions[0].Rating != 0)
                                checkCheck = false

                            jsSo3.addProperty(
                                "VoteOptionID",
                                citem.voteOptions[1].VoteOptionID.toString()
                            )
                            jsSo3.addProperty("VOID", citem.voteOptions[1].VoteOptionID.toString())
                            jsSo3.addProperty("Rating", 0)
                            if (!citem.voteOptions[1].Comment.isNullOrBlank())
                                jsSo3.addProperty(
                                    "Comment",
                                    citem.voteOptions[1].Comment.toString()
                                )
                            else
                                jsSo3.addProperty("Comment", "")

                            jsSoArr2.add(jsSo3)
                            vogIdObj3.addProperty(
                                "VOGID",
                                citem.VoteOptionGroupID.toString()
                            )
                            vogIdObj3.add("SelectedOptions", jsSoArr2)
                            prntObjArr.add(vogIdObj3)
                        }
                    }
                        if(checkF == 3 && vTypeID == "9" && checkCheck) {

                            next.isEnabled = false
                            next.backgroundTintList = ColorStateList.valueOf(ResourcesCompat.getColor(resources,
                                R.color.button_bg_disable, theme))
                        }
                        else {
                            println("V1")

 val dialog = ProgressDialogUtil.setProgressDialog(
                        this@SurveyOptionsActivity,
                        "Processing..."
                    )
                    dialog.show()

                    //println(prntObjArr.toString())
                    val call = apiServiceInterface!!.submitVote(
                        AppPreferences.authToken.toString(),
                        vToken,
                        prntObjArr.toString()
                    )

                    call.enqueue(object : Callback<JsonObject> {
                        override fun onResponse(
                            call: Call<JsonObject>,
                            response: Response<JsonObject>
                        ) {
                            dialog.cancel()

                            Log.d("jsonObjectM", response.body().toString())
                            if (response.isSuccessful) {
                                val jsonObject: JsonObject? = response.body()
                                var flag = jsonObject?.get("Flag").toString();
                                var flagCheck: Boolean? = flag.toBoolean() ?: false;
                                if (flagCheck == true) {

                                    val dObject = jsonObject?.getAsJsonObject("Data")
                                    println("dObject")
                                    println(dObject?.get("Address").toString())
                                    var vUnit = dObject?.get("Address").toString()
                                    if (vUnit == null)
                                        vUnit = ""

                                    val intentS = Intent(
                                        this@SurveyOptionsActivity,
                                        FinishSurveyActivity::class.java
                                    )
                                    intentS.putExtra("vName", vName)
                                    intentS.putExtra("vDate", vDate)
                                    intentS.putExtra("vID", vID)
                                    intentS.putExtra("vUnit", vUnit)
                                    startActivity(intentS)
                                    finish()
                                } else {
                                    var message: String = jsonObject?.get("Message").toString()
                                    Log.d("jsonObject", response.message())
                                    Toast.makeText(
                                        this@SurveyOptionsActivity,
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
                        }
                    })
                        }
                    }
                    else {
//                        print("cTopPosition")
//                        print(cTopPosition)
                        Toast.makeText(
                            this@SurveyOptionsActivity,
                            "Select minimum 1 option",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

        }

        previous.setOnClickListener {
            if(cTopPosition != 1) {
                next.isEnabled = true
                next.backgroundTintList = ColorStateList.valueOf(ResourcesCompat.getColor(resources,
                                R.color.button_bg, theme))
                next.text = "Next"
//                checkF--
                cTopNPosition--
                cTopPosition--
                viewPagerView.currentItem = cTopNPosition
                statusViewScroller.statusView.run {
                    currentCount = cTopPosition
                }
                if(cTopPosition == 1){
                    println("next else")
                    println(cTopPosition)
//                    println(checkF)
                    previous.backgroundTintList = ColorStateList.valueOf(
                        ResourcesCompat.getColor(resources,
                            R.color.button_bg_disable, theme))
                }
            }

        }

        ivulogo.setOnClickListener {

            val intent = Intent(
                this@SurveyOptionsActivity,
                VoteCloseActivity::class.java
            )
            intent.putExtra("vName", vName)
            intent.putExtra("vDate", vDate)
            intent.putExtra("vSurvey", "yes")
            startActivity(intent)
            finish()
        }

        getVotingData()
    }
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCellCheckClickListener() {

        next.isEnabled = true
        next.backgroundTintList = ColorStateList.valueOf(ResourcesCompat.getColor(resources,
            R.color.button_bg, theme))

    }
    private fun getVotingData() {
        val dialog = ProgressDialogUtil.setProgressDialog(this@SurveyOptionsActivity, "Processing...")
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
                        mAdapter = SurveyOptionAdapter(
                            voteList!!.Data!!.voteOptionGroups,
                            this@SurveyOptionsActivity,
                        )
                        viewPagerView.adapter = mAdapter
                        viewPagerView.isUserInputEnabled = false
                        //statusViewScroller.stepCount = vData.size
                        //statusViewScroller.scrollToStep(vData.size)
                        statusViewScroller.statusView.run {
                            stepCount = voteList!!.Data!!.voteOptionGroups.size
                            currentCount = cTopPosition
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
}