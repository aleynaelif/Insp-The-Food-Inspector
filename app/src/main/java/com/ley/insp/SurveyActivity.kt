package com.ley.insp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.ley.insp.databinding.ActivityProfileBinding
import com.ley.insp.databinding.ActivitySurveyBinding

class SurveyActivity : AppCompatActivity() {
    private lateinit var binding : ActivitySurveyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySurveyBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    fun secimleriKaydet(view: View){


        val intent = Intent(this, HomepageActivity::class.java)
        startActivity(intent)
        finish()
    }


}