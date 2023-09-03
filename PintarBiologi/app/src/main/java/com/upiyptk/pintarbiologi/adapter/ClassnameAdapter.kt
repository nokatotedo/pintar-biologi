package com.upiyptk.pintarbiologi.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.upiyptk.pintarbiologi.R
import com.upiyptk.pintarbiologi.data.StudentClassnameData

class ClassnameAdapter(private var list: ArrayList<StudentClassnameData>): RecyclerView.Adapter<ClassnameAdapter.ClassnameAdapterViewHolder>() {
    private lateinit var onItemClickCallback: OnItemClickCallback

    inner class ClassnameAdapterViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val tvClassname: TextView = itemView.findViewById(R.id.tv_classname)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClassnameAdapterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_classname, parent, false)
        return ClassnameAdapterViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClassnameAdapterViewHolder, position: Int) {
        val positionList = list[position]

        val classname = positionList.classname

        holder.tvClassname.text = classname

        holder.itemView.setOnClickListener { onItemClickCallback.onItemClicked(list[position]) }
    }

    override fun getItemCount(): Int = list.size

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onItemClicked(classname: StudentClassnameData)
    }
}