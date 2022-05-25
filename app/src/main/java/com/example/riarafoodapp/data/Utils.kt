package com.example.riarafoodapp.data

import android.content.Context
import androidx.appcompat.app.AlertDialog

/**
Created by zaloaustine in 5/25/22.
 */

fun Context.showAlertDialog(positiveBtn:String = "yes", title:String, message:String, completion: () -> Unit){
    val builder = AlertDialog.Builder(this)
    builder.setTitle(title)
    builder.setMessage(message)

    builder.setPositiveButton(positiveBtn) { dialog, which ->
        dialog.dismiss()
        completion()
    }

    builder.setNegativeButton(android.R.string.no) { dialog, which ->
        dialog.dismiss()
    }
    builder.show()
}