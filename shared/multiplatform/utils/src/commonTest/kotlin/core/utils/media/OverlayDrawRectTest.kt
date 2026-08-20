package core.utils.media

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class OverlayDrawRectTest {

    @Test
    fun `bottom-aligns overlay without stretching to export height`() {
        val dest = overlayDrawRect(
            exportWidth = 1080,
            exportHeight = 1920,
            overlayWidth = 1080,
            overlayHeight = 600,
        )

        assertEquals(0f, dest.left)
        assertEquals(1320f, dest.top)
        assertEquals(1080f, dest.width)
        assertEquals(600f, dest.height)
        assertEquals(1080f, dest.right)
        assertEquals(1920f, dest.bottom)
    }

    @Test
    fun `scales overlay width to export width`() {
        val dest = overlayDrawRect(
            exportWidth = 1080,
            exportHeight = 1920,
            overlayWidth = 540,
            overlayHeight = 300,
        )

        assertEquals(0f, dest.left)
        assertEquals(1320f, dest.top)
        assertEquals(1080f, dest.width)
        assertEquals(600f, dest.height)
    }

    @Test
    fun `rejects non-positive overlay size`() {
        assertFailsWith<IllegalArgumentException> {
            overlayDrawRect(1080, 1920, 0, 600)
        }
        assertFailsWith<IllegalArgumentException> {
            overlayDrawRect(1080, 1920, 1080, 0)
        }
    }
}
