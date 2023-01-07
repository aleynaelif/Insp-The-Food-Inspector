package com.ley.insp

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.profile_recycler_row.view.*


class ProfileAdapter (val profileList : ArrayList<String>, var profileAge: ArrayList<String>, val profileImage : ArrayList<Bitmap>, val choice: SurveyData) : RecyclerView.Adapter<ProfileAdapter.ProfileHolder>(){
    class ProfileHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileHolder {

        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.profile_recycler_row, parent,false)
        return ProfileHolder(view)
    }

    override fun onBindViewHolder(holder: ProfileHolder, position: Int) {

        //<--------- Profile Info------------>
        holder.itemView.name.text = profileList[position]
        holder.itemView.age.text = profileAge[position]
        holder.itemView.image.setImageBitmap(profileImage[position])
        holder.itemView.edit.setOnClickListener {
            val intent = Intent(holder.itemView.context, ProfileActivity::class.java)
            intent.putExtra("info", "updateProfile")
            holder.itemView.context.startActivity(intent)
        }

        //<---------Checkbox Info -------->

        holder.itemView.Sut.text = "Süt ve Süt Ürünleri"
        holder.itemView.Yumurta.text = "Yumurta"
        holder.itemView.Bal.text = "Bal"
        holder.itemView.Tereyagi.text = "Tereyağı"
        holder.itemView.Tavuk.text = "Tavuk"
        holder.itemView.KirmiziEt.text = "Kırmızı Et"
        holder.itemView.Deniz.text = "Deniz Ürünleri"
        holder.itemView.Domuz.text = "Domuz Eti"
        holder.itemView.Alkol.text = "Alkol"
        holder.itemView.Laktoz.text = "Laktoz"
        holder.itemView.Gluten.text = "Gluten"
        holder.itemView.Fistik.text = "Yer Fıstığı"
        holder.itemView.Soya.text = "Soya"
        holder.itemView.Misir.text = "Mısır"


        //<--------color changes---------->

        if(choice.sut)
            holder.itemView.Sut.setTextColor(Color.RED)
        if(choice.yumurta)
            holder.itemView.Yumurta.setTextColor(Color.RED)
        if(choice.bal)
            holder.itemView.Bal.setTextColor(Color.RED)
        if(choice.tereyagi)
            holder.itemView.Tereyagi.setTextColor(Color.RED)
        if(choice.tavuk)
            holder.itemView.Tavuk.setTextColor(Color.RED)
        if(choice.kirmiziEt)
            holder.itemView.KirmiziEt.setTextColor(Color.RED)
        if(choice.deniz)
            holder.itemView.Deniz.setTextColor(Color.RED)
        if(choice.domuz)
            holder.itemView.Domuz.setTextColor(Color.RED)
        if(choice.alkol)
            holder.itemView.Alkol.setTextColor(Color.RED)
        if(choice.laktoz)
            holder.itemView.Laktoz.setTextColor(Color.RED)
        if(choice.gluten)
            holder.itemView.Gluten.setTextColor(Color.RED)
        if(choice.fistik)
            holder.itemView.Fistik.setTextColor(Color.RED)
        if(choice.soya)
            holder.itemView.Soya.setTextColor(Color.RED)
        if(choice.misir)
            holder.itemView.Misir.setTextColor(Color.RED)


        holder.itemView.editChoice.setOnClickListener {
            val intent = Intent(holder.itemView.context, SurveyActivity::class.java)
            intent.putExtra("info", "updateSurvey")
            holder.itemView.context.startActivity(intent)
        }


        holder.itemView.signOut.setOnClickListener {
            var auth = Firebase.auth
            auth.signOut()
            val intent = Intent(holder.itemView.context, MainActivity::class.java)
            holder.itemView.context.startActivity(intent)
            (holder.itemView.context as Activity).finish()

        }
    }

    override fun getItemCount(): Int {
        return profileList.size

    }
}