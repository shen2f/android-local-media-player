package com.shen.mediaplayer.feature_videoplayer

import android.content.Context
import android.graphics.PointF
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.ViewConfiguration
import kotlin.math.abs

class VideoPlayerGestureDetector(
    context: Context,
    private val listener: GestureListener
) {

    private val touchSlop = ViewConfiguration.get(context).scaledTouchSlop
    private val minimumVelocity = ViewConfiguration.get(context).scaledMinimumFlingVelocity
    private val maximumVelocity = ViewConfiguration.get(context).scaledMaximumFlingVelocity

    private var velocityTracker: VelocityTracker? = null
    private var initialPoint = PointF(0f, 0f)
    private var lastPoint = PointF(0f, 0f)
    private var activePointerId = MotionEvent.INVALID_POINTER_ID
    private var isDragging = false
    private var gestureType: GestureType = GestureType.NONE

    private val screenWidth = context.resources.displayMetrics.widthPixels

    enum class GestureType {
        NONE,
        BRIGHTNESS,
        VOLUME,
        SEEK
    }

    interface GestureListener {
        fun onSingleTap(): Boolean
        fun onDoubleTap(): Boolean
        fun onBrightnessChange(delta: Float): Boolean
        fun onVolumeChange(delta: Float): Boolean
        fun onSeekChange(delta: Float, totalDuration: Long): Boolean
        fun onSeekStart(): Boolean
        fun onSeekEnd(): Boolean
        fun onFling(velocityX: Float, velocityY: Float): Boolean
    }

    fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                activePointerId = event.getPointerId(0)
                initialPoint.set(event.getX(0), event.getY(0))
                lastPoint.set(event.getX(0), event.getY(0))

                velocityTracker = VelocityTracker.obtain()
                velocityTracker?.addMovement(event)
            }

            MotionEvent.ACTION_POINTER_DOWN -> {
                activePointerId = event.getPointerId(event.actionIndex)
            }

            MotionEvent.ACTION_MOVE -> {
                val index = event.findPointerIndex(activePointerId)
                if (index == MotionEvent.INVALID_POINTER_ID) return false

                val currentX = event.getX(index)
                val currentY = event.getY(index)
                val dx = currentX - initialPoint.x
                val dy = currentY - lastPoint.y

                if (!isDragging) {
                    if (abs(dx) > touchSlop || abs(dy) > touchSlop) {
                        isDragging = true
                        determineGestureType(initialPoint.x / screenWidth, dx, dy)
                    }
                }

                if (isDragging) {
                    when (gestureType) {
                        GestureType.BRIGHTNESS -> {
                            listener.onBrightnessChange(-dy / 2000)
                        }
                        GestureType.VOLUME -> {
                            listener.onVolumeChange(-dy / 2000)
                        }
                        GestureType.SEEK -> {
                            val deltaX = currentX - initialPoint.x
                            listener.onSeekChange(deltaX, screenWidth)
                        }
                        else -> {}
                    }
                }

                velocityTracker?.addMovement(event)
                lastPoint.set(currentX, currentY)
            }

            MotionEvent.ACTION_UP -> {
                velocityTracker?.computeCurrentVelocity(1000, maximumVelocity.toFloat())
                val velocityX = velocityTracker?.xVelocity ?: 0f
                val velocityY = velocityTracker?.yVelocity ?: 0f

                if (abs(velocityX) > minimumVelocity || abs(velocityY) > minimumVelocity) {
                    listener.onFling(velocityX, velocityY)
                }

                resetGesture()
            }

            MotionEvent.ACTION_CANCEL -> {
                resetGesture()
            }

            MotionEvent.ACTION_POINTER_UP -> {
                val actionIndex = event.actionIndex
                val pointerId = event.getPointerId(actionIndex)
                if (pointerId == activePointerId) {
                    val newIndex = if (actionIndex == 0) 1 else 0
                    activePointerId = event.getPointerId(newIndex)
                    initialPoint.set(event.getX(newIndex), event.getY(newIndex))
                    lastPoint.set(event.getX(newIndex), event.getY(newIndex))
                }
            }
        }

        return true
    }

    private fun determineGestureType(xPercentage: Float, dx: Float, dy: Float) {
        if (abs(dx) > abs(dy)) {
            gestureType = GestureType.SEEK
            listener.onSeekStart()
        } else {
            if (xPercentage < 0.5f) {
                gestureType = GestureType.BRIGHTNESS
                listener.onBrightnessChange(0f)
            } else {
                gestureType = GestureType.VOLUME
                listener.onVolumeChange(0f)
            }
        }
    }

    private fun resetGesture() {
        if (gestureType == GestureType.SEEK) {
            listener.onSeekEnd()
        }
        velocityTracker?.recycle()
        velocityTracker = null
        activePointerId = MotionEvent.INVALID_POINTER_ID
        isDragging = false
        gestureType = GestureType.NONE
    }
}
