package com.vportals.app.voting

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.vportals.app.R
import com.vportals.app.model.VOption
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.voption_list_row.view.*
import java.util.*

class VotingOptionAdapter(
    val ballotList: ArrayList<VOption>,
    private val cellVClickListener: CellVClickListener,
    private val cellVBClickListener: CellVBClickListener
) : RecyclerView.Adapter<VotingOptionAdapter.ViewHolder>() {
    private var mSelectedItem = -1
    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VotingOptionAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(
            R.layout.voption_list_row,
            parent,
            false
        )
        return ViewHolder(v)
    }
    //this method is binding the data on the list
    //@RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: VotingOptionAdapter.ViewHolder, position: Int) {
        holder.bindItems(ballotList[position], position, mSelectedItem)
//        holder.bindItems(ballotList[position])
        holder.itemView.setOnClickListener {
            //holder.itemView.v_select.isChecked = true
            //    cellClickListener.onCellClickListener(ballotList[position])
            //holder.itemView.v_select.isChecked = mSelectedItem == position;
            mSelectedItem = position
            notifyDataSetChanged()
            cellVClickListener.onCellVClickListener(ballotList[position])
        }
        holder.itemView.v_bio.setOnClickListener{

            if(!ballotList[position].optionAttachments.isNullOrEmpty()) {
                cellVBClickListener.onCellVBClickListener(ballotList[position].optionAttachments[0],ballotList[position].FullName)
            }

        }
        if(ballotList[position].optionAttachments.isNullOrEmpty()) {
            holder.itemView.v_bio.visibility = View.GONE
        }
    }
    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return ballotList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //@RequiresApi(Build.VERSION_CODES.O)
        fun bindItems(balt: VOption, position: Int, selectedPosition: Int) {
            //val txt_name: TextView = itemView.findViewById(R.id.txt_name)
            //val txt_email_id: TextView = itemView.findViewById(R.id.txt_email_id)
            itemView.v_name.text = balt.FullName
            //set the onclick listener for the list item

            if ((selectedPosition == -1 && position == 0)){
                itemView.v_select.isChecked = true
                itemView.v_bio.setImageResource(R.drawable.ic_action_bio_active)
                itemView.vclayout.setBackgroundResource(R.drawable.option_card_bg_active)
                itemView.v_name.setTextColor(Color.parseColor("#2883ED"))
            }


            if (selectedPosition == position){
                itemView.v_select.isChecked = true
                itemView.v_bio.setImageResource(R.drawable.ic_action_bio_active)
                itemView.vclayout.setBackgroundResource(R.drawable.option_card_bg_active)
                itemView.v_name.setTextColor(Color.parseColor("#2883ED"))
            }
            else{
                itemView.v_select.isChecked = false
                itemView.v_bio.setImageResource(R.drawable.ic_action_bio)
                itemView.vclayout.setBackgroundResource(R.drawable.option_card_bg)
                itemView.v_name.setTextColor(Color.parseColor("#4A4A4A"))
            }



        }

    }

}