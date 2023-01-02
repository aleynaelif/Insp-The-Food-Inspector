package com.ley.insp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import androidx.fragment.app.Fragment

import com.ley.insp.databinding.ActivityHomepageBinding

class HomepageActivity : AppCompatActivity() {
    private lateinit var binding : ActivityHomepageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomepageBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        replaceFragment(ScanFragment())

        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId){
                R.id.checkbox -> replaceFragment(ScanFragment())
                //R.id.profile -> replaceFragment(ProfileFragment())
                R.id.history -> replaceFragment(HistoryFragment())

                else ->{

                }
            }
            true
        }

    }


    private fun replaceFragment(fragment : Fragment){

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout,fragment)
        //geri tuşuyla direkt uygulamadan çıkmasını istiyosan aşağıdaki satırı yoruma alabilirsin
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }
}