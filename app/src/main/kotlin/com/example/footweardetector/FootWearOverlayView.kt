package com.example.footweardetector

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class FootWearOverlayView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private var detections: List<FootWearDetector.Detection> = emptyList()
    
    private val boxPaint = Paint().apply {
        color = Color.parseColor("#00E676") // Vibrant Green
        style = Paint.Style.STROKE
        strokeWidth = 10f
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
        isAntiAlias = true
    }
    
    private val textPaint = Paint().apply {
        color = Color.WHITE
        textSize = 48f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        style = Paint.Style.FILL
        isAntiAlias = true
    }
    
    private val textBackgroundPaint = Paint().apply {
        color = Color.parseColor("#CC00E676") // Semi-transparent matching green
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    fun setDetections(results: List<FootWearDetector.Detection>) {
        detections = results
        postInvalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (detection in detections) {
            val x1 = detection.x1 * width
            val y1 = detection.y1 * height
            val x2 = detection.x2 * width
            val y2 = detection.y2 * height

            // Draw bounding box with rounded corners
            val rect = RectF(x1, y1, x2, y2)
            canvas.drawRoundRect(rect, 24f, 24f, boxPaint)
            
            // Format label text
            val labelStr = detection.label.replaceFirstChar { it.uppercase() }
            val text = "$labelStr ${(detection.confidence * 100).toInt()}%"
            val textWidth = textPaint.measureText(text)
            
            // Calculate pill background coordinates
            val paddingX = 20f
            val paddingY = 16f
            val textHeight = textPaint.descent() - textPaint.ascent()
            
            var bgTop = y1 - textHeight - (paddingY * 2) - 10f
            var bgBottom = y1 - 10f
            var textY = y1 - paddingY - textPaint.descent() - 10f
            
            // Adjust if drawing outside top edge
            if (bgTop < 0) {
                bgTop = y1 + 10f
                bgBottom = bgTop + textHeight + (paddingY * 2)
                textY = bgBottom - paddingY - textPaint.descent()
            }

            // Draw pill background
            val textBgRect = RectF(
                x1,
                bgTop,
                x1 + textWidth + (paddingX * 2),
                bgBottom
            )
            canvas.drawRoundRect(textBgRect, 16f, 16f, textBackgroundPaint)
            
            // Draw text
            canvas.drawText(text, x1 + paddingX, textY, textPaint)
        }
    }
}
