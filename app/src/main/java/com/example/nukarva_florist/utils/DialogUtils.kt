package com.example.nukarva_florist.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import com.airbnb.lottie.LottieAnimationView
import com.example.nukarva_florist.R

object DialogUtils {

    fun Context.showCustomStatusDialog(
        title: String,
        message: String,
        useImageIcon: Boolean = true,
        iconResId: Int? = null,
        lottieRawResId: Int? = null,
        isLoading: Boolean = false,
        onClickLogin: (() -> Unit)? = null
    ): Dialog {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.custom_dialog)

        val ivIcon = dialog.findViewById<ImageView>(R.id.ivIcon)
        val lottieIcon = dialog.findViewById<LottieAnimationView>(R.id.lottieAnimationView)
        val tvTitle = dialog.findViewById<TextView>(R.id.tvTitle)
        val tvMessage = dialog.findViewById<TextView>(R.id.tvMessage)
        val tvLogin = dialog.findViewById<TextView>(R.id.tvLogin)
        val lottieSpinner = dialog.findViewById<LottieAnimationView>(R.id.lottieAnimationSpinner)

        // Set title & message
        tvTitle.text = title
        tvMessage.text = message

        // Icon atau Lottie di atas
        if (useImageIcon && iconResId != null) {
            ivIcon.visibility = View.VISIBLE
            ivIcon.setImageResource(iconResId)
            lottieIcon.visibility = View.GONE
        } else if (lottieRawResId != null) {
            ivIcon.visibility = View.GONE
            lottieIcon.visibility = View.VISIBLE
            lottieIcon.setAnimation(lottieRawResId)
        }

        // Spinner atau Button di bawah
        if (isLoading) {
            lottieSpinner.visibility = View.VISIBLE
            tvLogin.visibility = View.GONE
        } else {
            lottieSpinner.visibility = View.GONE
            tvLogin.visibility = View.VISIBLE
        }

        // Button click
        tvLogin.setOnClickListener {
            dialog.dismiss()
            onClickLogin?.invoke()
        }

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        return dialog
    }

    // Create a new method to show loading dialog
    fun createLoadingDialog(context: Context): LoadingDialogHelper {
        return LoadingDialogHelper(context)
    }

    // Inner class for LoadingDialog functionality
    class LoadingDialogHelper(private val context: Context) {
        private var dialog: Dialog? = null
        private var animationView: LottieAnimationView? = null

        init {
            setupDialog()
        }

        private fun setupDialog() {
            dialog = Dialog(context)
            dialog?.apply {
                requestWindowFeature(Window.FEATURE_NO_TITLE)
                setCancelable(false)
                setContentView(R.layout.dialog_loading)
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }

            animationView = dialog?.findViewById(R.id.loading_animation)
        }

        fun show() {
            dialog?.show()
        }

        fun dismiss() {
            dialog?.dismiss()
        }

        fun setAnimation(fileName: String) {
            animationView?.setAnimation(fileName)
            animationView?.playAnimation()
        }
    }

}