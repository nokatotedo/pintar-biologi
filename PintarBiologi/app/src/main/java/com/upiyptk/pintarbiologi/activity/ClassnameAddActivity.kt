package com.upiyptk.pintarbiologi.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.google.firebase.database.*
import com.upiyptk.pintarbiologi.R
import com.upiyptk.pintarbiologi.data.StudentClassnameData

class ClassnameAddActivity: AppCompatActivity() {
    private lateinit var btnClose: ImageView
    private lateinit var etClassname: EditText
    private lateinit var btnSave: AppCompatButton
    private lateinit var ref: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_classname_add)

        btnClose = findViewById(R.id.button_close)
        etClassname = findViewById(R.id.et_classname)
        btnSave = findViewById(R.id.button_save)
        ref = FirebaseDatabase.getInstance().reference

        btnClose.setOnClickListener {
            Intent(this@ClassnameAddActivity, SchoolActivity::class.java).also {
                it.putExtra(SchoolActivity.EXTRA_LOGIN, 1)
                startActivity(it)
            }
        }

        btnSave.setOnClickListener {
            ref.child("studentClassname")
                .addListenerForSingleValueEvent(object: ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var available = false
                        if(snapshot.exists()) {
                            for(classname in snapshot.children) {
                                val classnameValue = classname.getValue(StudentClassnameData::class.java)
                                val classnameVal = classnameValue!!.classname.toString()
                                if(classnameVal == etClassname.text.toString().uppercase()) {
                                    available = true
                                    etClassname.error = "Kelas Sudah Ada"
                                }
                            }
                        }
                        if(!available) {
                            ref.child("lastValue").child("classname").get().addOnSuccessListener {
                                val classVal = Integer.parseInt(it.value.toString())
                                ref.child("studentClassname").child("c${classVal}").child("classname").setValue(etClassname.text.toString().uppercase())
                                ref.child("studentClassname").child("c${classVal}").child("number").setValue(classVal)
                                ref.child("lastValue").child("classname").setValue(classVal+1)
                            }
                            val intent = Intent(this@ClassnameAddActivity, SchoolActivity::class.java)
                            intent.putExtra(SchoolActivity.EXTRA_LOGIN, 1)
                            startActivity(intent)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(this@ClassnameAddActivity, "Error", Toast.LENGTH_LONG).show()
                    }
                })
        }
    }
}