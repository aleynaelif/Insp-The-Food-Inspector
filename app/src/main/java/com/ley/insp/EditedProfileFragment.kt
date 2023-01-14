package com.ley.insp


import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ley.insp.databinding.FragmentEditedProfileBinding


class EditedProfileFragment : Fragment() {
    private var _binding: FragmentEditedProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore
    private lateinit var surveyData: SurveyData

    var profileNameList = ArrayList<String>()
    var profileIdList = ArrayList<Int>()
    var profileAgeList = ArrayList<String>()
    var profileImage = ArrayList<Bitmap>()


    private lateinit var listeAdapter: ProfileAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth
        db = Firebase.firestore


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

        sqlVeriAlma()
        firebaseVeriAlma()

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

    fun firebaseVeriAlma() {
        db.collection("Choices").addSnapshotListener { value, error ->
            if(error != null){
                Toast.makeText(getActivity(),error.localizedMessage,Toast.LENGTH_LONG).show()
            }
            else{
                if(value != null){
                    if(!value.isEmpty){

                        val documents = value.documents

                        for (document in documents){
                            if(document.id.equals(auth.currentUser!!.uid)){
                                surveyData = SurveyData(
                                    document.get("sut") as? Boolean == true,
                                    document.get("yumurta") as? Boolean == true,
                                    document.get("bal") as? Boolean == true,
                                    document.get("tereyagi") as? Boolean == true,
                                    document.get("tavuk") as? Boolean == true,
                                    document.get("kirmiziEt") as? Boolean == true,
                                    document.get("deniz") as? Boolean == true,
                                    document.get("domuz") as? Boolean == true,
                                    document.get("alkol") as? Boolean == true,
                                    document.get("laktoz") as? Boolean == true,
                                    document.get("gluten") as? Boolean == true,
                                    document.get("fistik") as? Boolean == true,
                                    document.get("soya") as? Boolean == true,
                                    document.get("misir") as? Boolean == true
                                )

                                listeAdapter = ProfileAdapter(profileNameList,profileAgeList,profileImage,surveyData)

                                binding.recyclerView.layoutManager = LinearLayoutManager(context)
                                binding.recyclerView.adapter = listeAdapter
                            }
                        }
                    }
                }
            }
        }
    }
}

