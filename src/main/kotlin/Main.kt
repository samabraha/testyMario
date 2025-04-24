import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import kotlinx.coroutines.delay
import vm.GameViewModel

@Composable
@Preview
fun App(viewModel: GameViewModel) {

    LaunchedEffect(Unit) {
        while (true) {
            viewModel.update()
            delay(16)
        }
    }

    MaterialTheme {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(
                color = Color.Red,
                topLeft = Offset(viewModel.player.x, viewModel.player.y),
                size = Size(viewModel.player.width, viewModel.player.height)
            )
        }
    }
}

fun main() = application {

    val viewModel = remember { GameViewModel() }

    Window(
        state = rememberWindowState(width = GameViewModel.EDGE_X.dp, height = GameViewModel.GROUND_Y.dp),
        onCloseRequest = ::exitApplication,
        onKeyEvent = { keyEvent ->
            if (keyEvent.type == KeyEventType.KeyDown) {
                when (keyEvent.key) {
                    Key.A, Key.DirectionLeft -> {
                        viewModel.moveLeft()
                        true
                    }

                    Key.D, Key.DirectionRight -> {
                        viewModel.moveRight()
                        true
                    }

                    else -> false
                }
            } else false
        }) {

        App(viewModel)
    }
}
