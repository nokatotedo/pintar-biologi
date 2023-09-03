package com.upiyptk.pintarbiologi.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.upiyptk.pintarbiologi.data.StudentRankData
import com.upiyptk.pintarbiologi.R

class StudentRankAdapter(private var list: ArrayList<StudentRankData>): RecyclerView.Adapter<StudentRankAdapter.StudentRankAdapterViewHolder>() {
    private lateinit var onItemClickCallback: OnItemClickCallback

    inner class StudentRankAdapterViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val ivStudentImage: ImageView = itemView.findViewById(R.id.iv_student_image)
        val tvStudentNickname: TextView = itemView.findViewById(R.id.tv_student_nickname)
        val tvStudentRank: TextView = itemView.findViewById(R.id.tv_student_rank)
        val tvStudentResult: TextView = itemView.findViewById(R.id.tv_student_result)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentRankAdapterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_rank, parent, false)
        return StudentRankAdapterViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentRankAdapterViewHolder, position: Int) {
        list.sortWith(compareBy({ -it.result!! }, { it.time }, { it.number }))
        val positionList = list[position+3]

        val studentImage = when(positionList.image) {
            1 -> R.drawable.student_one
            2 -> R.drawable.student_two
            3 -> R.drawable.student_three
            4 -> R.drawable.student_four
            5 -> R.drawable.student_five
            6 -> R.drawable.student_six
            else -> when(positionList.gender == 0) {
                true -> R.drawable.student_one
                false -> R.drawable.student_four
            }
        }
        var studentNickname = positionList.nickname
        val listTemp = list
        listTemp.sortWith(compareBy({ it.rfid }, { it.number }))
        val positionListTemp = listTemp.withIndex().filter { it.value.rfid == positionList.rfid }.map { it.index }
        for(i in positionListTemp.indices) {
            if(positionList.number == listTemp[positionListTemp[i]].number) {
                val count = list.count { it.rfid == positionList.rfid }
                if(count > 1) { studentNickname = positionList.nickname + " (${i+1})" }
            }
        }
        val studentResult = positionList.result

        Glide.with(holder.itemView.context)
            .load(studentImage)
            .into(holder.ivStudentImage)
        holder.tvStudentNickname.text = studentNickname
        holder.tvStudentRank.text = "#${position+4}"
        holder.tvStudentResult.text = studentResult.toString()

        holder.itemView.setOnClickListener { onItemClickCallback.onItemClicked(list[position]) }
    }

    override fun getItemCount(): Int = list.size - 3

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onItemClicked(student: StudentRankData)
    }
}