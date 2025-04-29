package model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import kotlin.random.Random

class Rect {
    var x: Float = 0f
    var y: Float = 0f
    var width: Float = 32f
    var height: Float = 32f

    val offset get() = Offset(x = x, y = y)
    val size get() = Size(width = width, height = height)

    val color: Color = Color(Random.nextInt())

    constructor(x: Float, y: Float, with: Float, height: Float) {
        this.x = x
        this.y = y
        this.width = with
        this.height = height
    }

    constructor(offset: Offset, size: Size) {
        x = offset.x
        y = offset.y
        width = size.width
        height = size.height
    }

    /** Returns true if other [other] intersects with this one in all 4 directions. */
    fun intersects(other: Rect): Boolean {
        return x < other.x + other.width
                && x + width > other.x
                && y < other.y + other.height
                && y + height > other.y
    }
}