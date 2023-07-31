package androidstudio.tools.missed.utils

import java.awt.AlphaComposite
import java.awt.Component
import java.awt.Graphics
import java.awt.Graphics2D
import javax.swing.Icon

class DisabledIcon(private val originalIcon: Icon, private val transparency: Float = 0.2f) : Icon {
    override fun paintIcon(c: Component, g: Graphics, x: Int, y: Int) {
        val g2d = g.create() as Graphics2D
        g2d.composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, transparency)
        originalIcon.paintIcon(c, g2d, x, y)
        g2d.dispose()
    }

    override fun getIconWidth() = originalIcon.iconWidth

    override fun getIconHeight() = originalIcon.iconHeight
}
