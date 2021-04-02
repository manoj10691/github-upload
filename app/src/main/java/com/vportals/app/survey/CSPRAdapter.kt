package com.vportals.app.survey

import android.graphics.Color
import android.util.Log
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vportals.app.R
import com.vportals.app.model.VOption
import kotlinx.android.synthetic.main.cspr_list_row.view.*
import java.util.*

class CSPRAdapter(
    val ballotList: List<VOption>, val maxSelect: Int
) : RecyclerView.Adapter<CSPRAdapter.ViewHolder>() {
    private var mSelectedItem = -1
    private var checkBoxStateArray = SparseBooleanArray()
    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CSPRAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(
            R.layout.cspr_list_row,
            parent,
            false
        )
        return ViewHolder(v)
    }
    //this method is binding the data on the list
    //@RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: CSPRAdapter.ViewHolder, position: Int) {
        holder.bindItems(ballotList[position], position, checkBoxStateArray)
//        holder.bindItems(ballotList[position])
        holder.itemView.setOnClickListener {
            //holder.itemView.v_select.isChecked = true
            //    cellClickListener.onCellClickListener(ballotList[position])
            //holder.itemView.v_select.isChecked = mSelectedItem == position;
//            mSelectedItem = position
            Log.d("MAX",maxSelect.toString())
            Log.d("MAXA",checkBoxStateArray.size().toString())
//            var checkmax = 0
//            for (item in ballotList) {
//                if (item.HasSelected) {
//                    checkmax++
//                }
//            }
//            Log.d("MAXB",checkmax.toString())
//            if(checkmax != maxSelect) {
                if (!checkBoxStateArray.get(position, false)) {
                    if(checkBoxStateArray.size() != maxSelect) {
                        checkBoxStateArray.put(position, true)
                        ballotList[position].HasSelected = true
                    }

                } else {//checkbox unchecked
                    checkBoxStateArray.delete(position)
                    ballotList[position].HasSelected = false
                }

                notifyDataSetChanged()
            //}
            //cellVClickListener.onCellVClickListener(ballotList[position])
        }
//        holder.itemView.v_bio.setOnClickListener{
//
//            if(!ballotList[position].optionAttachments.isNullOrEmpty()) {
//                //cellVBClickListener.onCellVBClickListener(ballotList[position].optionAttachments[0],ballotList[position].FullName)
//            }
//
//        }
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
        fun bindItems(balt: VOption, position: Int, selectedPosition: SparseBooleanArray) {
            //val txt_name: TextView = itemView.findViewById(R.id.txt_name)
            //val txt_email_id: TextView = itemView.findViewById(R.id.txt_email_id)
            itemView.v_name.text = balt.OptionText
            //set the onclick listener for the list item

//            if ((selectedPosition == -1 && position == 0)){
//                itemView.v_select.isChecked = true
//                itemView.v_bio.setImageResource(R.drawable.ic_action_bio_active)
//                itemView.vclayout.setBackgroundResource(R.drawable.option_card_bg_active)
//                itemView.v_name.setTextColor(Color.parseColor("#2883ED"))
//            }
//
//
//            if (selectedPosition == position){
//                itemView.v_select.isChecked = true
//                itemView.v_bio.setImageResource(R.drawable.ic_action_bio_active)
//                itemView.vclayout.setBackgroundResource(R.drawable.option_card_bg_active)
//                itemView.v_name.setTextColor(Color.parseColor("#2883ED"))
//            }
            if(!selectedPosition.get(position,false))
            {//checkbox unchecked.
                itemView.v_select.isChecked = false
                itemView.v_bio.setImageResource(R.drawable.ic_action_bio)
                itemView.vclayout.setBackgroundResource(R.drawable.option_card_bg)
                itemView.v_name.setTextColor(Color.parseColor("#4A4A4A"))
            }
            else{
                itemView.v_select.isChecked = true
                itemView.v_bio.setImageResource(R.drawable.ic_action_bio_active)
                itemView.vclayout.setBackgroundResource(R.drawable.option_card_bg_active)
                itemView.v_name.setTextColor(Color.parseColor("#2883ED"))
            }



        }

    }

}