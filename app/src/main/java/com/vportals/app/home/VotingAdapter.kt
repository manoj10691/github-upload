package com.vportals.app.home

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.vportals.app.R
import com.vportals.app.model.Ballot
import com.vportals.app.service.AppPreferences
import kotlinx.android.synthetic.main.voting_list_row.view.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.abs

class VotingAdapter(
    val ballotList: ArrayList<Ballot>,
    private val cellClickListener: CellClickListener
) : RecyclerView.Adapter<VotingAdapter.ViewHolder>() {
    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VotingAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.voting_list_row, parent, false)
        return ViewHolder(v)
    }
    //this method is binding the data on the list
    //@RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: VotingAdapter.ViewHolder, position: Int) {
        holder.bindItems(ballotList[position])

        holder.itemView.setOnClickListener {
            cellClickListener.onCellClickListener(ballotList[position])
        }
    }
    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return ballotList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //@RequiresApi(Build.VERSION_CODES.O)
        fun bindItems(balt: Ballot) {
            //val txt_name: TextView = itemView.findViewById(R.id.txt_name)
            //val txt_email_id: TextView = itemView.findViewById(R.id.txt_email_id)
            itemView.txt_name.text = balt.VoteText
            Log.d("SectorID", AppPreferences.sectorId.toString())
            if(AppPreferences.sectorId.toString() == "1"){
                itemView.txt_unit.text = "Unit: "+balt.UnitNumber
                itemView.rlmdtnewadd.visibility = View.GONE
            }
           else if(AppPreferences.sectorId.toString() == "2"){
                itemView.txt_unit.text = "Unit: "+balt.UnitNumber
                itemView.txt_addr.text = "Address: "+balt.Address
            }
            else{
                itemView.rlmdtnewadd.visibility = View.GONE
                itemView.txt_unit.visibility = View.GONE
            }

            if(balt.HasVoted){
                itemView.txt_hasv.text = "Completed"
                itemView.txt_hasv.setTextColor(ContextCompat.getColor(itemView.context, R.color.button_bg));
            }
            else{
                itemView.txt_hasv.text = "Pending"
                itemView.txt_hasv.setTextColor(ContextCompat.getColor(itemView.context, R.color.pstatus));
            }

            if(balt.MeetingDateTime != null){
            if(!balt.MeetingDateTime.isNullOrEmpty()) {
                val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                val formatter = SimpleDateFormat("MM/dd/yyyy")

                val output = formatter.format(parser.parse(balt.MeetingDateTime))
                //println(output)
                itemView.txt_email_id.text = "Closes: ".plus(output)
                val now = formatter.format(Date())

                val firstDate: Date = formatter.parse(output)
                val secondDate: Date = formatter.parse(now)

                val diffInMillies = abs(secondDate.time - firstDate.time)
                val diff: Long = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS)
//                Log.d("NKK", "diff")
//                Log.d("NKK", diff.toString())
                if (diff > 0) {
                    if (firstDate.after(secondDate)) {
                        itemView.txt_email_id2.text = diff.toString().plus(" days left")
                    } else {
                        val ddate: String = "- " + diff.toString().plus(" days left")
                        itemView.txt_email_id2.text = ddate
                    }
                } else if (diff.toInt() == 0) {
                    itemView.txt_email_id2.text = diff.toString().plus(" days left")
                } else {
                    itemView.txt_email_id2.text = ""
                }
            }
            else{
                if(balt.VoteTypeID == 1)
                    itemView.txt_email_id.text = "No Close Date"
                else
                    itemView.txt_email_id.text = ""

                itemView.txt_email_id2.text = ""
            }
            }
            else{
                if(balt.VoteTypeID == 1)
                    itemView.txt_email_id.text = "No Close Date"
                else
                    itemView.txt_email_id.text = ""

                itemView.txt_email_id2.text = ""
            }
            //set the onclick listener for the list item
//            itemView.setOnClickListener({
//                Toast.makeText(itemView.context, student.name + "\n" + student.email, Toast.LENGTH_LONG).show();
//            })
        }

    }

}