import androidx.compose.ui.geometry.Offset
import model.Player
import model.Rect


fun Player.renderX(cameraX: Float) = this.x - cameraX
fun Rect.renderX(cameraX: Float) = this.x - cameraX
fun Rect.renderOffset(cameraX: Float) = Offset(this.x - cameraX, this.y)