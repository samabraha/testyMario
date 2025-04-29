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

        /** Replace platforms for testing to avoid random failures */
        viewModel.platforms = listOf(
            Rect(x = 0f, GameViewModel.GROUND_Y, GameViewModel.WORLD_WIDTH, 10f),
            Rect(x = 100f, y = 50f, with = 200f, height = 100f),
            Rect(x = 500f, y = 100f, with = 200f, height = 100f),
        )

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
        assertEquals(initialX - GameViewModel.WALK_STEP, viewModel.player.x)
    }

    @Test
    fun `player moves right`() {
        val initialX = viewModel.player.x
        viewModel.moveRight()
        assertEquals(initialX + GameViewModel.WALK_STEP, viewModel.player.x)
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
        viewModel.cameraX = 100f
        val initialCameraX = viewModel.cameraX
        val deadZoneLocation = initialCameraX + GameViewModel.GAME_WIDTH * (viewModel.leftDZRatio + 0.1f)
        viewModel.player = Player(x = deadZoneLocation)
        viewModel.calculateCameraX()

        assertEquals(initialCameraX, viewModel.cameraX)
    }

    @Test
    fun `camera moves left when player is left of dead zone`() {
        viewModel.cameraX = 100f
        val initialCameraX = viewModel.cameraX
        val playerX = initialCameraX + GameViewModel.GAME_WIDTH * 0.2f
        viewModel.player = Player(x = playerX)

        viewModel.calculateCameraX()

        assertTrue(viewModel.cameraX < initialCameraX)

        assertEquals(
            (playerX - GameViewModel.GAME_WIDTH * viewModel.leftDZRatio)
                .coerceAtLeast(0f), viewModel.cameraX
        )
    }

    @Test
    fun `camera moves right when player is right of dead zone`() {
        val initialCameraX = 100f
        val playerX = initialCameraX + GameViewModel.GAME_WIDTH * 0.8f
        viewModel.player = Player(x = playerX)

        viewModel.calculateCameraX()

        val expected = (playerX + viewModel.player.width - GameViewModel.GAME_WIDTH * viewModel.rightDZRatio)
            .coerceAtMost(GameViewModel.WORLD_WIDTH - GameViewModel.GAME_WIDTH)

        assertTrue(viewModel.cameraX > initialCameraX)

        assertEquals(expected, viewModel.cameraX)

    }

    @Test
    fun `camera clamps to 0f when player moves too far left`() {
        viewModel.player = Player()
        viewModel.calculateCameraX()
        assertEquals(0f, viewModel.cameraX)
    }

    @Test
    fun `camera clamps to WORLD_WIDTH - GAME_WIDTH when player moves too far right`() {
        val playerX = GameViewModel.WORLD_WIDTH - viewModel.player.width
        viewModel.player = Player(x = playerX)

        viewModel.calculateCameraX()

        val maxCameraX = GameViewModel.WORLD_WIDTH - GameViewModel.GAME_WIDTH

        assertEquals(maxCameraX, viewModel.cameraX)
    }
}

