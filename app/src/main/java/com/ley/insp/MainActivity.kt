package com.ley.insp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.ley.insp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.bottomNavigationView.isVisible= false
        replaceFragment(AuthenticationFragment())


        /*supportFragmentManager.findFragmentById(R.id.frameLayout)?.let {
            if (it is HomeFragment) {
                binding.bottomNavigationView.isVisible= true
            }
        }*/

    }

    private fun replaceFragment(fragment : Fragment){

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.replace(R.id.frameLayout,fragment,"auth")
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }



}