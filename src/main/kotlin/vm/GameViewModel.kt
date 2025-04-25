package vm

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import model.Player

class GameViewModel {
    var player by mutableStateOf(Player(50f, GROUND_Y - PLAYER_HEIGHT))

    private var verticalVelocity = GRAVITY

    fun update() {
        if (player.y < GROUND_Y - player.height - GRAVITY || verticalVelocity < 0f) {
            player = player.copy(y = player.y + verticalVelocity)
            verticalVelocity += GRAVITY
        } else {
            verticalVelocity = 0f
            player = player.copy(y = GROUND_Y)
        }
    }

    fun moveLeft() {
        if (player.x > WALK_STEP) {
            player = player.copy(x = player.x - WALK_STEP)
        }
    }

    fun moveRight() {
        if (player.x < EDGE_X - WALK_STEP) {
            player = player.copy(x = player.x + WALK_STEP)
        }
    }

    fun jump() {
        if (player.y >= GROUND_Y - player.height) {
            verticalVelocity = -20f
        }
    }

    companion object {
        const val GROUND_Y = 600f
        const val EDGE_X = 800f
        const val GRAVITY = 2f
        const val WALK_STEP = 5f

        const val PLAYER_HEIGHT= 32f
    }
}