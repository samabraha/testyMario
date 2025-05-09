import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
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
import vm.GameViewModel


const val TARGET_FPS = 80


@Composable
fun App(viewModel: GameViewModel) {
    var frameTrigger by remember { mutableStateOf(0) }

    val update: (deltaTime: Float) -> Unit = viewModel::update

    val scope = rememberCoroutineScope()
    LaunchedEffect(Unit) {

        val renderer = Renderer(
            targetFPS = TARGET_FPS,
            update = update,
            onFrame = { frameTrigger++ }
        )

        renderer.start(scope)

    }

    MaterialTheme {
        Canvas(modifier = Modifier.fillMaxSize().background(Color.Black)) {
            viewModel.platforms.forEach {
                drawRect(color = it.color, topLeft = it.renderOffset(viewModel.cameraX), size = it.size)
            }
            drawRect(
                color = Color.Red,
                topLeft = Offset(viewModel.player.renderX(viewModel.cameraX), viewModel.player.y),
                size = Size(viewModel.player.width, viewModel.player.height)
            )
        }
    }

}

fun main() = application {
    val viewModel = remember { GameViewModel() }

    Window(
        state = rememberWindowState(
            width = GameViewModel.GAME_WIDTH.dp + 50.dp,
            height = GameViewModel.GROUND_Y.dp + 75.dp
        ),

        onCloseRequest = ::exitApplication,

        onKeyEvent = { keyEvent ->
            when (keyEvent.type) {
                KeyEventType.KeyDown -> {
                    when (keyEvent.key) {
                        Key.A, Key.DirectionLeft -> {
                            viewModel.movingLeft = true
                            true
                        }

                        Key.D, Key.DirectionRight -> {
                            viewModel.movingRight = true
                            true
                        }

                        Key.DirectionUp, Key.Spacebar -> {
                            viewModel.jump()
                            true
                        }

                        else -> false
                    }
                }

                KeyEventType.KeyUp -> {
                    when (keyEvent.key) {
                        Key.A, Key.DirectionLeft -> {
                            viewModel.movingLeft = false
                            true
                        }

                        Key.D, Key.DirectionRight -> {
                            viewModel.movingRight = false
                            true
                        }

                        else -> false
                    }
                }

                else -> false
            }

        }) {

        App(viewModel)
    }
}
