package com.ley.insp

import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.ley.insp.databinding.FragmentEditedProfileBinding


class EditedProfileFragment : Fragment() {
    private var _binding: FragmentEditedProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var database : SQLiteDatabase


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditedProfileBinding.inflate(inflater,container,false)
        val view = binding.root
        return view
    }

    fun getData(view: View){
        //database = this.openOrCreateDatabase("Profile", AppCompatActivity.MODE_PRIVATE,null)





    }
}