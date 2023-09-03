package com.upiyptk.pintarbiologi.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.upiyptk.pintarbiologi.R
import com.upiyptk.pintarbiologi.data.StudentRankData

class StudentAdapter(private var list: ArrayList<StudentRankData>): RecyclerView.Adapter<StudentAdapter.StudentAdapterViewHolder>() {
    private lateinit var onItemClickCallback: OnItemClickCallback

    inner class StudentAdapterViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val ivStudentImage: ImageView = itemView.findViewById(R.id.iv_student_image)
        val tvStudentNickname: TextView = itemView.findViewById(R.id.tv_student_nickname)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentAdapterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_student, parent, false)
        return StudentAdapterViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentAdapterViewHolder, position: Int) {
        val positionList = list[position]

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
        val studentNickname = positionList.nickname

        Glide.with(holder.itemView.context)
            .load(studentImage)
            .into(holder.ivStudentImage)
        holder.tvStudentNickname.text = studentNickname

        holder.itemView.setOnClickListener { onItemClickCallback.onItemClicked(list[position]) }
    }

    override fun getItemCount(): Int = list.size

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onItemClicked(student: StudentRankData)
    }
}