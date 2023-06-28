package com.upiyptk.pintarbiologi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.*

class MainActivity: AppCompatActivity() {
    private lateinit var ivStudentImageOne: ImageView
    private lateinit var tvStudentNicknameOne: TextView
    private lateinit var tvStudentResultOne: TextView
    private lateinit var ivStudentImageTwo: ImageView
    private lateinit var tvStudentNicknameTwo: TextView
    private lateinit var tvStudentResultTwo: TextView
    private lateinit var ivStudentImageThree: ImageView
    private lateinit var tvStudentNicknameThree: TextView
    private lateinit var tvStudentResultThree: TextView
    private lateinit var tvStudentRank: TextView
    private lateinit var rvStudentRank: RecyclerView
    private lateinit var ref: DatabaseReference
    private var arrayStudentRank: ArrayList<DataStudentRank> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ivStudentImageOne = findViewById(R.id.iv_student_image_one)
        tvStudentNicknameOne = findViewById(R.id.tv_student_nickname_one)
        tvStudentResultOne = findViewById(R.id.tv_student_result_one)
        ivStudentImageTwo = findViewById(R.id.iv_student_image_two)
        tvStudentNicknameTwo = findViewById(R.id.tv_student_nickname_two)
        tvStudentResultTwo = findViewById(R.id.tv_student_result_two)
        ivStudentImageThree = findViewById(R.id.iv_student_image_three)
        tvStudentNicknameThree = findViewById(R.id.tv_student_nickname_three)
        tvStudentResultThree = findViewById(R.id.tv_student_result_three)
        tvStudentRank = findViewById(R.id.tv_student_rank)
        rvStudentRank = findViewById(R.id.rv_student_rank)
        rvStudentRank.layoutManager = LinearLayoutManager(this)
        rvStudentRank.setHasFixedSize(true)

        ref = FirebaseDatabase.getInstance().reference
        ref.child("studentResult").addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                arrayStudentRank.clear()
                if(snapshot.exists()) {
                    for(result in snapshot.children) {
                        val resultValue = result.getValue(DataStudentRank::class.java)
                        val numberVal = resultValue!!.number
                        val rfidVal = resultValue.rfid
                        val resultVal = resultValue.result
                        ref.child("student").orderByChild("rfid").equalTo(rfidVal)
                            .addListenerForSingleValueEvent(object: ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    if(snapshot.exists()) {
                                        for(student in snapshot.children) {
                                            val studentValue = student.getValue(DataStudentRank::class.java)
                                            val imageVal = studentValue!!.image
                                            val nameVal = studentValue.name
                                            val nicknameVal = studentValue.nickname
                                            val genderVal = studentValue.gender
                                            val data = DataStudentRank(numberVal, rfidVal, imageVal,
                                                nameVal, nicknameVal, genderVal, resultVal)
                                            arrayStudentRank.add(data)
                                        }
                                        if(arrayStudentRank.size > 0) {
                                            getStudentTop(arrayStudentRank, 1, ivStudentImageOne,
                                                tvStudentNicknameOne, tvStudentResultOne)
                                        }
                                        if(arrayStudentRank.size > 1) {
                                            getStudentTop(arrayStudentRank, 2, ivStudentImageTwo,
                                                tvStudentNicknameTwo, tvStudentResultTwo)
                                        }
                                        if(arrayStudentRank.size > 2) {
                                            getStudentTop(arrayStudentRank, 3, ivStudentImageThree,
                                                tvStudentNicknameThree, tvStudentResultThree)
                                        }
                                        if(arrayStudentRank.size > 3) {
                                            tvStudentRank.visibility = View.INVISIBLE
                                        }
                                        val adapter = AdapterStudentRank(arrayStudentRank)
                                        rvStudentRank.adapter = adapter
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    Toast.makeText(this@MainActivity, "Error", Toast.LENGTH_LONG).show()
                                }
                            })
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Error", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun getStudentTop(
        list: ArrayList<DataStudentRank>,
        rank: Int,
        image: ImageView,
        nickname: TextView,
        result: TextView
    ) {
        list.sortWith(compareBy({ -it.result!! }, { it.number }))
        val positionList = list[rank - 1]

        val studentImage = when (positionList.image) {
            1 -> R.drawable.student_one
            2 -> R.drawable.student_two
            3 -> R.drawable.student_three
            4 -> R.drawable.student_four
            5 -> R.drawable.student_five
            6 -> R.drawable.student_six
            else -> when (positionList.gender == 0) {
                true -> R.drawable.student_one
                false -> R.drawable.student_four
            }
        }
        var studentNickname = positionList.nickname
        list.sortWith(compareBy({ it.rfid }, { it.number }))
        val positionListTemp =
            list.withIndex().filter { it.value.rfid == positionList.rfid }.map { it.index }
        for (i in positionListTemp.indices) {
            if (positionList.number == list[positionListTemp[i]].number) {
                val count = list.count { it.rfid == positionList.rfid }
                if (count > 1) {
                    studentNickname = positionList.nickname + " (${i + 1})"
                }
            }
        }
        val studentResult = positionList.result

        image.visibility = View.VISIBLE
        nickname.visibility = View.VISIBLE
        result.visibility = View.VISIBLE
        Glide.with(this)
            .load(studentImage)
            .into(image)
        nickname.text = studentNickname
        result.text = studentResult.toString()
    }
}