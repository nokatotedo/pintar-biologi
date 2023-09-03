package com.upiyptk.pintarbiologi.data

data class StudentResultData(
    var number: Long? = null,
    var rfid: String? = "Error",
    var rank: Int? = null,
    var result: Int? = null,
    var time: Int? = null
)