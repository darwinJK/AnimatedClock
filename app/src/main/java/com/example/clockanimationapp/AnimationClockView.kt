package com.example.clockanimationapp

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import kotlin.math.sin
import java.util.Calendar
import kotlin.math.PI

class AnimationClockView @JvmOverloads constructor
    (
    context:Context,attributeSet : AttributeSet?=null) :
    View(context,attributeSet){

        //clock border paint
        private val paint = Paint().apply {
            color = Color.CYAN
            style = Paint.Style.STROKE
            strokeWidth = 10f
        }

        private val wavePaint = arrayOf(
            //color red
            Paint().apply {
                color=Color.RED
                style = Paint.Style.STROKE
                strokeWidth = 8f
                setShadowLayer(40f, 15f, 15f, Color.RED) },

            //color blue
            Paint().apply {
                color=Color.BLUE
                style=Paint.Style.STROKE
                strokeWidth=8f
                setShadowLayer(20f, 15f, 15f, Color.BLUE) },

            //color green
            Paint().apply {
                color=Color.GREEN
                style=Paint.Style.STROKE
                strokeWidth=8f
                setShadowLayer(20f, 15f, 15f, Color.GREEN)
            }
        )

        //hand paint
        private val handPaint = Paint().apply {
            color = Color.WHITE
            style = Paint.Style.STROKE
            strokeWidth = 8f
        }

        private val wavePath = Path()
        private var waveOffset = 0f
        private var waveAmplitude = 30f //height
        private var waveFrequency = 0.1f //number of wave

        init {
            startWaveAnimation()
        }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val centerX = width / 2f
        val centerY = height / 2f
        val radius = Math.min(centerX, centerY) - 100

        //calling drawCircle
        paint.alpha = 255
        canvas.drawCircle(centerX, centerY, radius, paint)

        //calling drawWaves
        drawWaves(canvas, centerX, centerY, radius)

        //calling drawClockHands
        drawClockHands(canvas, centerX, centerY, radius)
    }

    //wave customization
    private fun drawWaves(canvas: Canvas, centerX: Float, centerY: Float, radius: Float) {

        val amplitude = waveAmplitude
        val frequency = waveFrequency

        // Draw wave effect around the clock
        for (i in wavePaint.indices) {
            wavePath.reset()
            val waveRadius = radius + amplitude * (i + 1)

            for (angle in 0..360 step 5) {
                val radians = Math.toRadians(angle.toDouble())
                val x = (centerX + waveRadius * Math.cos(radians)).toFloat()
                val y = (centerY + waveRadius * Math.sin(radians) + amplitude * sin(frequency * angle + waveOffset)).toFloat()

                if (angle == 0) {
                    wavePath.moveTo(x, y)
                } else {
                    wavePath.lineTo(x, y)
                }
            }
            wavePath.close()
            canvas.drawPath(wavePath, wavePaint[i])

        }
    }

    private fun drawClockHands(canvas: Canvas, centerX: Float, centerY: Float, radius: Float) {
        val calendar = Calendar.getInstance()

        // Calculate the hour hand position
        val hour = calendar.get(Calendar.HOUR)
        val hourAngle = Math.toRadians(((hour + calendar.get(Calendar.MINUTE) / 60f) * 30 - 90).toDouble())
        val hourHandLength = radius * 0.5f
        val hourX = (centerX + Math.cos(hourAngle) * hourHandLength).toFloat()
        val hourY = (centerY + Math.sin(hourAngle) * hourHandLength).toFloat()

        // Calculate the minute hand position
        val minute = calendar.get(Calendar.MINUTE)
        val minuteAngle = Math.toRadians((minute * 6 - 90).toDouble())
        val minuteHandLength = radius * 0.8f
        val minuteX = (centerX + Math.cos(minuteAngle) * minuteHandLength).toFloat()
        val minuteY = (centerY + Math.sin(minuteAngle) * minuteHandLength).toFloat()

        // Draw hour hand
        canvas.drawLine(centerX, centerY, hourX, hourY, handPaint)

        // Draw minute hand
        canvas.drawLine(centerX, centerY, minuteX, minuteY, handPaint)
    }

    private fun startWaveAnimation() {
        val animator = ValueAnimator.ofFloat(0f, (2* PI).toFloat()).apply {
            duration = 2000
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
            addUpdateListener {
                waveOffset = it.animatedValue as Float
                invalidate()
            }
        }
        animator.start()
    }

}

