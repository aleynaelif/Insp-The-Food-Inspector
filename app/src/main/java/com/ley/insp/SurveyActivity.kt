package com.ley.insp


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.ley.insp.databinding.ActivitySurveyBinding
import java.util.*


class SurveyActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySurveyBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySurveyBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = Firebase.auth
        firestore = Firebase.firestore
        storage = Firebase.storage

        secimleriKaydet(view)
    }

    fun secimleriKaydet(view: View) {

        val kaydet = findViewById<Button>(R.id.saveTheChoices)

        kaydet.setOnClickListener {

        if ((binding.sut.isChecked || binding.yumurta.isChecked || binding.bal.isChecked ||
            binding.tereyagi.isChecked || binding.tavuk.isChecked || binding.kirmiziEt.isChecked ||
            binding.deniz.isChecked || binding.domuz.isChecked || binding.alkol.isChecked ||
            binding.laktoz.isChecked || binding.gluten.isChecked || binding.fistik.isChecked ||
            binding.soya.isChecked || binding.misir.isChecked) == true){

            val user = mapOf("email" to auth.currentUser!!.email)
            val userRef = firestore.collection("Choices")
            userRef.document(auth.currentUser!!.uid).set(user).addOnSuccessListener {

                if(auth.currentUser != null){
                    val choicesMap = hashMapOf<String, Any>()
                    choicesMap.put("userEmail", auth.currentUser!!.email!!)
                    choicesMap.put("sut",binding.sut.isChecked)
                    choicesMap.put("yumurta",binding.yumurta.isChecked)
                    choicesMap.put("bal",binding.bal.isChecked)
                    choicesMap.put("tereyagi",binding.tereyagi.isChecked)
                    choicesMap.put("tavuk",binding.tavuk.isChecked)
                    choicesMap.put("kirmiziEt",binding.kirmiziEt.isChecked)
                    choicesMap.put("deniz",binding.deniz.isChecked)
                    choicesMap.put("domuz",binding.domuz.isChecked)
                    choicesMap.put("alkol",binding.alkol.isChecked)
                    choicesMap.put("laktoz",binding.laktoz.isChecked)
                    choicesMap.put("gluten",binding.gluten.isChecked)
                    choicesMap.put("fistik",binding.fistik.isChecked)
                    choicesMap.put("soya",binding.soya.isChecked)
                    choicesMap.put("misir",binding.misir.isChecked)

                    userRef.document(auth.currentUser!!.uid).set(choicesMap).addOnSuccessListener {
                        val intent = Intent(this, HomepageActivity::class.java)
                        startActivity(intent)
                        finish()
                    }.addOnFailureListener {
                        Toast.makeText(this,it.localizedMessage,Toast.LENGTH_LONG).show()
                    }
                }
                else{ Toast.makeText(applicationContext,"Lütfen Seçim Yapınız!", Toast.LENGTH_SHORT).show() }
            }.addOnFailureListener { Toast.makeText(this,it.localizedMessage,Toast.LENGTH_LONG).show()}
        }
        }
    }
}