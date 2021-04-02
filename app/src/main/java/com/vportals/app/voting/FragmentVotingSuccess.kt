package com.vportals.app.voting

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
import kotlinx.android.synthetic.main.activity_finish_survey.*
import kotlinx.android.synthetic.main.fragment_voting_success.*
import kotlinx.android.synthetic.main.fragment_voting_success.ivulogo
import kotlinx.android.synthetic.main.fragment_voting_success.ltitle
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
class FragmentVotingSuccess : Fragment(){
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var vName = ""
    private var vDate = ""
    private var vUnit = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        titlemorning.text = vName
        val ys = vUnit.replace("\"", "")
        //println(ys)
        val vaname = AppPreferences.aName.toString()
        ltitle.text = "Your selection has been counted for $ys in connection with:"
        nbtitle.text = "Email sent by Voting Portals on behalf of $vaname. If this message was found in your SPAM folder, please add our email to your safe-senders list."
        getDateTime()
        ivulogo.load(AppPreferences.logoUrl.toString())
        backToHome.setOnClickListener {
            requireActivity().finish()
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        vName = arguments?.getString("v_name").toString()
        vDate = arguments?.getString("v_date").toString()
        vUnit = arguments?.getString("v_unit").toString()
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_voting_success, container, false)
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
            FragmentVotingSuccess().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


    private fun getDateTime() {
        // Friday, July 31, 2020
        if(!vDate.isNullOrEmpty()){
            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            val formatter = SimpleDateFormat("MM/dd/yyyy")

            val output =  formatter.format(parser.parse(vDate))

            titledate.text = "Closes: ".plus(output)
        }
        else {
            titledate.text = "No Close Date"
        }
    }

}