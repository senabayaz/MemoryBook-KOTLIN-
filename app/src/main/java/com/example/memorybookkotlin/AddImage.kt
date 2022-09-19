package com.example.memorybookkotlin

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.memorybookkotlin.databinding.ActivityAddImageBinding
import com.google.android.material.snackbar.Snackbar
import java.io.ByteArrayOutputStream

class AddImage : AppCompatActivity() {
    private lateinit var activityResult : ActivityResultLauncher<Intent>
    private lateinit var permissionResult : ActivityResultLauncher<String>
    private lateinit var binding : ActivityAddImageBinding
    var bitmap : Bitmap? = null
    private lateinit var database : SQLiteDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddImageBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        database = this.openOrCreateDatabase("Memories", MODE_PRIVATE,null)

        registerLauncher()

        var intent = intent
        var info = intent.getStringExtra("info")
        if(info == "new"){
            binding.editTextTextPersonName.setText("")
            binding.imageView.setImageResource(com.google.android.material.R.drawable.notification_icon_background)
            binding.editTextTextPersonName.setText("")
            binding.button.visibility = View.VISIBLE
        }else{
            binding.button.visibility = View.INVISIBLE
            var id = intent.getIntExtra("id",1)

            var cursor = database.rawQuery("SELECT*FROM memory WHERE id = ?", arrayOf(id.toString()))

            val titleIx = cursor.getColumnIndex("title")
            val imageIx = cursor.getColumnIndex("image")

            while(cursor.moveToNext()){

                binding.editTextTextPersonName.setText(cursor.getString(titleIx))

                val byteArr = cursor.getBlob(imageIx)
                val bitmap = BitmapFactory.decodeByteArray(byteArr,0,byteArr.size)
                binding.imageView.setImageBitmap(bitmap)

            }
            cursor.close()
        }

    }


    public fun clickImg(view : View){

        if(ContextCompat.checkSelfPermission(this@AddImage,android.Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this@AddImage,android.Manifest.permission.READ_EXTERNAL_STORAGE)){
                Snackbar.make(view,"Give Permission For Gallery",Snackbar.LENGTH_INDEFINITE).setAction("Give Permission",View.OnClickListener {
                    permissionResult.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }).show()
            }else{
                permissionResult.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }else{
            val intentGallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            activityResult.launch(intentGallery)
        }

    }

    private fun databaseImg(image : Bitmap , size : Int) : Bitmap{
        var width = image.width
        var height = image.height

        val ratio = width.toDouble() / height.toDouble()

        if(ratio > 1){
            width = size
            val scaleH = height + ratio
            height = scaleH.toInt()
        } else{
            height = size
            val scaleW = width + ratio
            width = scaleW.toInt()
        }

        return Bitmap.createScaledBitmap(image,width,height,true)
    }

    public fun save(view : View){

        var title = binding.editTextTextPersonName.text.toString()
        if(bitmap != null){
            val imgSmall = databaseImg(bitmap!!,300)

            val outputStream = ByteArrayOutputStream()
            imgSmall.compress(Bitmap.CompressFormat.JPEG,50,outputStream)
            val byteArr = outputStream.toByteArray()
            try{
                database = this.openOrCreateDatabase("Memories", MODE_PRIVATE,null)
                database.execSQL("CREATE TABLE IF NOT EXISTS memory (id INTEGER PRIMARY KEY,title VARCHAR,image BLOB)")
                var cursor = database.rawQuery("SELECT*FROM memory",null)

                val stringSql = "INSERT INTO memory (title,image) VALUES (?,?)"
                val statement = database.compileStatement(stringSql)

                statement.bindString(1,title)
                statement.bindBlob(2,byteArr)
                statement.execute()

                var intent = Intent(this@AddImage,MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)

            }catch (e : Exception){
                e.printStackTrace()
            }
        }

    }

    private fun registerLauncher(){
        activityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            if(result.resultCode == RESULT_OK){
                val resultInt = result.data
                if(resultInt != null){
                    val imgData = resultInt.data
                    if(imgData != null){
                        try{
                            if(Build.VERSION.SDK_INT >= 28){
                                var source = ImageDecoder.createSource(this@AddImage.contentResolver,imgData)
                                bitmap = ImageDecoder.decodeBitmap(source)
                                binding.imageView.setImageBitmap(bitmap)
                            } else{
                                bitmap = MediaStore.Images.Media.getBitmap(contentResolver,imgData)
                                binding.imageView.setImageBitmap(bitmap)
                            }
                        } catch (e : Exception){
                            println(e.localizedMessage)
                        }
                    }
                }
            }
        }

        permissionResult = registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
            if(result){
                val intentGallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResult.launch(intentGallery)
            }
        }
    }
}