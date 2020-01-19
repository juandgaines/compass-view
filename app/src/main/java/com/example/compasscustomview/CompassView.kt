package com.example.compasscustomview

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin


private enum class Directions(val label: Int) {
    N(R.string.direction_north),
    NE(R.string.direction_north_east),
    E(R.string.direction_east),
    SE(R.string.direction_south_east),
    S(R.string.direction_south),
    SW(R.string.direction_south_west),
    W(R.string.direction_west),
    NW(R.string.direction_north_west);

}

private const val RADIUS_OFFSET_LABEL = 60
private const val CURSOR_OFFSET_INDICATOR = 20
private const val LINES_DEGREES_OFFSET_INDICATOR = 30.0f
private const val LENGHT_LINE = 40.0f
private const val OFFSET_TEXT = 17

class CompassView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var radius = 0.0f
    private val pointPosition: PointF = PointF(0.0f, 0.0f)
    var degress: Float = 0.0f
        set(value) {
            field = value
            invalidate()
        }


    init {
        isClickable = true
        context.withStyledAttributes(attrs, R.styleable.CompassView) {
            degress = getFloat(R.styleable.CompassView_angleInDegrees, 0.0f)
        }
    }

    private val paintCircle = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 20.0f
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
        typeface = Typeface.create("", Typeface.BOLD)
    }

    private val paintText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 45.0f
        typeface = Typeface.create("", Typeface.BOLD)
    }

    private val paintCursor = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.RED
        strokeWidth = 20.0f
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        radius = (min(width, height) / 2.0 * 0.8).toFloat()
    }

    private fun PointF.computeXY(canvas: Canvas, pos: Directions, radius: Float) {
        // Angles are in radians.
        val startAngle = Math.PI.times(3.0f / 2.0f)
        val angle = startAngle + pos.ordinal * (Math.PI / 4)
        val c = cos(angle)
        val s = sin(angle)
        x = (radius * c).toFloat() + width / 2
        y = (radius * s).toFloat() + height / 2

        val x1 = x + c * LINES_DEGREES_OFFSET_INDICATOR
        val y1 = y + s * LINES_DEGREES_OFFSET_INDICATOR
        val x2 = x + c * (LENGHT_LINE + LINES_DEGREES_OFFSET_INDICATOR)
        val y2 = y + s * (LENGHT_LINE + LINES_DEGREES_OFFSET_INDICATOR)
        if (angle != startAngle)
            canvas.drawLine(x1.toFloat(), y1.toFloat(), x2.toFloat(), y2.toFloat(), paintCircle)
        else
            canvas.drawLine(x1.toFloat(), y1.toFloat(), x2.toFloat(), y2.toFloat(), paintCursor)

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawCircle((width / 2).toFloat(), (height / 2).toFloat(), radius, paintCircle)
        drawCursor(canvas, paintCursor, 70)
        canvas.rotate(degress, (width / 2.0f), height / 2.0f)
        val labelRadius = radius - RADIUS_OFFSET_LABEL
        for (i in Directions.values()) {
            pointPosition.computeXY(canvas, i, labelRadius)
            val label = resources.getString(i.label)
            canvas.drawText(label, pointPosition.x, pointPosition.y + OFFSET_TEXT, paintText)
        }
        canvas.rotate(0.0f, (width / 2.0f), height / 2.0f)

    }

    private fun drawCursor(canvas: Canvas, paint: Paint, widthT: Int) {
        val path = Path()
        val halfWidth = widthT / 2
        val pointCursor =
            PointF((width / 2.0f), (height / 2.0f).minus(radius).minus(CURSOR_OFFSET_INDICATOR))
        path.moveTo(pointCursor.x, pointCursor.y) // Bottom
        path.lineTo(pointCursor.x - halfWidth, pointCursor.y + halfWidth) // TOP left
        path.lineTo(pointCursor.x + halfWidth, pointCursor.y + halfWidth) // TOP right
        path.lineTo(pointCursor.x, pointCursor.y) // Back to Bottom
        path.close()
        canvas.drawPath(path, paint)
    }

    override fun performClick(): Boolean {
        if (super.performClick()) return true
        degress = degress.plus(10)
        invalidate()
        return true
    }
}