package com.ley.insp

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.api.Context
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.history_recycler_row.view.*
import kotlinx.android.synthetic.main.product_recycler_row.view.*

class HistoryAdapter (val productNameList : ArrayList<String>, val productImage : ArrayList<String>, val productBarcode : ArrayList<String>) : RecyclerView.Adapter<HistoryAdapter.HistoryHolder>() {
    class HistoryHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryHolder {

        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.history_recycler_row, parent, false)
        return HistoryHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryHolder, position: Int) {
        holder.itemView.historyProductName.text = productNameList[position]
        Picasso.get().load(productImage[position]).into(holder.itemView.historyImage)

        holder.itemView.historyImage.setOnClickListener{

            var fragment = ProductDataFragment()
            val bundle = Bundle()
            bundle.putString("barcodeH", productBarcode[position])
            fragment.arguments = bundle
            val transaction = (holder.itemView.context as HomepageActivity).supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frameLayout, ProductDataFragment())
            transaction.addToBackStack(null)
            transaction.commit()

        }

    }

    override fun getItemCount(): Int {
        return productNameList.size

    }
}