package com.farizhustha.storyapp.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Typeface
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import com.farizhustha.storyapp.utils.Utils
import com.google.android.material.textfield.TextInputEditText

class ConfirmPasswordEditText : TextInputEditText {
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

    private var inputLayout: ConfirmPasswordInputLayout? = null

    private fun init() {
        setupInitEditText()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        typeface = Typeface.DEFAULT
    }

    fun setInputLayout(inputLayout: ConfirmPasswordInputLayout) {
        this.inputLayout = inputLayout
    }


    private fun setupInitEditText() {
        inputType =
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                inputLayout?.error = if (inputLayout?.getPassword() == s.toString()) {
                    null
                } else {
                    "Password tidak cocok"
                }
            }
        })
        Utils.keyboardClearFocus(this)

    }
}