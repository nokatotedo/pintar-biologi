package com.upiyptk.pintarbiologi.activity

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.upiyptk.pintarbiologi.R
import com.upiyptk.pintarbiologi.adapter.ClassnameAdapter
import com.upiyptk.pintarbiologi.data.StudentClassnameData

class ClassnameFragment: Fragment() {
    private lateinit var btnAdd: CardView
    private lateinit var rvClassname: RecyclerView
    private lateinit var ref: DatabaseReference
    private var arrayClassname: ArrayList<StudentClassnameData> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_classname, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvClassname = view.findViewById(R.id.rv_classname)
        rvClassname.layoutManager = LinearLayoutManager(requireActivity())
        rvClassname.setHasFixedSize(true)
        btnAdd = view.findViewById(R.id.cv_classname_add)
        btnAdd.setOnClickListener {
            Intent(requireActivity(), ClassnameAddActivity::class.java).also {
                startActivity(it)
            }
        }

        val login = requireActivity().intent.getIntExtra("extra_login", 0)
        if(login == 1) {
            btnAdd.visibility = View.VISIBLE
        } else {
            btnAdd.visibility = View.GONE
        }

        ref = FirebaseDatabase.getInstance().reference
        ref.child("studentClassname")
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    arrayClassname.clear()
                    if(snapshot.exists()) {
                        for(classname in snapshot.children) {
                            val classnameValue = classname.getValue(StudentClassnameData::class.java)
                            if(classnameValue != null) {
                                arrayClassname.add(classnameValue)
                            }
                        }
                        val adapter = ClassnameAdapter(arrayClassname)
                        rvClassname.adapter = adapter
                        adapter.setOnItemClickCallback(object: ClassnameAdapter.OnItemClickCallback {
                            override fun onItemClicked(classname: StudentClassnameData) {
                                val action = ClassnameFragmentDirections.actionClassnameFragmentToClassnameStudentFragment(classname)
                                findNavController().navigate(action)
                            }
                        })
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireActivity(), "Error", Toast.LENGTH_LONG).show()
                }
            })
    }
}