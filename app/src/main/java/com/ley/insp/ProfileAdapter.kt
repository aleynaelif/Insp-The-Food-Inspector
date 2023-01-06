package com.ley.insp

import android.content.Intent
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.profile_recycler_row.view.*


class ProfileAdapter (val profileList : ArrayList<String>, var profileAge: ArrayList<String>, val profileImage : ArrayList<Bitmap>) : RecyclerView.Adapter<ProfileAdapter.ProfileHolder>(){
    class ProfileHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileHolder {

        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.profile_recycler_row, parent,false)
        return ProfileHolder(view)
    }

    override fun onBindViewHolder(holder: ProfileHolder, position: Int) {

        holder.itemView.name.text = profileList[position]
        holder.itemView.age.text = profileAge[position]
        holder.itemView.image.setImageBitmap(profileImage[position])
        holder.itemView.edit.setOnClickListener {
            val intent = Intent(holder.itemView.context, ProfileActivity::class.java)
            intent.putExtra("info", "updateProfile")
            holder.itemView.context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return profileList.size

    }
}