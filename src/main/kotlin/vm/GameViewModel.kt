package vm

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import model.Player
import model.Rect

class GameViewModel {
    var player by mutableStateOf(Player(50f, GROUND_Y - PLAYER_HEIGHT))
    var cameraX = 0f

    private var verticalVelocity = GRAVITY
    val leftDZRatio = 0.3f
    val rightDZRatio = 0.7f
    val topDZRatio = 0.2f

    var currentSpeed = WALK_STEP
    private val maxSpeed = 10f
    private val acceleration = 0.5f

    var movingLeft = false
    var movingRight = false

    var platforms = listOf(
        Rect(x = 0f, y = GAME_HEIGHT, with = WORLD_WIDTH, height = 10f), // bottom line
        Rect(200f, 500f, 200f, 24f),
        Rect(550f, 550f, 100f, 32f),
        Rect(800f, 500f, 450f, 32f),
        Rect(950f, 400f, 150f, 72f),
        Rect(1000f, 250f, 150f, 48f),
        Rect(1180f, 480f, 50f, 96f),
        Rect(1250f, 360f, 450f, 48f),
        Rect(1440f, 50f, 100f, 24f),
        Rect(1780f, 220f, 80f, 50f),
        Rect(1900f, 250f, 120f, 40f),
        Rect(2000f, 220f, 40f, 40f),
        Rect(2150f, 400f, 60f, 80f),
        Rect(2380f, 180f, 150f, 40f),
        Rect(2400f, 550f, 30f, 40f),
        Rect(2630f, 540f, 100f, 60f),
        Rect(2850f, 80f, 40f, 40f),
        Rect(3040f, 490f, 120f, 80f),
        Rect(3233f, 500f, 80f, 140f),
        Rect(3435f, 220f, 120f, 65f),
        Rect(3780f, 50f, 160f, 40f),
    )

    fun update() {
        currentSpeed = if (movingLeft || movingRight) {
            (currentSpeed + acceleration).coerceAtMost(maxSpeed)
        } else WALK_STEP

        if (movingLeft) moveLeft()
        if (movingRight) moveRight()

        val nextY = player.y + verticalVelocity
        val nextRect = Rect(player.x, nextY, player.width, player.height)

        val topPlatform = platforms.firstOrNull { it.intersects(nextRect) }
        if (topPlatform != null) {
            verticalVelocity = 0f

            player = player.copy(y = topPlatform.y - player.height)
        } else {
            player = player.copy(y = nextY)
            verticalVelocity += GRAVITY
        }

        cameraX = calculateCameraX(cameraX)
    }

    fun calculateCameraX(currCameraX: Float): Float {
        val deadZoneLeft = currCameraX + GAME_WIDTH * leftDZRatio
        val deadZoneRight = currCameraX + GAME_WIDTH * rightDZRatio
        val playerRight = player.x + player.width

        return when {
            player.x < deadZoneLeft -> (player.x - GAME_WIDTH * leftDZRatio)
                .coerceAtLeast(0f)

            playerRight > deadZoneRight -> (playerRight - GAME_WIDTH * rightDZRatio)
                .coerceAtMost(WORLD_WIDTH - GAME_WIDTH)

            else -> currCameraX
        }
    }

    fun moveLeft() {
        val nextX = player.x - WALK_STEP
        val nextRect = Rect(x = nextX, y = player.y, with = player.width, height = player.height)

        val blockingPlatform = platforms.firstOrNull { it.intersects(nextRect) }

        if (blockingPlatform == null && nextX >= 0f) {
            player = player.copy(x = nextX)
        } else if (blockingPlatform != null) {
            val distanceToWall = player.x - (blockingPlatform.x + blockingPlatform.width)
            if (distanceToWall in 0f..WALK_STEP) {
                player = player.copy(x = blockingPlatform.x + blockingPlatform.width)
            }
        }
    }

    fun moveRight() {
        val nextX = player.x + WALK_STEP
        val nextRect = Rect(x = nextX, y = player.y, with = player.width, height = player.height)

        val blockingPlatform = platforms.firstOrNull { it.intersects(nextRect) }

        if (blockingPlatform == null && nextX + player.width <= WORLD_WIDTH) {
            player = player.copy(x = nextX)
        } else if (blockingPlatform != null) {
            val distanceToWall = blockingPlatform.x - (player.x + player.width)
            if (distanceToWall in 0f..WALK_STEP) {
                player = player.copy(x = blockingPlatform.x - player.width)
            }
        }
    }

    fun jump() {
        val standingRect = Rect(player.x, player.y + 1f, player.width, player.height)
        val isStanding = platforms.any { it.intersects(standingRect) }

        if (isStanding) {
            verticalVelocity = -20f
        }
    }

    companion object {
        const val GAME_WIDTH = 800f
        const val GAME_HEIGHT = 600f
        const val WORLD_WIDTH = 3840f
        const val WORLD_HEIGHT = GAME_HEIGHT

        const val GROUND_Y = 600f
        const val GRAVITY = 2f
        const val WALK_STEP = 5f

        const val PLAYER_HEIGHT = 32f
    }
}

