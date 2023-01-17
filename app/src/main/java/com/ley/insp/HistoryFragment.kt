package com.ley.insp

import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_edited_profile.*
import kotlinx.android.synthetic.main.fragment_history.*


class HistoryFragment : Fragment() {

    var productNameList = ArrayList<String>()
    var productImage = ArrayList<String>()
    var productBarcode = ArrayList<String>()
    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore
    private lateinit var recyclerView: RecyclerView


    private lateinit var historyAdapter: HistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth
        db = Firebase.firestore
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_product_data, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseVeriAlma()
    }

    fun firebaseVeriAlma(){

        db.collection("Products").get().addOnSuccessListener { result->
            for(document in result){
                db.collection("Products").document(document.id).get().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val document = task.result
                        if (document != null && document.exists()) {
                            if (document.getString("userEmail")!!
                                    .equals(auth.currentUser!!.email)
                            ) {
                                productNameList.add((document.get("productName") as? String).toString())
                                productImage.add((document.get("productImage") as? String).toString())
                                productBarcode.add((document.get("barcode") as? String).toString())


                                historyAdapter = HistoryAdapter(productNameList,productImage,productBarcode)
                                recyclerView.layoutManager = LinearLayoutManager(context)
                                recyclerView.adapter = historyAdapter
                            } else {
                                // object does not exist

                            }
                        }
                    }
                }
            }

        }.addOnFailureListener { exception-> }
    }
}