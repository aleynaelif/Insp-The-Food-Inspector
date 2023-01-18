package com.ley.insp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.ButterKnife
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_edited_profile.*


class ProductDataFragment : Fragment() {

    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore
    private lateinit var productData: ProductData
    private lateinit var recyclerView: RecyclerView
    private lateinit var productAdapter : ProductAdapter
    private lateinit var barcode: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ButterKnife.bind(requireActivity())

        auth = Firebase.auth
        db = Firebase.firestore

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_product_data, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseVeriAlma()
    }

    fun firebaseVeriAlma() {


        db.collection("Products").addSnapshotListener { value, error ->
            if(error != null){
                Toast.makeText(getActivity(),error.localizedMessage, Toast.LENGTH_LONG).show()
            }
            else{
                if(value != null){
                    if(!value.isEmpty){

                        val documents = value.documents

                        for (document in documents){

                            barcode = arguments?.getString("barcode").toString()
                            if(!barcode.equals("not found")){
                            //val barcode1 = arguments?.getString("barcodeH").toString()

                            //döküman adı ürün barkodu olucak
                            if(document.id.equals(barcode)) {
                                productData = ProductData(
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
                                    document.get("misir") as? Boolean == true,
                                    (document.get("productName") as? String)!!,
                                    (document.get("productImage") as? String)!!
                                )
                                productAdapter = ProductAdapter(productData)

                                recyclerView.layoutManager = LinearLayoutManager(context)
                                recyclerView.adapter = productAdapter
                            }

                            }
                            else{
                                productData = ProductData(
                                    false,
                                    false,
                                    false,
                                    false,
                                    false,
                                    false,
                                    false,
                                    false,
                                    false,
                                    false,
                                    false,
                                    false,
                                    false,
                                    false,
                                    "null",
                                    "null"
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