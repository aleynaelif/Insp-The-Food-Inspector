package com.ley.insp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import com.ley.insp.databinding.FragmentScanBinding
import okhttp3.*
import org.json.JSONObject
import java.io.IOException


class ScanFragment : Fragment() {
    private var _binding: FragmentScanBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore
    private lateinit var surveyData: SurveyData
    private lateinit var storage: FirebaseStorage
    private lateinit var firestore: FirebaseFirestore



    val client = OkHttpClient()
    val client1 = OkHttpClient()




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth
        db = Firebase.firestore
        storage = Firebase.storage
        firestore = Firebase.firestore

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentScanBinding.inflate(inflater, container, false)
        _binding!!.scanItem.setOnClickListener {
            onButtonClick(it)
        }
        val view = binding.root
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Register the launcher and result handler
    private val barcodeLauncher = registerForActivityResult(
        ScanContract()) { result: ScanIntentResult ->
        if (result.contents == null)
            Toast.makeText(activity, "Cancelled", Toast.LENGTH_LONG).show()
        else {
        db.collection("Choices").addSnapshotListener { value, error ->

        if(error != null)
            Toast.makeText(getActivity(),error.localizedMessage,Toast.LENGTH_LONG).show()
        else{
        if(value != null && !value.isEmpty){

            val documents = value.documents
            for (document in documents){
                if(document.id.equals(auth.currentUser!!.uid)){
                    surveyData = SurveyData(
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


                    val request = Request.Builder()
                        .url("https://nutritionix-api.p.rapidapi.com/v1_1/item?upc="+ result.contents)
                        .get()
                        .addHeader("X-RapidAPI-Key", "8277f3b565mshf7a96c63bddb053p113ab0jsn8ba9ba3d7ce8")
                        .addHeader("X-RapidAPI-Host", "nutritionix-api.p.rapidapi.com")
                        .build()


                    client.newCall(request).enqueue(object : Callback {
                        override fun onFailure(call: Call, e: IOException)  {e.printStackTrace()}
                        override fun onResponse(call: Call, response: Response) {

                            if(!response.header("status_code").equals("404")){
                                var product = response.body()?.string()!!.toString()

                                val json = JSONObject(product)
                                var ingredients :String

                                //teker teker tarama
                                ingredients = json.getString("nf_ingredient_statement")
                                ingredients = ingredients.lowercase()

                                val user = mapOf("email" to auth.currentUser!!.email)

                                var productName = json.getString("brand_name") + " " + json.getString("item_name")

                                val userRef = firestore.collection("Product")

                                userRef.document(productName).set(user).addOnSuccessListener {

                                    if(auth.currentUser != null){

                                        val productMap = hashMapOf<String, Any>()

                                        productMap.put("userEmail", auth.currentUser!!.email!!)

                                        if(ingredients.contains("milk") || ingredients.contains("cream")
                                            || ingredients.contains("yogurt") || ingredients.contains("cheese")
                                            || ingredients.contains("butter") || ingredients.contains("casein")
                                            || ingredients.contains("whey") || ingredients.contains("lactose")
                                            || ingredients.contains("milk powder") || ingredients.contains("milk solids"))
                                        productMap.put("sut",true)
                                        else
                                            productMap.put("sut",false)
                                        if(ingredients.contains("egg") || ingredients.contains("egg whites")
                                            || ingredients.contains("egg yolks") || ingredients.contains("albumin")
                                            || ingredients.contains("globulin") || ingredients.contains("livetin")
                                            || ingredients.contains("ovomucin") || ingredients.contains("ovomucoid")
                                            || ingredients.contains("ovovitellin"))
                                        productMap.put("yumurta",true)
                                        else
                                            productMap.put("yumurta",false)
                                        if(ingredients.contains("honey") || ingredients.contains("bee pollen")
                                            || ingredients.contains("bee venom")  || ingredients.contains("royal jelly")
                                            || ingredients.contains("propolis"))
                                            productMap.put("bal",true)
                                        else
                                            productMap.put("bal",false)
                                        if(ingredients.contains("butter") || ingredients.contains("butterfat")
                                            || ingredients.contains("butter oil") || ingredients.contains("ghee")
                                            || ingredients.contains("buttercream"))
                                        productMap.put("tereyagi",true)
                                        else
                                            productMap.put("tereyagi",false)
                                        if(ingredients.contains("chicken"))
                                            productMap.put("tavuk",true)
                                        else
                                            productMap.put("tavuk",false)
                                        if(ingredients.contains("beef"))
                                            productMap.put("kirmiziEt",true)
                                        else
                                            productMap.put("kirmiziEt",false)
                                        if(ingredients.contains("fish") || ingredients.contains("anchovies")
                                            || ingredients.contains("sardines") || ingredients.contains("tuna")
                                            || ingredients.contains("salmon") || ingredients.contains("mackerel")
                                            || ingredients.contains("cod") || ingredients.contains("halibut")
                                            || ingredients.contains("omega-3"))
                                            productMap.put("deniz",true)
                                        else
                                            productMap.put("deniz",false)
                                        if(ingredients.contains("pork") || ingredients.contains("bacon")
                                            || ingredients.contains("ham") || ingredients.contains("gelatin"))
                                            productMap.put("domuz",true)
                                        else
                                            productMap.put("domuz",false)
                                        if(ingredients.contains("alcohol") || ingredients.contains("ethanol")
                                            || ingredients.contains("beer") || ingredients.contains("wine")
                                            || ingredients.contains("spirits") || ingredients.contains("liqueur"))
                                            productMap.put("alkol",true)
                                        else
                                            productMap.put("alkol",false)
                                        if(ingredients.contains("lactose") || ingredients.contains("milk")
                                            || ingredients.contains("cream") || ingredients.contains("whey")
                                            || ingredients.contains("curds") || ingredients.contains("dry milk powder")
                                            || ingredients.contains("condensed milk"))
                                            productMap.put("laktoz",true)
                                        else{
                                            if(ingredients.contains("lactose free") || ingredients.contains("lactose-free"))
                                            productMap.put("laktoz",false)
                                            else productMap.put("laktoz",false)
                                        }
                                        if(ingredients.contains("gluten") || ingredients.contains("wheat")
                                            || ingredients.contains("barley") || ingredients.contains("rye")
                                            || ingredients.contains("oats") || ingredients.contains("spelt")
                                            || ingredients.contains("kamut") || ingredients.contains("triticale")
                                            || ingredients.contains("malt") || ingredients.contains("brewer's yeast"))
                                        productMap.put("gluten",true)
                                        else{
                                            if(ingredients.contains("gluten free") || ingredients.contains("gluten-free"))
                                                productMap.put("gluten",false)
                                            else productMap.put("gluten",false)
                                        }
                                        if(ingredients.contains("peanuts") || ingredients.contains("peanut butter")
                                            || ingredients.contains("peanut oil") || ingredients.contains("peanut flour"))
                                        productMap.put("fistik",true)
                                        else{
                                            if(ingredients.contains("peanut free") || ingredients.contains("peanut-free"))
                                                productMap.put("fistik",false)
                                            else productMap.put("fistik",false)
                                        }
                                        if(ingredients.contains("soy") || ingredients.contains("soybeans")
                                            || ingredients.contains("tofu") || ingredients.contains("tempeh")
                                            || ingredients.contains("miso") || ingredients.contains("natto"))
                                        productMap.put("soya",true)
                                        else{
                                            if(ingredients.contains("soy free") || ingredients.contains("soy-free"))
                                            productMap.put("soya",false)
                                            else productMap.put("soya",false)
                                        }
                                        if(ingredients.contains("corn"))
                                        productMap.put("misir",true)
                                        else
                                            productMap.put("misir",false)


                                        userRef.document(productName).set(productMap).addOnSuccessListener {
                                            openFragment(ProductDataFragment())
                                        }.addOnFailureListener {
                                            Toast.makeText(activity,it.localizedMessage,Toast.LENGTH_LONG).show()
                                        }
                                    }
                                    else{ Toast.makeText(activity,"Lütfen Seçim Yapınız!", Toast.LENGTH_SHORT).show() }
                                }.addOnFailureListener { Toast.makeText(activity,it.localizedMessage,Toast.LENGTH_LONG).show()}
                            }
                            else{

                                val MyRequest = Request.Builder()
                                    .url("http://18.220.33.203/barcode/find?barcode=" + result.contents)
                                    .build()


                                client1.newCall(MyRequest).enqueue(object : Callback {
                                    override fun onFailure(call: Call, e: IOException)  {}
                                    override fun onResponse(call: Call, response: Response) {

                                        var MyProduct = response.body()?.string()!!.toString()
                                        //URLEncoder.encode(product, "UTF-8")

                                        Log.d("MYTAGDENEME",MyProduct)

                                    }
                                })
                                //  Toast.makeText(activity, "Scanned: " + result.contents, Toast.LENGTH_LONG).show()
                            }
                        }
                    })
                }
            }
        }
        }
        } }
    }

    // Launch
    fun onButtonClick(view: View?) {
        barcodeLauncher.launch(ScanOptions())
    }

    fun openFragment(fragment : Fragment) {
    val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.replace(R.id.frameLayout, fragment)
        transaction?.addToBackStack(null)
        transaction?.commit()
}

}