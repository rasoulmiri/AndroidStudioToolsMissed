package androidstudio.tools.missed.utils

import java.awt.Color
import java.awt.image.BufferedImage
import javax.swing.Icon
import javax.swing.ImageIcon

fun Icon.colored(color: Color): Icon {
    val image = BufferedImage(this.iconWidth, this.iconHeight, BufferedImage.TYPE_INT_ARGB)
    val graphics = image.createGraphics()

    this.paintIcon(null, graphics, 0, 0)

    graphics.color = color
    graphics.composite = java.awt.AlphaComposite.SrcAtop.derive(1f)
    graphics.fillRect(0, 0, this.iconWidth, this.iconHeight)

    graphics.dispose()
    return ImageIcon(image)
}
