package com.example.makeprogramme

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout
import android.widget.TextView

/**
 * Created by 徐三朋 on 2017/12/17.
 */
class DragView: TextView {
    private var lastX: Int = 0
    private var lastY: Int = 0
    private val CHECK_SPACE: Int = 50

    var offsetLeft: Int = 0
        set(value) {
            field = value
            if (layoutParams != null) {
                val params = layoutParams as FrameLayout.LayoutParams
                params.leftMargin = value
                layoutParams = params
            }
        }
    var offsetTop: Int = 0
        set(value) {
            field = value
            if (layoutParams != null) {
                val params = layoutParams as FrameLayout.LayoutParams
                params.topMargin = value
                layoutParams = params
            }
        }

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr) {
        if (attrs != null) {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.DragView)
            offsetLeft = typedArray.getDimension(R.styleable.DragView_offset_left, 0F).toInt()
            offsetTop = typedArray.getDimension(R.styleable.DragView_offset_top, 0F).toInt()
            typedArray.recycle()
        }
        isClickable = true
        post {
            val params = layoutParams as FrameLayout.LayoutParams
            params.leftMargin = offsetLeft
            params.topMargin = offsetTop
            layoutParams = params
        }
    }

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context) : this(context, null, 0)

    override fun onTouchEvent(event: MotionEvent): Boolean {
        // 获取当前触摸的绝对坐标
        val rawX = event.rawX.toInt()
        val rawY = event.rawY.toInt()
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // 上一次离开时的坐标
                lastX = rawX
                lastY = rawY
            }
            MotionEvent.ACTION_MOVE -> {
                // touchEvent(event, rawX - lastX, rawY - lastY)
                moveView(rawX - lastX, rawY - lastY)
                // 修改上次移动完成后坐标
                lastX = rawX
                lastY = rawY
            }
            else -> {
            }
        }
        return true
    }
    private fun touchEvent(event: MotionEvent, offsetX: Int, offsetY: Int) {
        when {
            event.x < CHECK_SPACE -> when {
                event.y < CHECK_SPACE -> dragLeftTop(offsetX, offsetY)
                event.y > height - CHECK_SPACE -> dragLeftBottom(offsetX, offsetY)
                else -> dragLeft(offsetX)
            }
            event.x > width - CHECK_SPACE -> when {
                event.y < CHECK_SPACE -> dragRightTop(offsetX, offsetY)
                event.y > height - CHECK_SPACE -> dragRightBottom(offsetX, offsetY)
                else -> dragRight(offsetX)
            }
            else -> when {
                event.y < CHECK_SPACE -> dragTop(offsetY)
                event.y > height - CHECK_SPACE -> dragBottom(offsetY)
                else -> moveView(offsetX, offsetY)
            }
        }
    }
    private fun dragLeftTop(offsetX: Int, offsetY: Int) {
        val layoutParams = layoutParams as FrameLayout.LayoutParams
        layoutParams.leftMargin += offsetX
        layoutParams.topMargin += offsetY
        layoutParams.width -= offsetX
        layoutParams.height -= offsetY
        setLayoutParams (layoutParams)
    }
    private fun dragLeft(offsetX: Int) {
        val layoutParams = layoutParams as FrameLayout.LayoutParams
        layoutParams.leftMargin += offsetX
        layoutParams.width -= offsetX
        setLayoutParams (layoutParams)
    }
    private fun dragLeftBottom(offsetX: Int, offsetY: Int) {
        val layoutParams = layoutParams as FrameLayout.LayoutParams
        layoutParams.leftMargin += offsetX
        layoutParams.width -= offsetX
        layoutParams.height += offsetY
        setLayoutParams (layoutParams)
    }
    private fun dragRightTop(offsetX: Int, offsetY: Int) {
        val layoutParams = layoutParams as FrameLayout.LayoutParams
        layoutParams.width += offsetX
        layoutParams.height -= offsetY
        setLayoutParams (layoutParams)
    }
    private fun dragRight(offsetX: Int) {
        val layoutParams = layoutParams as FrameLayout.LayoutParams
        layoutParams.width += offsetX
        setLayoutParams (layoutParams)
    }
    private fun dragRightBottom(offsetX: Int, offsetY: Int) {
        val layoutParams = layoutParams as FrameLayout.LayoutParams
        layoutParams.width += offsetX
        layoutParams.height += offsetY
        setLayoutParams (layoutParams)
    }
    private fun dragTop(offsetY: Int) {
        val layoutParams = layoutParams as FrameLayout.LayoutParams
        layoutParams.topMargin += offsetY
        layoutParams.height -= offsetY
        setLayoutParams (layoutParams)
    }
    private fun dragBottom(offsetY: Int) {
        val layoutParams = layoutParams as FrameLayout.LayoutParams
        layoutParams.height += offsetY
        setLayoutParams (layoutParams)
    }

    private fun moveView(offsetX: Int, offsetY: Int) {
        if (parent !is FrameLayout) {
            throw RuntimeException("Layout must be FrameLayout")
        }
        val frameLayout = parent as FrameLayout
        val layoutParams = layoutParams as FrameLayout.LayoutParams
        layoutParams.leftMargin += offsetX
        layoutParams.topMargin += offsetY
        if (layoutParams.leftMargin < 0) {
            layoutParams.leftMargin = 0
        }
        if (layoutParams.topMargin < 0) {
            layoutParams.topMargin = 0
        }
        if (layoutParams.leftMargin + layoutParams.width > frameLayout.width) {
            layoutParams.leftMargin = frameLayout.width - layoutParams.width
        }
        if (layoutParams.topMargin + layoutParams.height > frameLayout.height) {
            layoutParams.topMargin = frameLayout.height - layoutParams.height
        }
        setLayoutParams (layoutParams)
    }
}
