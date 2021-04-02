package com.vportals.app.home

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import coil.load
import com.google.gson.JsonObject
import com.vportals.app.R
import com.vportals.app.model.Ballot
import com.vportals.app.model.BallotResponse
import com.vportals.app.service.ApiServiceInterface
import com.vportals.app.service.ApiUtils
import com.vportals.app.service.AppPreferences
import com.vportals.app.service.ProgressDialogUtil
import com.vportals.app.survey.StartSurveyActivity
import com.vportals.app.survey.SurveyOptionsActivity
import kotlinx.android.synthetic.main.activity_ballot_otp.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.ltvrperror
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.ivulogo
import kotlinx.android.synthetic.main.fragment_home.titledate
import kotlinx.android.synthetic.main.fragment_home.titlemorning
import kotlinx.android.synthetic.main.voting_list_row.view.*
import okhttp3.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentHome.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentHome : Fragment() , CellClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var apiServiceInterface: ApiServiceInterface? = null
    private var voteList: BallotResponse? = null
    private lateinit var linearLayoutManager:LinearLayoutManager
    private lateinit var mAdapter: VotingAdapter
    lateinit var recyclerView: RecyclerView
    lateinit var recyclerViewNoB: ScrollView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        apiServiceInterface = ApiUtils.getApiService(requireContext())
        linearLayoutManager = LinearLayoutManager(requireContext())

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        titlemorning.text = getGreetingMessage()
        titledate.text = getDateTime()

        recyclerView = rvVoting
        recyclerViewNoB = rvNoBallet
        recyclerView.layoutManager = LinearLayoutManager(activity)
        getVotingData()
        swipeContainer.setOnRefreshListener {
            getVotingDataRefresh()
        }
        fetchAccount()
        ivulogo.load(AppPreferences.logoUrl.toString())

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
//        val view = inflater.inflate(R.layout.fragment_home, container, false)
//        view.titlemorning.text = getGreetingMessage()
//        return view
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentHome.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FragmentHome().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
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
    override fun onCellClickListener(data: Ballot) {
       // Toast.makeText(requireContext(),"Cell clicked-"+data.VoteText, Toast.LENGTH_SHORT).show()
//        if(data.VoteTypeID == 1){
//            if(!data.MeetingDateTime.isNullOrEmpty()) {

//                val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
//                val formatter = SimpleDateFormat("MM/dd/yyyy")

//                val output =  formatter.format(parser.parse(data.MeetingDateTime))
//                //println(output)
//                val now = formatter.format(Date())

//                val firstDate: Date = formatter.parse(output)
//                val secondDate: Date = formatter.parse(now)

//                val diffInMillies = abs(secondDate.time - firstDate.time)
//                val diff: Long = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS)

//                if(diff > 0){
//                    if (firstDate.after(secondDate)) {
                        if(!data.HasVoted) {
                            if(data.IsRequireVotingCode){
                            val intent = Intent(requireContext(), BallotOtpActivity::class.java)
                            //val intent = Intent(requireContext(), SurveyOptionsActivity::class.java)
                            intent.putExtra("vName", data.VoteText)
                            intent.putExtra("votId", data.VoteID.toString())
                            intent.putExtra("vTypeID", data.VoteTypeID.toString())
                            intent.putExtra("vAddress", data.Address)
                            intent.putExtra("vUnit", data.UnitNumber)
                            if (!data.MeetingDateTime.isNullOrEmpty()) {
                                intent.putExtra("vDate", data.MeetingDateTime)
                            } else {
                                intent.putExtra("vDate", "")
                            }
                            startActivity(intent)
                        }
                        else {

                                val dialog = ProgressDialogUtil.setProgressDialog(requireContext(), "Processing...")
                                dialog.show()

                                val formBody: RequestBody = FormBody.Builder()
                                    .add("VoteID", data.VoteID.toString())
                                    .add("Address", data.Address)
                                    .add("VotingCode", "")
                                    .build()

                                val call: Call<JsonObject> =  apiServiceInterface!!.enerateVT(
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

                                            Log.d("response", response.body().toString());
                                            val jsonObject: JsonObject? = response.body()
                                            var flag = jsonObject?.get("Flag").toString()
                                            var flagCheck: Boolean? = flag.toBoolean() ?: false;
                                            if (flagCheck == true) {

                                                val dObject = jsonObject?.getAsJsonObject("Data")

                                                if(data.VoteTypeID.equals("1")) {
                                                    val intentV = Intent(
                                                        requireContext(),
                                                        StartVotingActivity::class.java
                                                    )
                                                    intentV.putExtra("vName", data.VoteText)
                                                    intentV.putExtra("vToken", dObject?.get("VotingToken")?.asString!!)
                                                    if (!data.MeetingDateTime.isNullOrEmpty()) {
                                                        intentV.putExtra("vDate", data.MeetingDateTime)
                                                    } else {
                                                        intentV.putExtra("vDate", "")
                                                    }
                                                    intentV.putExtra("vID", data.VoteID.toString())
                                                    intentV.putExtra("vTypeID", data.VoteTypeID.toString())
                                                    intentV.putExtra("vAddress", data.Address)
                                                    intentV.putExtra("vUnit", data.UnitNumber)
                                                    startActivity(intentV)
                                                }
                                                else{
                                                    val intentS = Intent(
                                                        requireContext(),
                                                        StartSurveyActivity::class.java
                                                    )
                                                    intentS.putExtra("vName", data.VoteText)
                                                    intentS.putExtra("vToken", dObject?.get("VotingToken")?.asString!!)
                                                    if (!data.MeetingDateTime.isNullOrEmpty()) {
                                                        intentS.putExtra("vDate", data.MeetingDateTime)
                                                    } else {
                                                        intentS.putExtra("vDate", "")
                                                    }
                                                    intentS.putExtra("vID", data.VoteID.toString())
                                                    intentS.putExtra("vTypeID", data.VoteTypeID.toString())
                                                    intentS.putExtra("vAddress", data.Address)
                                                    intentS.putExtra("vUnit", data.UnitNumber)
                                                    startActivity(intentS)
                                                }

                                            } else {
                                                Toast.makeText(requireContext(), response.message(), Toast.LENGTH_LONG).show()
                                                Log.d("jsonObject", response.message())
                                            }
                                        } else {
                                            Log.d("jsonObject", response.message())
                                        }
                                    }

                                    override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                                        dialog.cancel()
                                        Log.d("jsonObject", t.message.toString())
                                        Toast.makeText(requireContext(), t.message, Toast.LENGTH_LONG).show()
                                    }
                                })

                        }
                        }
