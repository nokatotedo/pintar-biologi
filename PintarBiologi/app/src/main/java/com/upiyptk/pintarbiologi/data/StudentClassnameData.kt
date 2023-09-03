package com.upiyptk.pintarbiologi.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class StudentClassnameData(
    var number: Long? = null,
    var classname: String? = "Error"
): Parcelable