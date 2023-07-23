package androidstudio.tools.missed.features.input.model

import org.junit.Assert.assertEquals
import org.junit.Test

class EventKeyTest {

    @Test
    fun `EventKey value should match expected value`() {
        // Arrange
        val expectedDoneValue = "66"
        val expectedNextValue = "61"

        // Act
        val actualDoneValue = EventKey.DONE.value
        val actualNextValue = EventKey.NEXT.value

        // Assert
        assertEquals(expectedDoneValue, actualDoneValue)
        assertEquals(expectedNextValue, actualNextValue)
    }

    @Test
    fun `EventKey should have correct enum values`() {
        // Arrange
        val expectedEventKeys = listOf("DONE", "NEXT")

        // Act
        val actualEventKeys = EventKey.values().map { it.name }

        // Assert
        assertEquals(expectedEventKeys, actualEventKeys)
    }
}
