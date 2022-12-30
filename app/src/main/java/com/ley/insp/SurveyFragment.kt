package com.ley.insp

import android.net.Uri.encode
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
import com.ley.insp.databinding.FragmentSurveyBinding
import okhttp3.*
import java.io.IOException
import java.net.URLDecoder
import java.net.URLEncoder
import java.net.URLEncoder.encode
import java.util.concurrent.TimeUnit


class SurveyFragment : Fragment() {

    private var _binding: FragmentSurveyBinding? = null
    private val binding get() = _binding!!
    val client = OkHttpClient()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSurveyBinding.inflate(inflater, container, false)

        _binding!!.scan.setOnClickListener {
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

                    var product = response.body()?.string()!!.toString()

                    Log.d("TAGDENEME",product)

                    //teker teker tarama
                    println("Server: ${response.header("allergen_contains_fish")}")

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