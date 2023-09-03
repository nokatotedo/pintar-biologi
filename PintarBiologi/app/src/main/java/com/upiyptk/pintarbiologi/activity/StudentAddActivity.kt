package com.upiyptk.pintarbiologi.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import com.google.firebase.database.*
import com.upiyptk.pintarbiologi.R
import com.upiyptk.pintarbiologi.data.StudentClassnameData

class StudentAddActivity: AppCompatActivity() {
    private lateinit var btnClose: ImageView
    private lateinit var tvStudentRFID: TextView
    private lateinit var etStudentName: EditText
    private lateinit var etStudentNickname: EditText
    private lateinit var actvStudentGender: AutoCompleteTextView
    private lateinit var actvStudentClassname: AutoCompleteTextView
    private lateinit var btnSave: AppCompatButton
    private lateinit var ref: DatabaseReference
    private var arrayStudentGender: ArrayList<String> = arrayListOf()
    private var arrayStudentClassname: ArrayList<String> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_add)

        btnClose = findViewById(R.id.button_close)
        tvStudentRFID = findViewById(R.id.tv_student_rfid)
        etStudentName = findViewById(R.id.et_student_name)
        etStudentNickname = findViewById(R.id.et_student_nickname)
        actvStudentGender = findViewById(R.id.actv_student_gender)
        actvStudentClassname = findViewById(R.id.actv_student_classname)
        btnSave = findViewById(R.id.button_save)
        ref = FirebaseDatabase.getInstance().reference

        btnClose.setOnClickListener {
            Intent(this@StudentAddActivity, SchoolActivity::class.java).also {
                it.putExtra(SchoolActivity.EXTRA_LOGIN, 1)
                startActivity(it)
            }
        }

        ref.child("lastValue").child("rfid")
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    tvStudentRFID.text = "Error"
                    if(snapshot.exists()) {
                        tvStudentRFID.text = snapshot.value.toString()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@StudentAddActivity, "Error", Toast.LENGTH_LONG).show()
                }
            })

        arrayStudentGender.add("Laki-laki")
        arrayStudentGender.add("Perempuan")
        val gender = arrayStudentGender.toTypedArray()
        val adapterGender = ArrayAdapter(this@StudentAddActivity, android.R.layout.simple_list_item_1, gender)
        actvStudentGender.setAdapter(adapterGender)

        ref.child("studentClassname")
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    arrayStudentClassname.clear()
                    if(snapshot.exists()) {
                        for(classname in snapshot.children) {
                            val classnameValue = classname.getValue(StudentClassnameData::class.java)
                            if(classnameValue != null) {
                                arrayStudentClassname.add(classnameValue.classname.toString().replace("\"", ""))
                            }
                        }
                    }
                    val classname = arrayStudentClassname.toTypedArray()
                    if(classname.isEmpty()) {
                        actvStudentClassname.setText("Kosong")
                    } else {
                        actvStudentClassname.setText(classname[0])
                    }
                    val adapterClassname = ArrayAdapter(this@StudentAddActivity, android.R.layout.simple_list_item_1, classname)
                    actvStudentClassname.setAdapter(adapterClassname)
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@StudentAddActivity, "Error", Toast.LENGTH_LONG).show()
                }
            })

        btnSave.setOnClickListener {
            val rfidV = tvStudentRFID.text
            val nameVal = etStudentName.text
            val nicknameVal = etStudentNickname.text
            val genderVal = actvStudentGender.text
            val classnameVal = actvStudentClassname.text

            if(nameVal.isEmpty() || nameVal[0] == ' ') {
                etStudentName.error = "Harap Isi Nama"
                return@setOnClickListener
            }
            if(nicknameVal.isEmpty() || nicknameVal[0] == ' ') {
                etStudentNickname.error = "Harap Isi Panggilan"
                return@setOnClickListener
            }
            if(classnameVal.toString() == "Kosong") {
                Toast.makeText(this@StudentAddActivity, "Buat Kelas Terlebih Dahulu!", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if(rfidV.toString() == "Silahkan Scan Kartu") {
                Toast.makeText(this@StudentAddActivity, "Scan Kartu Terlebih Dahulu!", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val imageVal = if(genderVal.toString() == "Laki-laki") {
                (1..3).random()
            } else {
                (4..6).random()
            }

            val rfidVal = rfidV.toString().replace("\"", "").replace("\\p{Zs}+".toRegex(), "").take(8)
            ref.child("student").child(rfidVal).child("rfid").setValue(rfidV)
            ref.child("student").child(rfidVal).child("image").setValue(imageVal)
            ref.child("student").child(rfidVal).child("name").setValue(nameVal.toString())
            ref.child("student").child(rfidVal).child("nickname").setValue(nicknameVal.toString())
            if(genderVal.toString() == "Laki-laki") {
                ref.child("student").child(rfidVal).child("gender").setValue(0)
            } else {
                ref.child("student").child(rfidVal).child("gender").setValue(1)
            }
            ref.child("student").child(rfidVal).child("classname").setValue(classnameVal.toString())
            val intent = Intent(this@StudentAddActivity, SchoolActivity::class.java)
            intent.putExtra(SchoolActivity.EXTRA_LOGIN, 1)
            startActivity(intent)
        }
    }
}