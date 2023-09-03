package com.upiyptk.pintarbiologi.activity

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.*
import com.upiyptk.pintarbiologi.R
import com.upiyptk.pintarbiologi.adapter.StudentResultAdapter
import com.upiyptk.pintarbiologi.data.StudentResultData
import com.upiyptk.pintarbiologi.function.Pack

class DetailsStudentActivity: AppCompatActivity() {
    companion object {
        const val EXTRA_RFID = "extra_rfid"
        const val EXTRA_IMAGE = "extra_image"
        const val EXTRA_NAME = "extra_name"
        const val EXTRA_GENDER = "extra_gender"
        const val EXTRA_CLASSNAME = "extra_classname"
        const val EXTRA_ACTIVITY = "extra_activity"
        const val EXTRA_LOGIN = "extra_login"
    }

    private lateinit var btnClose: ImageView
    private lateinit var btnDelete: ImageView
    private lateinit var ivStudentImage: ImageView
    private lateinit var tvStudentName: TextView
    private lateinit var tvStudentClassname: TextView
    private lateinit var tvStudentResult: TextView
    private lateinit var rvStudentResult: RecyclerView
    private lateinit var ref: DatabaseReference
    private var arrayStudentResult: ArrayList<StudentResultData> = arrayListOf()
    private var login: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details_student)

        btnClose = findViewById(R.id.button_close)
        btnDelete = findViewById(R.id.button_delete)
        ivStudentImage = findViewById(R.id.iv_student_image)
        tvStudentName = findViewById(R.id.tv_student_name)
        tvStudentClassname = findViewById(R.id.tv_student_classname)
        tvStudentResult = findViewById(R.id.tv_student_result)
        rvStudentResult = findViewById(R.id.rv_student_result)
        rvStudentResult.layoutManager = LinearLayoutManager(this)
        rvStudentResult.setHasFixedSize(true)

        login = intent.getIntExtra(EXTRA_LOGIN, 0)
        val rfid = intent.getStringExtra(EXTRA_RFID)
        val image = when(intent.getIntExtra(EXTRA_IMAGE, 0)) {
            1 -> R.drawable.student_one
            2 -> R.drawable.student_two
            3 -> R.drawable.student_three
            4 -> R.drawable.student_four
            5 -> R.drawable.student_five
            6 -> R.drawable.student_six
            else -> when(intent.getIntExtra(EXTRA_GENDER, 0) == 0) {
                true -> R.drawable.student_one
                false -> R.drawable.student_four
            }
        }
        val name = intent.getStringExtra(EXTRA_NAME)
        val classname = intent.getStringExtra(EXTRA_CLASSNAME)

        if(login == 1) {
            btnDelete.visibility = View.VISIBLE
        } else {
            btnDelete.visibility = View.INVISIBLE
        }

        Glide.with(this)
            .load(image)
            .into(ivStudentImage)
        tvStudentName.text = name
        tvStudentClassname.text = classname

        ref = FirebaseDatabase.getInstance().reference
        ref.child("studentResult")
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    arrayStudentResult.clear()
                    if(snapshot.exists()) {
                        for(result in snapshot.children) {
                            val resultValue = result.getValue(StudentResultData::class.java)
                            if(resultValue != null) {
                                arrayStudentResult.add(resultValue)
                            }
                        }
                        arrayStudentResult.sortWith(compareBy({ -it.result!! }, { it.time }, { it.number }))
                        val indexList = arrayStudentResult.withIndex().filter { it.value.rfid == rfid }.map { it.index }
                        for(i in indexList.indices) {
                            arrayStudentResult[indexList[i]].rank = indexList[i]+1
                        }
                        arrayStudentResult.removeIf { it.rank == null }
                        val adapter = StudentResultAdapter(arrayStudentResult)
                        rvStudentResult.adapter = adapter
                        if(arrayStudentResult.size == 0) {
                            tvStudentResult.visibility = View.VISIBLE
                        } else {
                            tvStudentResult.visibility = View.INVISIBLE
                        }
                        adapter.setOnItemClickCallback(object: StudentResultAdapter.OnItemClickCallback {
                            override fun onItemClicked(result: StudentResultData) {
                                if(login == 1) {
                                    if(!Pack.isOnline(this@DetailsStudentActivity)) {
                                        Toast.makeText(this@DetailsStudentActivity, "Kesalahan Jaringan", Toast.LENGTH_LONG).show()
                                        return
                                    }

                                    val builder = AlertDialog.Builder(this@DetailsStudentActivity)
                                    builder.setTitle("Hapus Data Hasil")
                                    builder.setMessage("Yakin Hapus?")
                                    builder.setPositiveButton("Ya") { _, _ ->
                                        ref.child("studentResult").orderByChild("number").equalTo(result.number!!.toDouble())
                                            .addListenerForSingleValueEvent(object: ValueEventListener {
                                                override fun onDataChange(snapshot: DataSnapshot) {
                                                    if(snapshot.exists()) {
                                                        for(student in snapshot.children) {
                                                            student.ref.removeValue()
                                                        }
                                                    }
                                                }

                                                override fun onCancelled(error: DatabaseError) {
                                                    Toast.makeText(this@DetailsStudentActivity, "Error", Toast.LENGTH_LONG).show()
                                                }
                                            })
                                    }

                                    builder.setNegativeButton("Tidak") { _, _ ->
                                        return@setNegativeButton
                                    }

                                    val alert: AlertDialog = builder.create()
                                    alert.show()
                                    alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#AA7E6A"))
                                    alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#452A25"))
                                }
                            }
                        })
                    } else {
                        val adapter = StudentResultAdapter(arrayStudentResult)
                        rvStudentResult.adapter = adapter
                        tvStudentResult.visibility = View.VISIBLE
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@DetailsStudentActivity, "Error", Toast.LENGTH_LONG).show()
                }
            })

        btnClose.setOnClickListener {
            backIntent()
        }

        btnDelete.setOnClickListener {
            if(!Pack.isOnline(this)) {
                Toast.makeText(this@DetailsStudentActivity, "Kesalahan Jaringan", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val builder = AlertDialog.Builder(this)
            builder.setTitle("Hapus Data Siswa")
            builder.setMessage("Data Siswa dan Riwayat Siswa akan Dihapus. Yakin Hapus?")
            builder.setPositiveButton("Ya") { _, _ ->
                val rfidValue = rfid.toString().replace("\"", "").replace("\\p{Zs}+".toRegex(), "").take(8)
                ref.child("student").child(rfidValue).removeValue()
                ref.child("studentResult").orderByChild("rfid").equalTo("$rfid")
                    .addListenerForSingleValueEvent(object: ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if(snapshot.exists()) {
                                for(student in snapshot.children) {
                                    student.ref.removeValue()
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(this@DetailsStudentActivity, "Error", Toast.LENGTH_LONG).show()
                        }
                    })
                backIntent()
            }

            builder.setNegativeButton("Tidak") { _, _ ->
                return@setNegativeButton
            }

            val alert: AlertDialog = builder.create()
            alert.show()
            alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#AA7E6A"))
            alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#452A25"))
        }
    }

    private fun backIntent() {
        if(intent.getStringExtra(EXTRA_ACTIVITY) == "0") {
            Intent(this@DetailsStudentActivity, MainActivity::class.java).also {
                if(login == 1) {
                    it.putExtra(MainActivity.EXTRA_LOGIN, 1)
                } else {
                    it.putExtra(MainActivity.EXTRA_LOGIN, 0)
                }
                startActivity(it)
            }
        } else {
            Intent(this@DetailsStudentActivity, SchoolActivity::class.java).also {
                if(login == 1) {
                    it.putExtra(SchoolActivity.EXTRA_LOGIN, 1)
                } else {
                    it.putExtra(SchoolActivity.EXTRA_LOGIN, 0)
                }
                startActivity(it)
            }
        }
    }
}