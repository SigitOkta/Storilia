package com.dwarfkit.storilia.utils

import android.app.Dialog
import android.content.Context
import android.view.Window
import com.dwarfkit.storilia.R

object LoadingDialog {
    private var dialog: Dialog? = null
    fun startLoading(context: Context) {
        dialog = Dialog(context)
        dialog?.let {
            it.requestWindowFeature(Window.FEATURE_NO_TITLE)
            it.setContentView(R.layout.custom_loading)
            it.setCancelable(false)
            it.show()
        }
    }

    fun hideLoading() {
        if (dialog != null) {
            dialog!!.dismiss()
        }
    }
}