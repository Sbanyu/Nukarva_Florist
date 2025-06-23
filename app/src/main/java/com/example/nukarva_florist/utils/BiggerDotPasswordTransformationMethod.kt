package com.example.nukarva_florist.utils

import android.text.method.PasswordTransformationMethod
import android.view.View

class BiggerDotPasswordTransformationMethod : PasswordTransformationMethod() {
    override fun getTransformation(source: CharSequence, view: View): CharSequence {
        return PasswordCharSequence(super.getTransformation(source, view))
    }

    private class PasswordCharSequence(
        val transformation: CharSequence
    ) : CharSequence by transformation {
        override fun get(index: Int): Char = if (transformation[index] == DOT) {
            BIGGER_DOT
        } else {
            transformation[index]
        }
    }

    companion object {
        // Opsi karakter dot dengan ukuran yang berbeda-beda
        private const val BIGGER_DOT = '●'   // BLACK CIRCLE (paling besar)
        private const val MEDIUM_DOT = '•'   // BULLET (ukuran medium)
        private const val MIDDLE_DOT = '·'   // MIDDLE DOT (lebih kecil)
        private const val SMALL_DOT = '∙'    // DOT OPERATOR (kecil)
        private const val DOT = '\u2022'     // Default password dot
    }
}