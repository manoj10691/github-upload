package com.vportals.app.voting

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
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
import kotlinx.android.synthetic.main.activity_vote_close.*
import java.text.SimpleDateFormat
import java.util.*


class VoteCloseActivity : AppCompatActivity() {

    private var vName = ""
    private var vDate = ""
    private var vSurvey = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vote_close)


        val intent = intent
        vName = intent.getStringExtra("vName").toString()
        vDate = intent.getStringExtra("vDate").toString()
        vSurvey = intent.getStringExtra("vSurvey").toString()
        titlemorning.text = vName

        ivulogo.load(AppPreferences.logoUrl.toString())

        if(!vDate.isNullOrEmpty()){
            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            val formatter = SimpleDateFormat("MM/dd/yyyy")

            val output =  formatter.format(parser.parse(vDate))

            titledate.text = "Closes: ".plus(output)
        }
        else {
            if(vSurvey == "yes")
                titledate.text = ""
            else
            titledate.text = "No Close Date"
        }

        btnExit.setOnClickListener {

            finish()

        }

    }

}