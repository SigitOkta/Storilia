package com.dwarfkit.storilia.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.dwarfkit.storilia.R

class PassEditText : AppCompatEditText, View.OnTouchListener  {
    private lateinit var visibilityButtonImage: Drawable
    constructor(context: Context) : super(context) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        hint = resources.getString(R.string.text_hint_password)
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }

    private fun init() {
        visibilityButtonImage = ContextCompat.getDrawable(context, R.drawable.ic_visibility) as Drawable
        setOnTouchListener(this)
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Do nothing.
            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString().isNotEmpty()) showVisibilityButton() else hideVisibilityButton()
                if (s.length < 6 && s.toString().isNotEmpty()) error = resources.getString(
                    R.string.valid_password)
            }
            override fun afterTextChanged(s: Editable) {
                // Do nothing.
            }
        })
    }


    private fun showVisibilityButton() {
        setButtonDrawables(endOfTheText = visibilityButtonImage)
    }
    private fun hideVisibilityButton() {
        setButtonDrawables()
    }

    private fun setButtonDrawables(
        startOfTheText: Drawable? = null,
        topOfTheText: Drawable? = null,
        endOfTheText: Drawable? = null,
        bottomOfTheText: Drawable? = null
    ){
        setCompoundDrawablesWithIntrinsicBounds(
            startOfTheText,
            topOfTheText,
            endOfTheText,
            bottomOfTheText
        )
    }
    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        if (compoundDrawables[2] != null) {
            val visibilityButtonStart: Float
            val visibilityButtonEnd: Float
            var isVisibilityButtonClicked = false
            if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                visibilityButtonEnd = (visibilityButtonImage.intrinsicWidth + paddingStart).toFloat()
                when {
                    event.x < visibilityButtonEnd -> isVisibilityButtonClicked = true
                }
            } else {
                visibilityButtonStart = (width - paddingEnd - visibilityButtonImage.intrinsicWidth).toFloat()
                when {
                    event.x > visibilityButtonStart -> isVisibilityButtonClicked = true
                }
            }
            if (isVisibilityButtonClicked) {
                return when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        hideVisibilityButton()
                        if (transformationMethod.equals(HideReturnsTransformationMethod.getInstance())) {
                            transformationMethod = PasswordTransformationMethod.getInstance() // hide password
                            visibilityButtonImage = ContextCompat.getDrawable(context, R.drawable.ic_visibility) as Drawable
                            showVisibilityButton()
                        } else {
                            transformationMethod = HideReturnsTransformationMethod.getInstance() // show password
                            visibilityButtonImage = ContextCompat.getDrawable(context, R.drawable.ic_visibility_off) as Drawable
                            showVisibilityButton()
                        }
                        true
                    }
                    else -> return false
                }
            } else {
                return false
            }
        }
        return false
    }
}