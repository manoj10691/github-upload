package com.vportals.app.survey

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vportals.app.R
import com.vportals.app.model.VOption
import com.vportals.app.model.VOptionGroup
import com.vportals.app.voting.CellVClickListener
import kotlinx.android.synthetic.main.activity_vote_options.*
import kotlinx.android.synthetic.main.csmc_options.view.*
import kotlinx.android.synthetic.main.csmcrc_options.view.*
import kotlinx.android.synthetic.main.cspr_options.view.*
import java.util.*


class SurveyOptionAdapter(
    val ballotList: List<VOptionGroup>,
    private val cellCheckClickListener: CellCheckClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
//    lateinit var linearLayoutManager: LinearLayoutManager
    lateinit var csmcAdapter: CSMCAdapter
    lateinit var csprAdapter: CSPRAdapter
    lateinit var recyclerView: RecyclerView
    private val VIEW_TYPE_CSMC = 1
    private val VIEW_TYPE_CSPR = 2
    private val VIEW_TYPE_CSMCRC = 3
    private val VIEW_TYPE_TWO = 4
    private var isChecking = true
    var mCheckedId: Int = 0

    private inner class CSMCViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(
        itemView
    ) {

        internal fun bind(position: Int) {
            // This method will be called anytime a list item is created or update its data
            itemView.rvCsMcTitle.text = ballotList[position].GroupName
            recyclerView = itemView.rvCsMcVoting
            recyclerView.layoutManager = LinearLayoutManager(itemView.context)
//            val vData = ArrayList<VOption>()
////            ballotList[position]
//            vData.addAll(ballotList[position].voteOptions)
            if (ballotList[position].voteOptions.isNotEmpty()) {

                csmcAdapter = CSMCAdapter(
                    ballotList[position].voteOptions
                )
                recyclerView.adapter = csmcAdapter
                recyclerView.isNestedScrollingEnabled = false
            }
        }
    }

    private inner class CSPRViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(
        itemView
    ) {

        internal fun bind(position: Int) {
            // This method will be called anytime a list item is created or update its data
            itemView.rvCsPrTitle.text = ballotList[position].GroupName
            recyclerView = itemView.rvCsPrVoting
            recyclerView.layoutManager = LinearLayoutManager(itemView.context)
            //val vData = ArrayList<VOption>()
//            ballotList[position]
            //vData.addAll(ballotList[position].voteOptions)
            if (ballotList[position].voteOptions.isNotEmpty()) {
                recyclerView.visibility = View.VISIBLE
                csprAdapter = CSPRAdapter(
                    ballotList[position].voteOptions,
                    ballotList[position].MaxSelectionCount
                )
                recyclerView.adapter = csprAdapter
                recyclerView.isNestedScrollingEnabled = false
            }
        }
    }

    private inner class CSMCRCViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(
        itemView
    ) {
        internal fun bind(position: Int) {
            // This method will be called anytime a list item is created or update its data
            itemView.rvCsMcCrTitle.text = ballotList[position].GroupName
            itemView.first_group.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId ->
                if (checkedId != -1 && isChecking) {
                    isChecking = false
                    itemView.second_group.clearCheck()
                    mCheckedId = checkedId
                    val idx: Int = itemView.first_group.indexOfChild(itemView.findViewById(mCheckedId))
                    println("idx0")
                    println(idx+1)
                    ballotList[position].voteOptions[0].Rating = idx+1
                }
                isChecking = true
                cellCheckClickListener.onCellCheckClickListener()
            })

            itemView.second_group.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId ->
                if (checkedId != -1 && isChecking) {
                    isChecking = false
                    itemView.first_group.clearCheck()
                    mCheckedId = checkedId
                    val idx: Int = itemView.second_group.indexOfChild(itemView.findViewById(mCheckedId))
                    println("idx1")
                    println(idx+4)
                    ballotList[position].voteOptions[0].Rating = idx+4
                }
                isChecking = true
                cellCheckClickListener.onCellCheckClickListener()
            })

            itemView.tvAddComment.setOnClickListener {

                itemView.btnSave.visibility = View.VISIBLE
                itemView.edComment.visibility = View.VISIBLE
                itemView.tvAddComment.visibility = View.GONE
                itemView.rbrl.visibility = View.GONE

                }
            itemView.btnSave.setOnClickListener {
                ballotList[position].voteOptions[1].Comment = itemView.edComment.text.toString()
                itemView.tvAddComment.visibility = View.VISIBLE
                itemView.btnSave.visibility = View.GONE
                itemView.edComment.visibility = View.GONE
                itemView.rbrl.visibility = View.VISIBLE
            }
        }
    }

    private inner class ViewHolder2 internal constructor(itemView: View) : RecyclerView.ViewHolder(
        itemView
    ) {


        internal fun bind(position: Int) {
            // This method will be called anytime a list item is created or update its data
            //Do your stuff here
            println(ballotList[position].GroupType)
            itemView.rvCsMcTitle.text = ballotList[position].GroupName
            //itemView.v_name.text = ballotList[position].GroupName
        }
    }

    private var mSelectedItem = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_CSMC) {
            CSMCViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.csmc_options,
                    parent,
                    false
                )
            )
        } else if (viewType == VIEW_TYPE_CSPR) {
            CSPRViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.cspr_options,
                    parent,
                    false
                )
            )
        } else if (viewType == VIEW_TYPE_CSMCRC) {
            CSMCRCViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.csmcrc_options,
                    parent,
                    false
                )
            )
        }else ViewHolder2(
            LayoutInflater.from(parent.context).inflate(
                R.layout.csmc_options,
                parent,
                false
            )
        ) //if it's not VIEW_TYPE_ONE then its VIEW_TYPE_TWO
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (ballotList[position].GroupType == "csmc") {

            if (ballotList[position].MaxSelectionCount == 1)
            (holder as CSMCViewHolder).bind(position)
            else
             (holder as CSPRViewHolder).bind(position)

        }
        else if (ballotList[position].GroupType == "csmcrc") { // put your condition, according to your requirements
            (holder as CSMCRCViewHolder).bind(position)
        }
        else {
            (holder as ViewHolder2).bind(position)
        }
    }

    override fun getItemCount(): Int {
        return ballotList.size
    }

    override fun getItemViewType(position: Int): Int {
        // here you can get decide from your model's ArrayList, which type of view you need to load. Like
        return if (ballotList[position].GroupType == "csmc") {
            if (ballotList[position].MaxSelectionCount == 1)
                VIEW_TYPE_CSMC
            else
                VIEW_TYPE_CSPR

        } else if (ballotList[position].GroupType == "csmcrc") { // put your condition, according to your requirements
            VIEW_TYPE_CSMCRC
        } else VIEW_TYPE_TWO
    }

}