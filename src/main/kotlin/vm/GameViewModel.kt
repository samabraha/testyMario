package vm

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import model.Player

class GameViewModel {
    var player by mutableStateOf(Player(50f, 50f))

    fun update() {
        if (player.y < GROUND_Y - GRAVITY) {
            player = player.copy(y = player.y + GRAVITY)
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

    companion object {
        const val GROUND_Y = 600f
        const val EDGE_X = 800f
        const val GRAVITY = 2f
        const val WALK_STEP = 5f
    }
}