package com.ley.insp


import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import com.ley.insp.databinding.FragmentScanBinding
import kotlinx.android.synthetic.main.fragment_edited_profile.*
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit


class ScanFragment : Fragment() {
    private var _binding: FragmentScanBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var surveyData: SurveyData
    private lateinit var storage: FirebaseStorage
    private lateinit var firestore: FirebaseFirestore
    private lateinit var database: DatabaseReference

    val client = OkHttpClient()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth
        db = Firebase.firestore
        storage = Firebase.storage
        firestore = Firebase.firestore
        database = Firebase.database.reference

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
        ScanContract()
    ) { result: ScanIntentResult ->
        if (result.contents == null)
            Toast.makeText(activity, "Cancelled", Toast.LENGTH_LONG).show()
        else {
            db.collection("Choices").addSnapshotListener { value, error ->

                if (error != null)
                    Toast.makeText(getActivity(), error.localizedMessage, Toast.LENGTH_LONG).show()
                else {
                    if (value != null && !value.isEmpty) {

                        val documents = value.documents
                        for (document in documents) {
                            if (document.id.equals(auth.currentUser!!.uid)) {
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

                                val docRef = firestore.collection("Products").document(result.contents)

                                docRef.get().addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        val document = task.result
                                        if (document != null && document.exists()) {
                                            //surveyData da gönderilicek
                                             openFragment(ProductDataFragment(),result.contents)
                                        } else {
                                            // object does not exist
                                            APIRequest(result.contents, surveyData)
                                        }
                                    } else {
                                        // handle error
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Launch
    fun onButtonClick(view: View?) {
        barcodeLauncher.launch(ScanOptions())
    }

    fun openFragment(fragment: Fragment, barcode: String) {

        val bundle = Bundle()
        bundle.putString("barcode", barcode)
        fragment.arguments = bundle
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.replace(R.id.frameLayout, fragment)
        transaction?.addToBackStack(null)
        transaction?.commit()
    }

    fun APIRequest(barcode : String, surveyData: SurveyData){


        val request = Request.Builder()
            .url("https://nutritionix-api.p.rapidapi.com/v1_1/item?upc=" + barcode)
            .get()
            .addHeader("X-RapidAPI-Key", "8277f3b565mshf7a96c63bddb053p113ab0jsn8ba9ba3d7ce8")
            .addHeader("X-RapidAPI-Host", "nutritionix-api.p.rapidapi.com")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call, response: Response) {

                if (!response.header("status").equals("404 Not Found")) {

                    var product = response.body()?.string()!!.toString()

                    val json = JSONObject(product)
                    var ingredients: String

                    //teker teker tarama
                    ingredients = json.getString("nf_ingredient_statement")
                    ingredients = ingredients.lowercase()

                    val clientImage = OkHttpClient()

                    val request = Request.Builder()
                        .url("https://seo-api.p.rapidapi.com/v1/image/q=" + json.getString("brand_name").filter { !it.isWhitespace() } + json.getString("item_name").filter { !it.isWhitespace() })
                        .get().addHeader("X-Proxy-Location", "EU")
                        .addHeader("X-User-Agent", "desktop")
                        .addHeader("X-RapidAPI-Key", "8277f3b565mshf7a96c63bddb053p113ab0jsn8ba9ba3d7ce8")
                        .addHeader("X-RapidAPI-Host", "seo-api.p.rapidapi.com").build()

                    val responseImage =clientImage.newCall(request).execute()
                    //null kontrolü
                    var productImage =responseImage.body()?.string()!!.toString()

                    val jsonImage = JSONObject(productImage)
                    var image = jsonImage.getJSONArray("image_results")
                    var url = image.getJSONObject(0).getJSONObject("image").getString("src")


                    var productName = json.getString("brand_name") + " " + json.getString("item_name")


                    val userRef = firestore.collection("Products")

                    userRef.document(barcode).get().addOnSuccessListener {

                        if (auth.currentUser != null) {

                            val productMap = hashMapOf<String, Any>()

                            productMap.put("userEmail", auth.currentUser!!.email!!)
                            productMap.put("barcode",barcode)
                            productMap.put("productName", productName)
                            productMap.put("productImage", url)
                            println("--------------------")
                            println(surveyData.sut)

                            if ((ingredients.contains("milk") || ingredients.contains("cream")
                                || ingredients.contains("yogurt") || ingredients.contains("cheese")
                                || ingredients.contains("butter") || ingredients.contains("casein")
                                || ingredients.contains("whey") || ingredients.contains("lactose")
                                || ingredients.contains("milk powder") || ingredients.contains("milk solids")) && surveyData.sut)
                                productMap.put("sut", true)
                            else
                                productMap.put("sut", false)
                            if ((ingredients.contains("egg") || ingredients.contains("egg whites")
                                || ingredients.contains("egg yolks") || ingredients.contains("albumin")
                                || ingredients.contains("globulin") || ingredients.contains("livetin")
                                || ingredients.contains("ovomucin") || ingredients.contains("ovomucoid")
                                || ingredients.contains("ovovitellin")) && surveyData.yumurta)
                                productMap.put("yumurta", true)
                            else
                                productMap.put("yumurta", false)
                            if ((ingredients.contains("honey") || ingredients.contains("bee pollen")
                                        || ingredients.contains("bee venom") || ingredients.contains("royal jelly")
                                        || ingredients.contains("propolis")) && surveyData.bal)
                                productMap.put("bal", true)
                            else
                                productMap.put("bal", false)
                            if ((ingredients.contains("butter") || ingredients.contains("butterfat")
                                || ingredients.contains("butter oil") || ingredients.contains("ghee")
                                || ingredients.contains("buttercream")) && surveyData.tereyagi)
                                productMap.put("tereyagi", true)
                            else
                                productMap.put("tereyagi", false)
                            if (ingredients.contains("chicken") && surveyData.tavuk)
                                productMap.put("tavuk", true)
                            else
                                productMap.put("tavuk", false)
                            if (ingredients.contains("beef") && surveyData.kirmiziEt)
                                productMap.put("kirmiziEt", true)
                            else
                                productMap.put("kirmiziEt", false)
                            if ((ingredients.contains("fish") || ingredients.contains("anchovies")
                                || ingredients.contains("sardines") || ingredients.contains("tuna")
                                || ingredients.contains("salmon") || ingredients.contains("mackerel")
                                || ingredients.contains("cod") || ingredients.contains("halibut")
                                || ingredients.contains("omega-3")) && surveyData.deniz)
                                productMap.put("deniz", true)
                            else
                                productMap.put("deniz", false)
                            if ((ingredients.contains("pork") || ingredients.contains("bacon")
                                || ingredients.contains("ham") || ingredients.contains("gelatin")) && surveyData.domuz)
                                productMap.put("domuz", true)
                            else
                                productMap.put("domuz", false)
                            if ((ingredients.contains("alcohol") || ingredients.contains("ethanol")
                                || ingredients.contains("beer") || ingredients.contains("wine")
                                || ingredients.contains("spirits") || ingredients.contains("liqueur")) && surveyData.alkol)
                                productMap.put("alkol", true)
                            else
                                productMap.put("alkol", false)
                            if ((ingredients.contains("lactose") || ingredients.contains("milk")
                                || ingredients.contains("cream") || ingredients.contains("whey")
                                || ingredients.contains("curds") || ingredients.contains("dry milk powder")
                                || ingredients.contains("condensed milk")) && surveyData.laktoz)
                                productMap.put("laktoz", true)
                            else {
                                if (ingredients.contains("lactose free") || ingredients.contains("lactose-free"))
                                    productMap.put("laktoz", false)
                                else productMap.put("laktoz", false)
                            }
                            if ((ingredients.contains("gluten") || ingredients.contains("wheat")
                                || ingredients.contains("barley") || ingredients.contains("rye")
                                || ingredients.contains("oats") || ingredients.contains("spelt")
                                || ingredients.contains("kamut") || ingredients.contains("triticale")
                                || ingredients.contains("malt") || ingredients.contains("brewer's yeast")) && surveyData.gluten)
                                productMap.put("gluten", true)
                            else {
                                if (ingredients.contains("gluten free") || ingredients.contains("gluten-free"))
                                    productMap.put("gluten", false)
                                else productMap.put("gluten", false)
                            }
                            if ((ingredients.contains("peanuts") || ingredients.contains("peanut butter")
                                || ingredients.contains("peanut oil") || ingredients.contains("peanut flour")) && surveyData.fistik)
                                productMap.put("fistik", true)
                            else {
                                if (ingredients.contains("peanut free") || ingredients.contains("peanut-free"))
                                    productMap.put("fistik", false)
                                else productMap.put("fistik", false)
                            }
                            if ((ingredients.contains("soy") || ingredients.contains("soybeans")
                                || ingredients.contains("tofu") || ingredients.contains("tempeh")
                                || ingredients.contains("miso") || ingredients.contains("natto")) && surveyData.soya)
                                productMap.put("soya", true)
                            else {
                                if (ingredients.contains("soy free") || ingredients.contains("soy-free"))
                                    productMap.put("soya", false)
                                else productMap.put("soya", false)
                            }
                            if (ingredients.contains("corn") && surveyData.misir)
                                productMap.put("misir", true)
                            else
                                productMap.put("misir", false)

                            userRef.document(barcode)
                                .set(productMap).addOnSuccessListener { openFragment(ProductDataFragment(), barcode)
                                }.addOnFailureListener {
                                    Toast.makeText(activity, it.localizedMessage, Toast.LENGTH_LONG).show()
                                }
                        } else {
                            Toast.makeText(activity, "Lütfen Seçim Yapınız!", Toast.LENGTH_SHORT).show()
                        }
                    }.addOnFailureListener {
                        Toast.makeText(activity, it.localizedMessage, Toast.LENGTH_LONG).show()
                    }
                }
                else if(response.header("status").equals("404 Not Found")){

                    val request = Request.Builder().url("http://18.220.33.203/barcode/find?barcode=" + barcode).build()

                    val client1 = OkHttpClient.Builder()
                        .connectTimeout(60, TimeUnit.SECONDS)
                        .writeTimeout(60, TimeUnit.SECONDS)
                        .readTimeout(60, TimeUnit.SECONDS)
                        .build()

                    client1.newCall(request).enqueue(object : Callback {
                        override fun onFailure(call: Call, e: IOException)  {}
                        override fun onResponse(call: Call, response: Response) {


                            var product = response.body()?.string()!!.toString()

                            if (!product.contains("Error")) {

                                val json = JSONObject(product)

                                var ingredients = json.getString("Ingredients").lowercase()
                                var url = json.getString("image_link")
                                var productName = json.getString("Product")
                                var allergens = json.getString("Allergens").lowercase()


                                val userRef = firestore.collection("Products")

                                userRef.document(barcode).get().addOnSuccessListener {

                                    if (auth.currentUser != null) {

                                        val productMap = hashMapOf<String, Any>()

                                        productMap.put("userEmail", auth.currentUser!!.email!!)
                                        productMap.put("barcode", barcode)
                                        productMap.put("productName", productName)
                                        productMap.put("productImage", url)

                                        if ((ingredients.contains("süt") || ingredients.contains("krema")
                                                    || ingredients.contains("yoğurt") || ingredients.contains(
                                                "peynir"
                                            )
                                                    || ingredients.contains("tereyağı") || ingredients.contains(
                                                "kazein"
                                            )
                                                    || ingredients.contains("laktoz") || ingredients.contains(
                                                "süt tozu"
                                            )) && surveyData.sut
                                        )
                                            productMap.put("sut", true)
                                        else
                                            productMap.put("sut", false)
                                        if ((ingredients.contains("yumurta") || ingredients.contains(
                                                "albümin"
                                            )
                                                    || ingredients.contains("globülin") || ingredients.contains(
                                                "livetin"
                                            )
                                                    || ingredients.contains("ovomucin") || ingredients.contains(
                                                "ovomucoid"
                                            )
                                                    || ingredients.contains("ovovitellin")) && surveyData.yumurta
                                        )
                                            productMap.put("yumurta", true)
                                        else
                                            productMap.put("yumurta", false)
                                        if ((ingredients.contains("bal") || ingredients.contains("arı sütü")
                                                    || ingredients.contains("propolis")) && surveyData.bal
                                        )
                                            productMap.put("bal", true)
                                        else
                                            productMap.put("bal", false)
                                        if (ingredients.contains("tereyağı") && surveyData.tereyagi)
                                            productMap.put("tereyagi", true)
                                        else
                                            productMap.put("tereyagi", false)
                                        if (ingredients.contains("tavuk") && surveyData.tavuk)
                                            productMap.put("tavuk", true)
                                        else
                                            productMap.put("tavuk", false)
                                        if ((ingredients.contains("dana eti") || ingredients.contains(
                                                "kuzu eti"
                                            )) && surveyData.kirmiziEt
                                        )
                                            productMap.put("kirmiziEt", true)
                                        else
                                            productMap.put("kirmiziEt", false)
                                        if ((ingredients.contains("balık") || ingredients.contains("ançuez")
                                                    || ingredients.contains("sardalya") || ingredients.contains(
                                                "ton balığı"
                                            )
                                                    || ingredients.contains("somon") || ingredients.contains(
                                                "uskumru"
                                            )
                                                    || ingredients.contains("hamsi") || ingredients.contains(
                                                "omega-3"
                                            )
                                                    || ingredients.contains("karides") || ingredients.contains(
                                                "ıstakoz"
                                            )
                                                    || ingredients.contains("ahtapot")) && surveyData.deniz
                                        )
                                            productMap.put("deniz", true)
                                        else
                                            productMap.put("deniz", false)
                                        if ((ingredients.contains("domuz") || ingredients.contains("jelatin")) && surveyData.domuz)
                                            productMap.put("domuz", true)
                                        else {
                                            if (ingredients.contains("domuz") && ingredients.contains(
                                                    "içermez"
                                                )
                                            )
                                                productMap.put("domuz", false)
                                            else productMap.put("domuz", false)
                                        }
                                        if ((ingredients.contains("alkol") || ingredients.contains("etanol")
                                                    || ingredients.contains("bira") || ingredients.contains(
                                                "şarap"
                                            )
                                                    || ingredients.contains("likör")) && surveyData.alkol
                                        )
                                            productMap.put("alkol", true)
                                        else {
                                            if (ingredients.contains("alkol") && ingredients.contains(
                                                    "içermez"
                                                )
                                            )
                                                productMap.put("alkol", false)
                                            else productMap.put("alkol", false)
                                        }
                                        if ((ingredients.contains("laktoz") || ingredients.contains(
                                                "süt"
                                            )
                                                    || ingredients.contains("krema") || ingredients.contains(
                                                "lor"
                                            )
                                                    || ingredients.contains("süt tozu") || ingredients.contains(
                                                "peynir altı suyu tozu"
                                            )) && surveyData.laktoz
                                        )
                                            productMap.put("laktoz", true)
                                        else {
                                            if (ingredients.contains("laktoz") || ingredients.contains(
                                                    "içermez"
                                                )
                                            )
                                                productMap.put("laktoz", false)
                                            else productMap.put("laktoz", false)
                                        }
                                        if ((ingredients.contains("gluten") || ingredients.contains(
                                                "buğday"
                                            )
                                                    || ingredients.contains("arpa") || ingredients.contains(
                                                "pirinç"
                                            )
                                                    || ingredients.contains("yulaf") || ingredients.contains(
                                                "malt"
                                            )
                                                    || ingredients.contains("bira mayası")) && surveyData.gluten
                                        )
                                            productMap.put("gluten", true)
                                        else {
                                            if (ingredients.contains("gluten") || ingredients.contains(
                                                    "içermez"
                                                )
                                            )
                                                productMap.put("gluten", false)
                                            else productMap.put("gluten", false)
                                        }
                                        if ((ingredients.contains("yer fıstığı") || ingredients.contains(
                                                "fıstık ezmesi"
                                            )
                                                    || ingredients.contains("yer fıstığı yağı")) && surveyData.fistik
                                        )
                                            productMap.put("fistik", true)
                                        else {
                                            if (ingredients.contains("yer fıstığı") || ingredients.contains(
                                                    "içermez"
                                                )
                                            )
                                                productMap.put("fistik", false)
                                            else productMap.put("fistik", false)
                                        }
                                        if ((ingredients.contains("soya") || ingredients.contains("tofu")
                                                    || ingredients.contains("tempeh") || ingredients.contains(
                                                "miso"
                                            )
                                                    || ingredients.contains("natto")) && surveyData.soya
                                        )
                                            productMap.put("soya", true)
                                        else {
                                            if (ingredients.contains("soya") || ingredients.contains(
                                                    "içermez"
                                                )
                                            )
                                                productMap.put("soya", false)
                                            else productMap.put("soya", false)
                                        }
                                        if (ingredients.contains("mısır") && surveyData.misir)
                                            productMap.put("misir", true)
                                        else
                                            productMap.put("misir", false)

                                        userRef.document(barcode)
                                            .set(productMap).addOnSuccessListener {
                                                openFragment(ProductDataFragment(), barcode)
                                            }.addOnFailureListener {
                                                Toast.makeText(
                                                    activity,
                                                    it.localizedMessage,
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }
                                    } else {
                                        Toast.makeText(
                                            activity,
                                            "Lütfen Seçim Yapınız!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }.addOnFailureListener {
                                    Toast.makeText(activity, it.localizedMessage, Toast.LENGTH_LONG)
                                        .show()
                                }
                            }
                            else{
                                openFragment(ProductDataFragment(),"not found")
                            }
                        }
                    })
                }
            }
        })
    }
}