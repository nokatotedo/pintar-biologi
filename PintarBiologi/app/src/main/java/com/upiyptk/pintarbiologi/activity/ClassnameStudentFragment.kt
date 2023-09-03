package com.upiyptk.pintarbiologi.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.upiyptk.pintarbiologi.R
import com.upiyptk.pintarbiologi.adapter.StudentAdapter
import com.upiyptk.pintarbiologi.data.StudentRankData
import com.upiyptk.pintarbiologi.function.Pack

class ClassnameStudentFragment: Fragment() {
    private lateinit var btnBack: ImageView
    private lateinit var btnDelete: ImageView
    private lateinit var tvClassname: TextView
    private lateinit var tvStudent: TextView
    private lateinit var rvStudent: RecyclerView
    private lateinit var ref: DatabaseReference
    private var arrayStudent: ArrayList<StudentRankData> = arrayListOf()
    private val args by navArgs<ClassnameStudentFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_classname_student, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnBack = view.findViewById(R.id.button_back)
        btnDelete = view.findViewById(R.id.button_delete)
        tvClassname = view.findViewById(R.id.tv_classname)
        tvStudent = view.findViewById(R.id.tv_student)
        rvStudent = view.findViewById(R.id.rv_student)
        rvStudent.layoutManager = LinearLayoutManager(requireActivity())
        rvStudent.setHasFixedSize(true)

        val login = requireActivity().intent.getIntExtra("extra_login", 0)
        if(login == 1) {
            btnDelete.visibility = View.VISIBLE
        } else {
            btnDelete.visibility = View.INVISIBLE
        }

        btnBack.setOnClickListener {
            val action = ClassnameStudentFragmentDirections.actionClassnameStudentFragmentToClassnameFragment()
            findNavController().navigate(action)
        }
        tvClassname.text = args.classnameArgument.classname

        ref = FirebaseDatabase.getInstance().reference
        ref.child("student").orderByChild("classname").equalTo(args.classnameArgument.classname)
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
                        tvStudent.visibility = View.INVISIBLE
                        val adapter = StudentAdapter(arrayStudent)
                        rvStudent.adapter = adapter
                        adapter.setOnItemClickCallback(object: StudentAdapter.OnItemClickCallback {
                            override fun onItemClicked(student: StudentRankData) {
                                getStudentDetails(student)
                            }
                        })
                    } else {
                        val adapter = StudentAdapter(arrayStudent)
                        rvStudent.adapter = adapter
                        tvStudent.visibility = View.VISIBLE
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireActivity(), "Error", Toast.LENGTH_LONG).show()
                }
            })


        btnDelete.setOnClickListener {
            if(!Pack.isOnline(requireActivity())) {
                Toast.makeText(requireActivity(), "Error", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val builder = AlertDialog.Builder(requireActivity())
            builder.setTitle("Hapus Data Kelas")
            builder.setMessage("Data Kelas, Siswa, dan Riwayat Siswa akan Dihapus. Yakin Hapus?")
            builder.setPositiveButton("Ya") { _, _ ->
                ref.child("studentClassname").child("c${args.classnameArgument.number}").removeValue()
                ref.child("student").orderByChild("classname").equalTo(args.classnameArgument.classname)
                    .addValueEventListener(object: ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if(snapshot.exists()) {
                                for(student in snapshot.children) {
                                    val studentValue = student.getValue(StudentRankData::class.java)
                                    val rfidVal = studentValue!!.rfid
                                    ref.child("studentResult").orderByChild("rfid").equalTo("$rfidVal")
                                        .addValueEventListener(object: ValueEventListener {
                                            override fun onDataChange(snapshot: DataSnapshot) {
                                                if(snapshot.exists()) {
                                                    for(result in snapshot.children) {
                                                        result.ref.removeValue()
                                                    }
                                                }
                                            }

                                            override fun onCancelled(error: DatabaseError) {
                                                Toast.makeText(requireActivity(), "Error", Toast.LENGTH_LONG).show()
                                            }
                                        })
                                    student.ref.removeValue()
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(requireActivity(), "Error", Toast.LENGTH_LONG).show()
                        }
                    })
                val action = ClassnameStudentFragmentDirections.actionClassnameStudentFragmentToClassnameFragment()
                findNavController().navigate(action)
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