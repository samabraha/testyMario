package vm

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import model.Player
import model.Rect
import kotlin.random.Random

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

    var platforms: List<Rect> = buildList {
        add(Rect(x = 0f, y = GAME_HEIGHT, with = WORLD_WIDTH, height = 10f)) // bottom line

        repeat(30) {
            add(
                Rect(
                    x = Random.nextFloat() * WORLD_WIDTH,
                    y = Random.nextFloat() * WORLD_HEIGHT,
                    with = Random.nextFloat() * 200,
                    height = Random.nextFloat() * 100
                )
            )
        }
    }

    fun update(dt: Float) {
        updateSpeed()

        if (movingLeft) moveLeft()
        if (movingRight) moveRight()

        updateVerticalMovement()

        calculateCameraX()
    }

    private fun updateVerticalMovement() {
        val nextY = player.y + verticalVelocity
        val nextRect = Rect(player.x, nextY, player.width, player.height)

        val collidedPlatform = platforms.firstOrNull { it.intersects(nextRect) }
        if (collidedPlatform != null) {
            if (verticalVelocity > 0f) {

                player = player.copy(y = collidedPlatform.y - player.height)
            } else if (verticalVelocity < 0f) {

                player = player.copy(y = collidedPlatform.y + collidedPlatform.height)
            }
            verticalVelocity = 0f
        } else {
            player = player.copy(y = nextY)
            verticalVelocity += GRAVITY
        }

    }

    private fun updateSpeed() {
        currentSpeed = if (movingLeft || movingRight) {
            (currentSpeed + acceleration).coerceAtMost(maxSpeed)
        } else WALK_STEP

    }

    fun calculateCameraX() {
        val deadZoneLeft = cameraX + GAME_WIDTH * leftDZRatio
        val deadZoneRight = cameraX + GAME_WIDTH * rightDZRatio
        val playerRight = player.x + player.width

        cameraX = when {
            player.x < deadZoneLeft -> (player.x - GAME_WIDTH * leftDZRatio).coerceAtLeast(0f)

            playerRight > deadZoneRight -> (playerRight - GAME_WIDTH * rightDZRatio).coerceAtMost(WORLD_WIDTH - GAME_WIDTH)

            else -> cameraX
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
        if (isStandingOnPlatform()) {
            verticalVelocity = -20f
        }
    }

    fun isStandingOnPlatform(): Boolean {
        val feetY = player.y + player.height
        val footRect = Rect(x = player.x, y = feetY, player.width, height = 1f)
        return platforms.any { platform ->
            platform.intersects(footRect) && feetY <= platform.y + 1f
        }
    }


    companion object {
        const val GAME_WIDTH = 800f
        const val GAME_HEIGHT = 600f
        const val WORLD_WIDTH = 3690f
        const val WORLD_HEIGHT = GAME_HEIGHT

        const val GROUND_Y = 600f
        const val GRAVITY = 2f
        const val WALK_STEP = 5f

        const val PLAYER_HEIGHT = 32f
    }
}

