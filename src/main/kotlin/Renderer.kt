import androidx.compose.runtime.Composable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.skiko.currentNanoTime
import java.util.logging.Logger

val logger: Logger = Logger.getLogger("com.develogica")


class Renderer(
    private val targetFPS: Int = 60,
    private val update: (deltaTime: Float) -> Unit,
    private val render: @Composable () -> Unit
) {
    private val frameTimeNanos = 1_000_000_000L / targetFPS
    private var running = true

    fun start() {
        logger.info("Renderer started")
        CoroutineScope(Dispatchers.Default).launch {
            while (running) {
                val startTime = System.currentTimeMillis()
                update(1f / targetFPS)
                render()
                val elapsedTime = currentNanoTime() - startTime
                val sleepTime = frameTimeNanos - elapsedTime
                if (sleepTime > 0) {
                    delay(sleepTime / 1_000_000L)
                }
            }
        }

    }

    fun stop() {
        logger.info("Renderer stopped")
        running = false
    }
}