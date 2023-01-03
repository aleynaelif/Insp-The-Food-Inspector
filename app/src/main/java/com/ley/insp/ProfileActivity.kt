package com.ley.insp


import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.ley.insp.databinding.ActivityProfileBinding
import java.io.ByteArrayOutputStream


class ProfileActivity : AppCompatActivity() {

    private lateinit var binding :ActivityProfileBinding
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher : ActivityResultLauncher<String>
    var selectedBitmap : Bitmap? = null
    private lateinit var database : SQLiteDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        database = this.openOrCreateDatabase("Profile", MODE_PRIVATE,null)

        val mCursor: Cursor = database.rawQuery("SELECT * FROM profile", null)


        val intent = intent
        val info = intent.getStringExtra("info")


        if (mCursor.moveToFirst() && !info.equals("updateProfile")) {
            val intent = Intent(this,SurveyActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            registerLauncher()
        }
    }

    fun profilePhoto (view: View){

        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,android.Manifest.permission.READ_EXTERNAL_STORAGE)){
                Snackbar.make(view,"Galeriye erişim izni gerekli!",Snackbar.LENGTH_INDEFINITE).setAction("İzin verin",View.OnClickListener {
                    //request permission
                    permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)

                }).show()
            }
            else{
                //request permission
                permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
        else{
            val intentToGallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            //intent
            activityResultLauncher.launch(intentToGallery)

        }
    }

    fun save (view: View){
        val Name = binding.name.text.toString()
        val Age = binding.age.text.toString()

        //SQLitea görsel kaydetmeden önce küçültmen lazım 1mbı aşan row oluşturuşamaz

        if(selectedBitmap != null){
            val smallBitmap = makeSmallerBitmap(selectedBitmap!!,300)
            //görsel byte olarak kaydedilir

            val outPutStream = ByteArrayOutputStream()
            smallBitmap.compress(Bitmap.CompressFormat.PNG,50,outPutStream)
            val byteArray = outPutStream.toByteArray()


            try{

                database.execSQL("CREATE TABLE IF NOT EXISTS profile (id INTEGER PRIMARY KEY, name VARCHAR, age VARCHAR, image BLOB)")

                val sqlString = "INSERT INTO profile (name,age, image) VALUES (?,?,?)"
                val statement = database.compileStatement(sqlString)

                statement.bindString(1, Name)
                statement.bindString(2, Age)
                statement.bindBlob(3,byteArray)
                statement.execute()
            }
            catch(e:Exception){
                e.printStackTrace()
            }

            val intent = Intent(this,SurveyActivity::class.java)
            //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
        }

    }

    private fun makeSmallerBitmap(image: Bitmap, maximumSize : Int): Bitmap{
        var width = image.width
        var height = image.height

        val bitmapRatio : Double = width.toDouble() / height.toDouble()

        if(bitmapRatio >1){
            width = maximumSize
            val scaledHeight = width / bitmapRatio
            height = scaledHeight.toInt()
        }
        else{
            height = maximumSize
            val scaledWidth = height * bitmapRatio
            width = scaledWidth.toInt()
        }
        return Bitmap.createScaledBitmap(image,width,height,true)
    }

    private fun registerLauncher() {
        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val intentFromResult = result.data
                    if (intentFromResult != null) {
                        val imageData = intentFromResult.data
                        //binding.imageView.setImageURI(imageData)

                        if (imageData != null) {
                            try {
                                if (Build.VERSION.SDK_INT >= 28) {
                                    val source = ImageDecoder.createSource(this.contentResolver, imageData)
                                    selectedBitmap = ImageDecoder.decodeBitmap(source)
                                    binding.imageView.setImageBitmap(selectedBitmap)
                                } else {
                                    //version 28den küçükse
                                    selectedBitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageData)
                                    binding.imageView.setImageBitmap(selectedBitmap)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            }

        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
                if (result) {
                    //permision granted
                    val intentToGallery =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    activityResultLauncher.launch(intentToGallery)
                } else {
                    //permission denied
                    Toast.makeText(this, "İzin Gerekli!", Toast.LENGTH_LONG)
                        .show()
                }
            }
    }
}