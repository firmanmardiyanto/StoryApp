package org.firmanmardiyanto.core.customview

import android.util.Patterns
import androidx.appcompat.widget.AppCompatEditText

enum class ValidationType {
    EMAIL,
    PASSWORD,
    TEXT
}

class ValidationEditText : AppCompatEditText {

    private var validationTpye: ValidationType? = ValidationType.TEXT


    constructor(context: android.content.Context) : super(context) {
        init()
    }

    constructor(context: android.content.Context, attrs: android.util.AttributeSet) : super(
        context,
        attrs
    ) {
        init()
    }

    constructor(
        context: android.content.Context,
        attrs: android.util.AttributeSet,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        init()
    }

    interface OnValidatedListener {
        fun onValidate(isValid: Boolean)
    }

    private var onValidatedListener: OnValidatedListener? = null
    private var required = false

    fun setOnValidatedListener(
        validationType: ValidationType,
        required: Boolean,
        onValidatedListener: OnValidatedListener
    ) {
        this.onValidatedListener = onValidatedListener
        this.required = required
        this.validationTpye = validationType
    }

    private fun init() {
        addTextChangedListener(object : android.text.TextWatcher {
            override fun afterTextChanged(s: android.text.Editable?) {}

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                onValidatedListener?.onValidate(validation(s))
            }
        })
    }

    private fun validation(s: CharSequence?): Boolean {
        if (required && s.toString().isEmpty()) {
            error = "Required"
            return false
        }
        when (validationTpye) {
            ValidationType.PASSWORD -> {
                if (s.toString().length < 6 && s.toString().isNotEmpty()) {
                    error = "Password must be at least 6 characters"
                    return false
                }
            }
            ValidationType.EMAIL -> {
                if (!Patterns.EMAIL_ADDRESS.matcher(s.toString())
                        .matches() && s.toString().isNotEmpty()
                ) {
                    error = "Invalid email address"
                    return false
                }
            }
            else -> return true
        }
        return true
    }
}