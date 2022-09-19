package com.example.memorybookkotlin

import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Adapter
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.memorybookkotlin.Adapter.RecylerAdapter
import com.example.memorybookkotlin.Model.Memory
import com.example.memorybookkotlin.databinding.ActivityAddImageBinding
import com.example.memorybookkotlin.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private lateinit var arrList : ArrayList<Memory>
    private lateinit var adapter : RecylerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)



        arrList = ArrayList<Memory>()
        adapter = RecylerAdapter(arrList)

        binding.recycler.layoutManager = LinearLayoutManager(this)
        binding.recycler.adapter = adapter



        try{
            var database = this.openOrCreateDatabase("Memories", MODE_PRIVATE,null)
            var cursor = database.rawQuery("SELECT*FROM memory",null)

            var titleIx = cursor.getColumnIndex("title")
            var idIx = cursor.getColumnIndex("id")

            while (cursor.moveToNext()){
                var title = cursor.getString(titleIx)
                var id = cursor.getInt(idIx)

                var memory = Memory(title,id)
                arrList.add(memory)
            }
            adapter.notifyDataSetChanged()
            cursor.close()

        } catch(e : Exception){
            e.printStackTrace()
        }


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = getMenuInflater()
        menuInflater.inflate(R.menu.option_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.memory){
           val intent = Intent(this@MainActivity,AddImage::class.java)
            intent.putExtra("info","new")
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }


}