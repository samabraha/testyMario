package vm

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


class GameViewModelTest {
    private lateinit var viewModel: GameViewModel

    @BeforeEach
    fun `initialize viewModel`() {
        viewModel = GameViewModel()
    }

    @Test
    fun playerFallsDown() {
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
        assertTrue(viewModel.player.x <= GameViewModel.EDGE_X)
    }
}