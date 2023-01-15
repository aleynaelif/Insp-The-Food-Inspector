package com.ley.insp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_edited_profile.*


class ProductDataFragment : Fragment() {

    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore
    private lateinit var productData: SurveyData

    private lateinit var productAdapter : ProductAdapter

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
        return inflater.inflate(R.layout.fragment_product_data, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseVeriAlma()
    }

    fun firebaseVeriAlma() {

        
        db.collection("Product").addSnapshotListener { value, error ->
            if(error != null){
                Toast.makeText(getActivity(),error.localizedMessage, Toast.LENGTH_LONG).show()
            }
            else{
                if(value != null){
                    if(!value.isEmpty){

                        val documents = value.documents

                        for (document in documents){
                            //döküman adı ürün ismi olucak
                            if(document.id.equals(auth.currentUser!!.uid)){
                                productData = SurveyData(
                                    document.get("sut") as? Boolean == true,
                                    document.get("yumurta") as? Boolean == true,
                                    document.get("bal") as? Boolean == true,
                                    document.get("tereyagi") as? Boolean == true,
                                    document.get("tavuk") as? Boolean == true,
                                    document.get("kirmiziEt") as? Boolean == true,
                                    document.get("deniz") as? Boolean == true,
                                    document.get("domuz") as? Boolean == true,
                                    document.get("alkol") as? Boolean == true,
                                    document.get("laktoz") as? Boolean == true,
                                    document.get("gluten") as? Boolean == true,
                                    document.get("fistik") as? Boolean == true,
                                    document.get("soya") as? Boolean == true,
                                    document.get("misir") as? Boolean == true
                                )

                                productAdapter = ProductAdapter(productData)

                                recyclerView.layoutManager = LinearLayoutManager(context)
                                recyclerView.adapter = productAdapter
                            }
                        }
                    }
                }
            }
        }
    }
}