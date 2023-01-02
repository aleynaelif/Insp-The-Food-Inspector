package com.ley.insp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import com.ley.insp.databinding.FragmentScanBinding
import okhttp3.*
import java.io.IOException


class ScanFragment : Fragment() {
    private var _binding: FragmentScanBinding? = null
    private val binding get() = _binding!!

    val client = OkHttpClient()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        if (result.contents == null) {
            Toast.makeText(activity, "Cancelled", Toast.LENGTH_LONG).show()
        } else {

//nutella 59032823

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

                        Log.d("TAGDENEME",product)

                        //teker teker tarama
                        println("Server: ${response.header("allergen_contains_fish")}")
                    }

                    else{

                        val MyRequest = Request.Builder()
                            .url("http://18.220.33.203/barcode/find?barcode=" + result.contents)
                            .build()


                        client.newCall(MyRequest).enqueue(object : Callback {
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
            //  Toast.makeText(activity, "Scanned: " + result.contents, Toast.LENGTH_LONG).show()
        }
    }

    // Launch
    fun onButtonClick(view: View?) {
        barcodeLauncher.launch(ScanOptions())
    }
}