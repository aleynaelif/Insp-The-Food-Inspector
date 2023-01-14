package com.ley.insp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.ley.insp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private lateinit var auth : FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = Firebase.auth


        val currentUser = auth.currentUser
        if(currentUser != null){
            val intent = Intent(this, HomepageActivity::class.java)
            intent.putExtra("info", "main")
            startActivity(intent)
            finish()
        }
    }

    fun SignIn(view : View){

        val email = binding.EmailTextView.text.toString()
        val password = binding.PasswordTextView.text.toString()

        if(email.equals("") || password.equals("")){
            Toast.makeText(this,"Geçerli bir email ve parola giriniz!", Toast.LENGTH_LONG).show()
        }
        else{
            auth.signInWithEmailAndPassword(email,password).addOnSuccessListener {
                val intent = Intent(this@MainActivity, ProfileActivity::class.java)
                startActivity(intent)
                finish()
            }.addOnFailureListener {
                Toast.makeText(this@MainActivity,it.localizedMessage, Toast.LENGTH_LONG).show()
            }
        }
    }

    fun SignUp(view : View){
        val email = binding.EmailTextView.text.toString()
        val password = binding.PasswordTextView.text.toString()

        if(email.equals("") || password.equals("")){
            Toast.makeText(this,"Geçerli bir email ve parola giriniz!", Toast.LENGTH_LONG).show()
        }
        else{
            auth.createUserWithEmailAndPassword(email,password).addOnSuccessListener {
                val intent = Intent(this@MainActivity, ProfileActivity::class.java)
                startActivity(intent)
                finish()
            }.addOnFailureListener {
                Toast.makeText(this@MainActivity,it.localizedMessage, Toast.LENGTH_LONG).show()
            }
        }

    }
}