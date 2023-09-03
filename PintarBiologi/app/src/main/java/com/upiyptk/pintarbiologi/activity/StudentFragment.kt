package com.upiyptk.pintarbiologi.activity

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.upiyptk.pintarbiologi.R
import com.upiyptk.pintarbiologi.adapter.StudentAdapter
import com.upiyptk.pintarbiologi.data.StudentRankData

class StudentFragment: Fragment() {
    private lateinit var btnAdd: CardView
    private lateinit var rvStudent: RecyclerView
    private lateinit var ref: DatabaseReference
    private var arrayStudent: ArrayList<StudentRankData> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_student, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnAdd = view.findViewById(R.id.cv_student_add)
        rvStudent = view.findViewById(R.id.rv_student)
        rvStudent.layoutManager = LinearLayoutManager(requireActivity())
        rvStudent.setHasFixedSize(true)

        btnAdd.setOnClickListener {
            Intent(requireActivity(), StudentAddActivity::class.java).also {
                startActivity(it)
                ref.child("lastValue").child("rfid").setValue("Silahkan Scan Kartu")
            }
        }

        val login = requireActivity().intent.getIntExtra("extra_login", 0)
        if(login == 1) {
            btnAdd.visibility = View.VISIBLE
        } else {
            btnAdd.visibility = View.GONE
        }

        ref = FirebaseDatabase.getInstance().reference
        ref.child("student")
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    arrayStudent.clear()
                    if(snapshot.exists()) {
                        for(student in snapshot.children) {
                            val studentValue = student.getValue(StudentRankData::class.java)
                            if(studentValue != null) {
                                arrayStudent.add(studentValue)
                            }
                        }
                        val adapter = StudentAdapter(arrayStudent)
                        rvStudent.adapter = adapter
                        adapter.setOnItemClickCallback(object: StudentAdapter.OnItemClickCallback {
                            override fun onItemClicked(student: StudentRankData) {
                                getStudentDetails(student)
                            }
                        })
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireActivity(), "Error", Toast.LENGTH_LONG).show()
                }
            })
    }

    private fun getStudentDetails(student: StudentRankData) {
        Intent(requireActivity(), DetailsStudentActivity::class.java).also {
            it.putExtra(DetailsStudentActivity.EXTRA_RFID, student.rfid)
            it.putExtra(DetailsStudentActivity.EXTRA_IMAGE, student.image)
            it.putExtra(DetailsStudentActivity.EXTRA_NAME, student.name)
            it.putExtra(DetailsStudentActivity.EXTRA_GENDER, student.gender)
            it.putExtra(DetailsStudentActivity.EXTRA_CLASSNAME, student.classname)
            it.putExtra(DetailsStudentActivity.EXTRA_ACTIVITY, "1")
            it.putExtra(DetailsStudentActivity.EXTRA_LOGIN, requireActivity().intent.getIntExtra("extra_login", 0))
            startActivity(it)
        }
    }
}