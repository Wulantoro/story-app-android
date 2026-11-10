package com.example.storyapp.customview

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet

class CustomPass: MyEditText {

    constructor(context: Context) : super(context) {
        validatePass()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        validatePass()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        validatePass()
    }

    private fun validatePass() {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (s.toString().length < 8) {
                    error = if (s.toString().length < 8) {
                        "Password tidak boleh kurang dari 8 karakter"
                    } else {
                        null
                    }

                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
    }
}