//                    }
//                }


//            }
//            else{
//                Toast.makeText(requireContext(),data.VoteText+" No Close Date!", Toast.LENGTH_SHORT).show()
//            }
        //}
//        else {
//            Toast.makeText(requireContext(),data.VoteText+" Coming soon!", Toast.LENGTH_SHORT).show()
//        }
    }
    private fun getDateTime(): String? {
        // Friday, July 31, 2020
        val dateFormat: DateFormat = SimpleDateFormat("EEEE, MMMM d, yyyy")
        val date = Date()
        return dateFormat.format(date)
    }

    private fun getVotingData() {
        val dialog = ProgressDialogUtil.setProgressDialog(requireContext(), "Processing...")
        dialog.show()

        val call = apiServiceInterface!!.getVotingData(AppPreferences.authToken.toString())

        call.enqueue(object : Callback<BallotResponse> {
            override fun onResponse(
                call: Call<BallotResponse>,
                response: Response<BallotResponse>
            ) {
                dialog.cancel()
                if (response.isSuccessful) {
                    voteList = response.body()
                    Log.d("jsonObject1", voteList?.Message.toString())
                    Log.d("jsonObject2", voteList?.Data.toString())
                    val vData = ArrayList<Ballot>()
                    vData.addAll(voteList!!.Data)
                    if (vData.size != 0) {
                        recyclerView.visibility = View.VISIBLE
                        recyclerViewNoB.visibility = View.GONE
                        mAdapter = VotingAdapter(vData, this@FragmentHome)
                        recyclerView.adapter = mAdapter
                    } else {
                        recyclerView.visibility = View.GONE
                        recyclerViewNoB.visibility = View.VISIBLE
                    }

                } else {
                    Log.d("jsonObject", response.message())
                }

            }

            override fun onFailure(call: Call<BallotResponse>, t: Throwable) {
                dialog.cancel()
                Log.d("jsonObject", t.message.toString())
                //Toast.makeText(this@LoginActivity, t.message, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun getVotingDataRefresh() {

        val call = apiServiceInterface!!.getVotingData(AppPreferences.authToken.toString())

        call.enqueue(object : Callback<BallotResponse> {
            override fun onResponse(
                call: Call<BallotResponse>,
                response: Response<BallotResponse>
            ) {
                swipeContainer.isRefreshing = false
                if (response.isSuccessful) {
                    voteList = response.body()
                    Log.d("jsonObject1", voteList?.Message.toString())
                    Log.d("jsonObject2", voteList?.Data.toString())
                    val vData = ArrayList<Ballot>()
                    vData.addAll(voteList!!.Data)
                    if (vData.size != 0) {
                        recyclerView.visibility = View.VISIBLE
                        recyclerViewNoB.visibility = View.GONE
                        mAdapter = VotingAdapter(vData, this@FragmentHome)
                        recyclerView.adapter = mAdapter
                    } else {
                        recyclerView.visibility = View.GONE
                        recyclerViewNoB.visibility = View.VISIBLE
                    }

                } else {
                    Log.d("jsonObject", response.message())
                }
                fetchAccount()

            }

            override fun onFailure(call: Call<BallotResponse>, t: Throwable) {
                swipeContainer.isRefreshing = false
                Log.d("jsonObject", t.message.toString())
                //Toast.makeText(this@LoginActivity, t.message, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun fetchAccount() {

        val call: Call<JsonObject> = apiServiceInterface!!.dashboard(AppPreferences.authToken.toString())
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(
                call: Call<JsonObject>,
                response: Response<JsonObject>
            ) {
                if (response.isSuccessful) {
                    val jsonObject: JsonObject? = response.body()
                    Log.d("jsonObjectM", jsonObject?.get("Flag").toString())
                    var flag = jsonObject?.get("Flag").toString();
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
                        var email:String = dObject?.get("EmailAddress")?.asString!!
                        var phone:String = dObject?.get("PhoneNo")?.asString!!
                        var aname:String = dObject?.get("AssociationName")?.asString!!
                        var sectorid:String = dObject?.get("SectorID")?.asString!!

                        AppPreferences.firstName = fname.trim()
                        AppPreferences.lastName = lname.trim()
                        AppPreferences.fullName = fname.plus(" ").plus(lname)
                        AppPreferences.emailId = email.trim()
                        AppPreferences.phoneNo = phone.trim()
                        AppPreferences.logoUrl = ApiUtils.baseCDNUrl.plus(logo.trim())
                        AppPreferences.aName = aname.trim()
                        AppPreferences.sectorId = sectorid.trim()



                    } else {
                        Log.d("jsonObject", "null")

                    }
                } else {
                    Log.d("jsonObject", response.message())
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.d("jsonObject", t.message.toString())
            }
        })
    }
}