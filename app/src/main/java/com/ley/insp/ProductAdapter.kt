package com.ley.insp

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.product_recycler_row.view.*
import kotlinx.android.synthetic.main.profile_recycler_row.view.*

class ProductAdapter (val product: SurveyData) : RecyclerView.Adapter<ProductAdapter.ProductHolder>(){
    class ProductHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductHolder {

        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.product_recycler_row, parent,false)
        return ProductHolder(view)
    }

    override fun onBindViewHolder(holder: ProductHolder, position: Int) {

        //<---------Checkbox Info -------->

        holder.itemView.productSut.text = "Süt ve Süt Ürünleri"
        holder.itemView.productYumurta.text = "Yumurta"
        holder.itemView.productBal.text = "Bal"
        holder.itemView.productTereyagi.text = "Tereyağı"
        holder.itemView.productTavuk.text = "Tavuk"
        holder.itemView.productKirmiziEt.text = "Kırmızı Et"
        holder.itemView.productDeniz.text = "Deniz Ürünleri"
        holder.itemView.productDomuz.text = "Domuz Eti"
        holder.itemView.productAlkol.text = "Alkol"
        holder.itemView.productLaktoz.text = "Laktoz"
        holder.itemView.productGluten.text = "Gluten"
        holder.itemView.productFistik.text = "Yer Fıstığı"
        holder.itemView.productSoya.text = "Soya"
        holder.itemView.productMisir.text = "Mısır"


        //<--------color changes---------->

        if(product.sut)
            holder.itemView.productSut.setTextColor(Color.RED)
        if(product.yumurta)
            holder.itemView.productYumurta.setTextColor(Color.RED)
        if(product.bal)
            holder.itemView.productBal.setTextColor(Color.RED)
        if(product.tereyagi)
            holder.itemView.productTereyagi.setTextColor(Color.RED)
        if(product.tavuk)
            holder.itemView.productTavuk.setTextColor(Color.RED)
        if(product.kirmiziEt)
            holder.itemView.productKirmiziEt.setTextColor(Color.RED)
        if(product.deniz)
            holder.itemView.productDeniz.setTextColor(Color.RED)
        if(product.domuz)
            holder.itemView.productDomuz.setTextColor(Color.RED)
        if(product.alkol)
            holder.itemView.productAlkol.setTextColor(Color.RED)
        if(product.laktoz)
            holder.itemView.productLaktoz.setTextColor(Color.RED)
        if(product.gluten)
            holder.itemView.productGluten.setTextColor(Color.RED)
        if(product.fistik)
            holder.itemView.productFistik.setTextColor(Color.RED)
        if(product.soya)
            holder.itemView.productSoya.setTextColor(Color.RED)
        if(product.misir)
            holder.itemView.productMisir.setTextColor(Color.RED)


       /* geridön tuşu????
       holder.itemView.signOut.setOnClickListener {
            var auth = Firebase.auth
            auth.signOut()
            val intent = Intent(holder.itemView.context, MainActivity::class.java)
            holder.itemView.context.startActivity(intent)
            (holder.itemView.context as Activity).finish()

        }*/
    }

    override fun getItemCount(): Int {
        return listOf(product).size
    }
}