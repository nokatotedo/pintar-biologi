package com.upiyptk.pintarbiologi.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.upiyptk.pintarbiologi.R
import com.upiyptk.pintarbiologi.data.StudentResultData

class StudentResultAdapter(private var list: ArrayList<StudentResultData>): RecyclerView.Adapter<StudentResultAdapter.StudentResultAdapterViewHolder>() {
    private lateinit var onItemClickCallback: OnItemClickCallback

    inner class StudentResultAdapterViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val tvStudentNumber: TextView = itemView.findViewById(R.id.tv_student_number)
        val tvStudentRank: TextView = itemView.findViewById(R.id.tv_student_rank)
        val tvStudentResult: TextView = itemView.findViewById(R.id.tv_student_result)
        val tvStudentTime: TextView = itemView.findViewById(R.id.tv_student_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentResultAdapterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_result, parent, false)
        return StudentResultAdapterViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentResultAdapterViewHolder, position: Int) {
        list.sortBy { it.number }
        val positionList = list[position]
        val studentNumber = "${position+1}."
        val studentRank = "#${positionList.rank}"
        val studentResult = positionList.result
        val studentTime = "${positionList.time}s"

        holder.tvStudentNumber.text = studentNumber
        holder.tvStudentRank.text = studentRank
        holder.tvStudentResult.text = studentResult.toString()
        holder.tvStudentTime.text = studentTime

        holder.itemView.setOnClickListener { onItemClickCallback.onItemClicked(list[position]) }
    }

    override fun getItemCount(): Int = list.size

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onItemClicked(result: StudentResultData)
    }
}