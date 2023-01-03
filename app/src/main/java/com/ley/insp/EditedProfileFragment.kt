package com.ley.insp


import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.ley.insp.databinding.FragmentEditedProfileBinding


class EditedProfileFragment : Fragment() {
    private var _binding: FragmentEditedProfileBinding? = null
    private val binding get() = _binding!!

    var profileNameList = ArrayList<String>()
    var profileIdList = ArrayList<Int>()
    var profileAgeList = ArrayList<String>()
    var profileImage = ArrayList<Bitmap>()

    private lateinit var listeAdapter: ProfileAdapter


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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listeAdapter = ProfileAdapter(profileNameList,profileAgeList,profileImage)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = listeAdapter

        sqlVeriAlma()

    }

    fun sqlVeriAlma(){

        try {
            activity?.let {
                val database = it.openOrCreateDatabase("Profile", Context.MODE_PRIVATE,null)

                val cursor = database.rawQuery("SELECT * FROM profile",null)
                val profileName = cursor.getColumnIndex("name")
                val profileId = cursor.getColumnIndex("id")
                val profileAge = cursor.getColumnIndex("age")
                val getPhoto = cursor.getColumnIndex("image")


                profileNameList.clear()
                profileIdList.clear()
                profileAgeList.clear()
                profileImage.clear()


                 cursor.moveToLast()
                    profileNameList.add(cursor.getString(profileName))
                    profileIdList.add(cursor.getInt(profileId))
                    profileAgeList.add(cursor.getString(profileAge))

                    val byteDizisi = cursor.getBlob(getPhoto)
                    val bitmap = BitmapFactory.decodeByteArray(byteDizisi,0,byteDizisi.size)
                    profileImage.add(bitmap)


                listeAdapter.notifyDataSetChanged()
                cursor.close()
            }
        }catch (e : Exception){

        }
    }

}