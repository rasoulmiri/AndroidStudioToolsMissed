package androidstudio.tools.missed.utils

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import java.awt.Color
import java.awt.Graphics
import java.awt.image.BufferedImage
import javax.swing.Icon
import javax.swing.ImageIcon
import org.junit.After
import org.junit.Before
import org.junit.Test


class IconExtensionTest {

    @Test
    fun `colored should return an icon with the specified color`() {
        // Arrange
        val originalIcon = mockk<Icon>()
        val iconWidth = 10
        val iconHeight = 10
        every { originalIcon.iconWidth } returns iconWidth
        every { originalIcon.iconHeight } returns iconHeight
        every { originalIcon.paintIcon(any(), any(), any(), any()) } answers {
            val graphics = it.invocation.args[1] as Graphics
            graphics.color = Color.BLACK
            graphics.fillRect(0, 0, iconWidth, iconHeight)
        }

        val color = Color.RED

        // Act
        val coloredIcon = originalIcon.colored(color)

        // Assert
        assertTrue(coloredIcon is ImageIcon)
        val image = (coloredIcon as ImageIcon).image
        assertTrue(image is BufferedImage)
        val bufferedImage = image as BufferedImage
        assertEquals(iconWidth, bufferedImage.width)
        assertEquals(iconHeight, bufferedImage.height)

        // Verify the color change
        for (x in 0 until iconWidth) {
            for (y in 0 until iconHeight) {
                assertEquals(color.rgb, bufferedImage.getRGB(x, y))
            }
        }

        verify { originalIcon.paintIcon(null, any(), 0, 0) }
    }
}
