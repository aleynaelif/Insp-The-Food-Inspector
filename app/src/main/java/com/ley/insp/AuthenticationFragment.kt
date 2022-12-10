package com.ley.insp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.ley.insp.databinding.FragmentAuthenticationBinding

class AuthenticationFragment : Fragment() {

    private var _binding : FragmentAuthenticationBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth : FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth

        //if there is a current user already signed in then keep them signed in.
        val currentUser = auth.currentUser

        if(currentUser != null){
            replaceFragment(HomeFragment())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAuthenticationBinding.inflate(inflater, container, false)

        _binding!!.signUpButton.setOnClickListener {
            val email = binding.EmailTextView.text.toString()
            val password = binding.PasswordTextView.text.toString()

            if(email.isNotEmpty() && password.isNotEmpty()){
                auth.createUserWithEmailAndPassword(email,password).addOnSuccessListener {
                    //success
                    replaceFragment(HomeFragment())
                }.addOnFailureListener {
                    Toast.makeText(this@AuthenticationFragment.requireActivity(),it.localizedMessage, Toast.LENGTH_LONG).show()
                }
            }
            else{
                Toast.makeText(this@AuthenticationFragment.requireActivity(),"Please enter email and password!", Toast.LENGTH_LONG).show()
            }
        }
        _binding!!.signInButton.setOnClickListener {
            val email = binding.EmailTextView.text.toString()
            val password = binding.PasswordTextView.text.toString()

            if(email.equals("") || password.equals("")){
                Toast.makeText(this@AuthenticationFragment.requireActivity(),"Please enter email and password!", Toast.LENGTH_LONG).show()
            }else{
                auth.signInWithEmailAndPassword(email,password).addOnSuccessListener {
                    replaceFragment(HomeFragment())
                }.addOnFailureListener {
                    Toast.makeText(this@AuthenticationFragment.requireActivity(),it.localizedMessage, Toast.LENGTH_LONG).show()
                }
            }
        }
        val view = binding.root
        return view
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun replaceFragment(fragment : Fragment){
        val fragmentManager = getParentFragmentManager()
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout,fragment,"home")
        fragmentTransaction.commit()
    }
}