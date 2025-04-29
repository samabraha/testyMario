package vm

import model.Player
import model.Rect
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


class GameViewModelTest {
    private lateinit var viewModel: GameViewModel

    @BeforeEach
    fun `initialize viewModel`() {
        viewModel = GameViewModel()
    }

    @Test
    fun `player falls down if not on ground`() {
        viewModel.player.y = GameViewModel.GROUND_Y - GameViewModel.PLAYER_HEIGHT - 100f
        val initialY = viewModel.player.y
        viewModel.update()
        assertEquals(initialY + 2f, viewModel.player.y)
    }

    @Test
    fun `player moves left`() {
        val initialX = viewModel.player.x
        viewModel.moveLeft()
        assertEquals(initialX - 5f, viewModel.player.x)
    }

    @Test
    fun `player moves right`() {
        val initialX = viewModel.player.x
        viewModel.moveRight()
        assertEquals(initialX + 5f, viewModel.player.x)
    }

    @Test
    fun `player does not fall below ground level`() {
        repeat(250) {
            viewModel.update()
        }

        assertTrue(viewModel.player.y <= GameViewModel.GROUND_Y)
    }

    @Test
    fun `player does not move past left edge`() {
        repeat(250) { viewModel.moveLeft() }
        assertTrue(viewModel.player.x >= 0f)
    }


    @Test
    fun `player does not move past right edge`() {
        repeat(250) { viewModel.moveRight() }
        assertTrue(viewModel.player.x <= GameViewModel.WORLD_WIDTH)
    }

    @Test
    fun `player jumps up`() {
        val initialY = viewModel.player.y
        viewModel.jump()
        viewModel.update()
        assertTrue(viewModel.player.y < initialY)
    }

    @Test
    fun `player lands on platform`() {
        viewModel.platforms = listOf(
            Rect(50f, 100f, 100f, 20f),

            Rect(x = GameViewModel.GAME_WIDTH, y = 0f, with = 0f, height = GameViewModel.GAME_HEIGHT), // right line
        )
        viewModel.player = Player(60f, 50f)
        repeat(10) { viewModel.update() }
        assertEquals(68f, viewModel.player.y)
    }

    @Test
    fun `player changes speed`() {
        val initialSpeed = viewModel.currentSpeed

        // Simulate user input
        viewModel.movingLeft = true
        viewModel.update()

        assertTrue(viewModel.currentSpeed > initialSpeed)
    }

    @Test
    fun `camera follows player horizontally if outside dead zone`() {
        viewModel.update()

        val playerStartX = GameViewModel.GAME_WIDTH * viewModel.rightDZRatio + 10f
        viewModel.player = viewModel.player.copy(x = playerStartX)

        val initialCameraX = viewModel.cameraX

        viewModel.update()

        assertTrue(
            viewModel.cameraX > initialCameraX,
            "Expected cameraX to increase after player passed right dead zone"
        )

    }

    @Test
    fun `camera does not move when player inside dead zone`() {
        val initialCameraX = 100f
        viewModel.player = Player(x = initialCameraX + GameViewModel.GAME_WIDTH * 0.4f)
        val result = viewModel.calculateCameraX(initialCameraX)

        assertEquals(initialCameraX, result)
    }

    @Test
    fun `camera moves left when player is left of dead zone`() {
        val initialCameraX = 100f
        val playerX = initialCameraX + GameViewModel.GAME_WIDTH * 0.2f
        viewModel.player = Player(x = playerX)

        val result = viewModel.calculateCameraX(currCameraX = initialCameraX)

        assertTrue(result < initialCameraX)
        assertEquals((playerX - GameViewModel.GAME_WIDTH * 0.25f).coerceAtLeast(0f), result)
    }

    @Test
    fun `camera moves right when player is right of dead zone`() {
        val initialCameraX = 100f
        val playerX = initialCameraX + GameViewModel.GAME_WIDTH * 0.8f
        viewModel.player = Player(x = playerX)

        val result = viewModel.calculateCameraX(initialCameraX)

        val expected = (playerX + viewModel.player.width - GameViewModel.GAME_WIDTH * 0.75f)
            .coerceAtMost(GameViewModel.WORLD_WIDTH - GameViewModel.GAME_WIDTH)

        assertTrue(result > initialCameraX)

        assertEquals(expected, result)

    }

    @Test
    fun `camera clamps to 0f when player moves too far left`() {
        viewModel.player = Player()
        val result = viewModel.calculateCameraX(0f)
        assertEquals(0f, result)
    }

    @Test
    fun `camera clamps to WORLD_WIDTH - GAME_WIDTH when player moves too far right`() {
        val playerX = GameViewModel.WORLD_WIDTH - viewModel.player.width
        viewModel.player = Player(x = playerX)

        val result = viewModel.calculateCameraX(GameViewModel.WORLD_WIDTH - GameViewModel.GAME_WIDTH)

        val maxCameraX = GameViewModel.WORLD_WIDTH - GameViewModel.GAME_WIDTH

        assertEquals(maxCameraX, result)
    }
}

