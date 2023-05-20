package com.farizhustha.storyapp.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Typeface
import android.util.AttributeSet
import com.farizhustha.storyapp.R
import com.google.android.material.textfield.TextInputLayout

class ConfirmPasswordInputLayout : TextInputLayout {

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    ) {
        init()
    }

    private var password: String? = null

    private val inputEditText: ConfirmPasswordEditText = ConfirmPasswordEditText(context).apply {
        setInputLayout(this@ConfirmPasswordInputLayout)
    }

    private fun init() {
        addView(inputEditText)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        endIconMode = END_ICON_PASSWORD_TOGGLE
        hint = "Confirm Password"
        typeface = Typeface.DEFAULT
    }

    fun getPassword(): String? = password

    fun setPassword(password: String) {
        this.password = password
    }
}