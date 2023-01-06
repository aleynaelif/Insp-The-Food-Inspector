package com.ley.insp

import android.content.Intent
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_survey.view.*
import kotlinx.android.synthetic.main.profile_recycler_row.view.*


class SurveyAdapter (val choice: SurveyData) : RecyclerView.Adapter<SurveyAdapter.SurveyHolder>(){
    class SurveyHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SurveyHolder {

        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.profile_recycler_row, parent,false)
        return SurveyHolder(view)
    }

    override fun onBindViewHolder(holder: SurveyHolder, position: Int) {

        holder.itemView.sut.text = choice.sut.toString()[position].toString()


        holder.itemView.editChoice.setOnClickListener {
            val intent = Intent(holder.itemView.context, SurveyActivity::class.java)
            intent.putExtra("info", "updateSurvey")
            holder.itemView.context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }


}