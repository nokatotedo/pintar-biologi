package com.upiyptk.pintarbiologi.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.*
import com.upiyptk.pintarbiologi.R
import com.upiyptk.pintarbiologi.adapter.StudentRankAdapter
import com.upiyptk.pintarbiologi.data.StudentClassnameData
import com.upiyptk.pintarbiologi.data.StudentRankData

class MainActivity: AppCompatActivity() {
    companion object {
        const val EXTRA_LOGIN = "extra_login"
    }
    private lateinit var btnStudent: ImageView
    private lateinit var actvStudentClassname: AutoCompleteTextView
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
    private var arrayStudentRank: ArrayList<StudentRankData> = arrayListOf()
    private var arrayStudentClassname: ArrayList<String> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnStudent = findViewById(R.id.button_student)
        actvStudentClassname = findViewById(R.id.actv_student_classname)
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

        btnStudent.setOnClickListener {
            Intent(this@MainActivity, SchoolActivity::class.java).also {
                it.putExtra(DetailsStudentActivity.EXTRA_LOGIN, intent.getIntExtra(EXTRA_LOGIN, 0))
                startActivity(it)
            }
        }

        ref = FirebaseDatabase.getInstance().reference
        getClassname()
        getStudentRank("Semua Kelas")
        actvStudentClassname.setOnItemClickListener { _, _, _, _ ->
            arrayStudentRank.clear()
            getStudentRank(actvStudentClassname.text.toString())
        }
    }

    private fun getClassname() {
        ref.child("studentClassname")
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    arrayStudentClassname.clear()
                    arrayStudentClassname.add("Semua Kelas")
                    if(snapshot.exists()) {
                        for(classname in snapshot.children) {
                            val classnameValue = classname.getValue(StudentClassnameData::class.java)
                            if(classnameValue != null) {
                                arrayStudentClassname.add(classnameValue.classname.toString().replace("\"", ""))
                            }
                        }
                        val classname = arrayStudentClassname.toTypedArray()
                        val adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_list_item_1, classname)
                        actvStudentClassname.setAdapter(adapter)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@MainActivity, "Error", Toast.LENGTH_LONG).show()
                }
            })
    }

    private fun getStudentTop(
        list: ArrayList<StudentRankData>,
        rank: Int,
        image: ImageView,
        nickname: TextView,
        result: TextView
    ) {
        list.sortWith(compareBy({ -it.result!! }, { it.time }, { it.number }))
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

        image.setOnClickListener {
            getStudentDetails(StudentRankData(
                positionList.number,
                positionList.rfid,
                positionList.image,
                positionList.name,
                positionList.nickname,
                positionList.gender,
                positionList.classname,
                positionList.result,
                positionList.time
            ))
        }
    }

    private fun getStudentRank(classname: String) {
        ref.child("studentResult").addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                arrayStudentRank.clear()
                if(snapshot.exists()) {
                    for(result in snapshot.children) {
                        val resultValue = result.getValue(StudentRankData::class.java)
                        val numberVal = resultValue!!.number
                        val rfidVal = resultValue.rfid
                        val resultVal = resultValue.result
                        val timeVal = resultValue.time
                        ref.child("student").orderByChild("rfid").equalTo("$rfidVal")
                            .addListenerForSingleValueEvent(object: ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    if(snapshot.exists()) {
                                        for (student in snapshot.children) {
                                            val studentValue =
                                                student.getValue(StudentRankData::class.java)
                                            val imageVal = studentValue!!.image
                                            val nameVal = studentValue.name
                                            val nicknameVal = studentValue.nickname
                                            val genderVal = studentValue.gender
                                            val classnameVal = studentValue.classname
                                            val data = StudentRankData(
                                                numberVal,
                                                rfidVal,
                                                imageVal,
                                                nameVal,
                                                nicknameVal,
                                                genderVal,
                                                classnameVal,
                                                resultVal,
                                                timeVal
                                            )
                                            if(classname == "Semua Kelas") {
                                                arrayStudentRank.add(data)
                                            } else if(classname == classnameVal) {
                                                arrayStudentRank.add(data)
                                            }
                                        }
                                        if(arrayStudentRank.size == 0) {
                                            ivStudentImageOne.visibility = View.INVISIBLE
                                            tvStudentNicknameOne.visibility = View.INVISIBLE
                                            tvStudentResultOne.visibility = View.INVISIBLE
                                            ivStudentImageTwo.visibility = View.INVISIBLE
                                            tvStudentNicknameTwo.visibility = View.INVISIBLE
                                            tvStudentResultTwo.visibility = View.INVISIBLE
                                            ivStudentImageThree.visibility = View.INVISIBLE
                                            tvStudentNicknameThree.visibility = View.INVISIBLE
                                            tvStudentResultThree.visibility = View.INVISIBLE
                                        }
                                        if(arrayStudentRank.size > 0) {
                                            getStudentTop(arrayStudentRank, 1, ivStudentImageOne,
                                                tvStudentNicknameOne, tvStudentResultOne)
                                            ivStudentImageTwo.visibility = View.INVISIBLE
                                            tvStudentNicknameTwo.visibility = View.INVISIBLE
                                            tvStudentResultTwo.visibility = View.INVISIBLE
                                            ivStudentImageThree.visibility = View.INVISIBLE
                                            tvStudentNicknameThree.visibility = View.INVISIBLE
                                            tvStudentResultThree.visibility = View.INVISIBLE
                                        }
                                        if(arrayStudentRank.size > 1) {
                                            getStudentTop(arrayStudentRank, 2, ivStudentImageTwo,
                                                tvStudentNicknameTwo, tvStudentResultTwo)
                                            ivStudentImageThree.visibility = View.INVISIBLE
                                            tvStudentNicknameThree.visibility = View.INVISIBLE
                                            tvStudentResultThree.visibility = View.INVISIBLE
                                        }
                                        if(arrayStudentRank.size > 2) {
                                            getStudentTop(arrayStudentRank, 3, ivStudentImageThree,
                                                tvStudentNicknameThree, tvStudentResultThree)
                                        }
                                        if(arrayStudentRank.size > 3) {
                                            tvStudentRank.visibility = View.INVISIBLE
                                        }
                                        val adapter = StudentRankAdapter(arrayStudentRank)
                                        rvStudentRank.adapter = adapter
                                        adapter.setOnItemClickCallback(object: StudentRankAdapter.OnItemClickCallback {
                                            override fun onItemClicked(student: StudentRankData) {
                                                getStudentDetails(student)
                                            }
                                        })
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

    private fun getStudentDetails(student: StudentRankData) {
        Intent(this@MainActivity, DetailsStudentActivity::class.java).also {
            it.putExtra(DetailsStudentActivity.EXTRA_RFID, student.rfid)
            it.putExtra(DetailsStudentActivity.EXTRA_IMAGE, student.image)
            it.putExtra(DetailsStudentActivity.EXTRA_NAME, student.name)
            it.putExtra(DetailsStudentActivity.EXTRA_GENDER, student.gender)
            it.putExtra(DetailsStudentActivity.EXTRA_CLASSNAME, student.classname)
            it.putExtra(DetailsStudentActivity.EXTRA_ACTIVITY, "0")
            it.putExtra(DetailsStudentActivity.EXTRA_LOGIN, intent.getIntExtra(EXTRA_LOGIN, 0))
            startActivity(it)
        }
    }
}