import androidx.compose.runtime.Composable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.skiko.currentNanoTime
import java.util.logging.Logger

val logger: Logger = Logger.getLogger("com.develogica")


class Renderer(
    targetFPS: Int = 60,
    private val update: (deltaTime: Float) -> Unit,
    private val onFrame:() -> Unit
) {
    private val frameTimeNanos = 1_000_000_000L / targetFPS
    private var running = true

    fun start(scope: CoroutineScope) {
        logger.info("Renderer started")
        scope.launch {
            var lastTime = currentNanoTime()
            while (running) {
                val currentTime = currentNanoTime()
                val deltaTime = (currentTime - lastTime) / 1_000_000_000f
                lastTime = currentTime 

                update(deltaTime)
                onFrame()
                
                val elapsedTime = currentNanoTime() - currentTime
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