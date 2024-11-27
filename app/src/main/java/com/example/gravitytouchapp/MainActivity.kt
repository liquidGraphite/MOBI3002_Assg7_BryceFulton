package com.example.gravitytouchapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.gravitytouchapp.ui.theme.GravityTouchAppTheme
import android.graphics.Canvas
import android.graphics.Paint
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.min

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val drawView = DrawView(this)
        setContentView(drawView)
    }

    class DrawView(context: MainActivity) : View(context) {
        private val paint = Paint()
        private val ballPaint = Paint()
        private var ballX = 0f
        private var ballY = 0f
        private var touchActive = false
        private var gravityX = 0f
        private var gravityY = 0f

        init {
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 5f
            paint.color = 0xFF000000.toInt()

            ballPaint.style = Paint.Style.FILL
            ballPaint.color = 0xFF00FF00.toInt()

            // Register for sensor updates (e.g., accelerometer)
            val sensorManager = context.getSystemService(SENSOR_SERVICE) as android.hardware.SensorManager
            val gravitySensor = sensorManager.getDefaultSensor(android.hardware.Sensor.TYPE_ACCELEROMETER)

            sensorManager.registerListener(object : android.hardware.SensorEventListener {
                override fun onSensorChanged(event: android.hardware.SensorEvent?) {
                    event?.let {
                        gravityX = it.values[0]
                        gravityY = it.values[1]
                        invalidate() // Redraw the screen
                    }
                }

                override fun onAccuracyChanged(sensor: android.hardware.Sensor?, accuracy: Int) {}
            }, gravitySensor, android.hardware.SensorManager.SENSOR_DELAY_UI)
        }

        override fun onTouchEvent(event: MotionEvent): Boolean {
            when (event.action) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                    ballX = event.x
                    ballY = event.y
                    touchActive = true
                    invalidate()
                }
                MotionEvent.ACTION_UP -> {
                    touchActive = false
                }
            }
            return true
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            if (touchActive) {
                canvas.drawCircle(ballX, ballY, 20f, ballPaint)
            }

            // Update ball position based on gravity
            ballX -= gravityX
            ballY += gravityY

            // Keep the ball within screen bounds
            ballX = ballX.coerceIn(0f, width.toFloat())
            ballY = ballY.coerceIn(0f, height.toFloat())

            // Draw the ball
            canvas.drawCircle(ballX, ballY, 20f, ballPaint)
        }
    }
}