package com.upiyptk.pintarbiologi.data

data class StudentRankData(
    var number: Long? = null,
    var rfid: String? = "Error",
    var image: Int? = 0,
    var name: String? = "Error",
    var nickname: String? = "Error",
    var classname: String? = "Error",
    var gender: Int? = 0,
    var result: Int? = null
)