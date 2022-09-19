package com.example.memorybookkotlin.Adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.memorybookkotlin.AddImage
import com.example.memorybookkotlin.MainActivity
import com.example.memorybookkotlin.Model.Memory
import com.example.memorybookkotlin.databinding.RecyclerRowBinding

class RecylerAdapter : RecyclerView.Adapter<RecylerAdapter.holder> {
    lateinit var arrList : ArrayList<Memory>

    constructor(arrList: ArrayList<Memory>) : super() {
        this.arrList = arrList
    }

    class holder(val binding : RecyclerRowBinding ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): holder {
        val binding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return holder(binding)
    }

    override fun onBindViewHolder(holder: holder, position: Int) {
        holder.binding.textView2.text = arrList.get(position).title
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, AddImage::class.java)
            intent.putExtra("data",arrList.get(position).title)
            intent.putExtra("info","old")
            intent.putExtra("id",arrList.get(position).id)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return arrList.size
    }
}