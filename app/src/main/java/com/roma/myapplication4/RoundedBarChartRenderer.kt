package com.roma.myapplication4

import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.renderer.BarChartRenderer
import com.github.mikephil.charting.utils.Utils

class RoundedBarChartRenderer(
    chart: BarDataProvider,
    animator: ChartAnimator,
    viewPortHandler: com.github.mikephil.charting.utils.ViewPortHandler
) : BarChartRenderer(chart, animator, viewPortHandler) {

    var radius = 20f

    override fun drawDataSet(c: Canvas, dataSet: IBarDataSet, index: Int) {
        val trans = mChart.getTransformer(dataSet.axisDependency)
        mBarBorderPaint.color = dataSet.barBorderColor
        mBarBorderPaint.strokeWidth = Utils.convertDpToPixel(dataSet.barBorderWidth)
        val drawBorder = dataSet.barBorderWidth > 0f
        val phaseX = mAnimator.phaseX
        val phaseY = mAnimator.phaseY

        // Shadow is not rounded in this renderer

        val buffer = mBarBuffers[index]
        buffer.setPhases(phaseX, phaseY)
        buffer.setDataSet(index)
        buffer.setInverted(mChart.isInverted(dataSet.axisDependency))
        buffer.setBarWidth(mChart.barData.barWidth)
        buffer.feed(dataSet)
        trans.pointValuesToPixel(buffer.buffer)

        val isSingleColor = dataSet.colors.size == 1
        if (isSingleColor) {
            mRenderPaint.color = dataSet.color
        }

        var j = 0
        while (j < buffer.size()) {
            if (!mViewPortHandler.isInBoundsLeft(buffer.buffer[j + 2])) {
                j += 4
                continue
            }
            if (!mViewPortHandler.isInBoundsRight(buffer.buffer[j])) break

            if (!isSingleColor) {
                mRenderPaint.color = dataSet.getColor(j / 4)
            }

            // The main drawing logic for rounded bars
            val path = roundRect(
                left = buffer.buffer[j],
                top = buffer.buffer[j + 1],
                right = buffer.buffer[j + 2],
                bottom = buffer.buffer[j + 3],
                rx = radius,
                ry = radius,
                tl = true, // Top-left corner
                tr = true, // Top-right corner
                br = false, // Bottom-right corner
                bl = false  // Bottom-left corner
            )
            c.drawPath(path, mRenderPaint)

            if (drawBorder) {
                val borderPath = roundRect(buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2], buffer.buffer[j + 3], radius, radius, true, true, false, false)
                c.drawPath(borderPath, mBarBorderPaint)
            }
            j += 4
        }
    }

    // Helper to create a path for a rectangle with specific rounded corners
    private fun roundRect(left: Float, top: Float, right: Float, bottom: Float, rx: Float, ry: Float, tl: Boolean, tr: Boolean, br: Boolean, bl: Boolean): Path {
        var newRx = rx
        var newRy = ry
        val path = Path()
        if (newRx < 0) newRx = 0f
        if (newRy < 0) newRy = 0f
        val width = right - left
        val height = bottom - top
        if (newRx > width / 2) newRx = width / 2
        if (newRy > height / 2) newRy = height / 2
        val widthMinusCorners = width - 2 * newRx
        val heightMinusCorners = height - 2 * newRy

        path.moveTo(right, top + newRy)
        if (tr) path.rQuadTo(0f, -newRy, -newRx, -newRy) else { path.rLineTo(0f, -newRy); path.rLineTo(-newRx, 0f) }
        path.rLineTo(-widthMinusCorners, 0f)
        if (tl) path.rQuadTo(-newRx, 0f, -newRx, newRy) else { path.rLineTo(-newRx, 0f); path.rLineTo(0f, newRy) }
        path.rLineTo(0f, heightMinusCorners)
        if (bl) path.rQuadTo(0f, newRy, newRx, newRy) else { path.rLineTo(0f, newRy); path.rLineTo(newRx, 0f) }
        path.rLineTo(widthMinusCorners, 0f)
        if (br) path.rQuadTo(newRx, 0f, newRx, -newRy) else { path.rLineTo(newRx, 0f); path.rLineTo(0f, -newRy) }
        path.rLineTo(0f, -heightMinusCorners)

        path.close()
        return path
    }
}