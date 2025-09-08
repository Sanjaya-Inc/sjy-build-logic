package com.sanjaya.buildlogic.common.components

import com.sanjaya.buildlogic.common.utils.C
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import kotlin.test.assertEquals

class BuildLogicLoggerTest {
    private val outContent = ByteArrayOutputStream()
    private val originalOut = System.out
    private val nl = System.lineSeparator()
    private lateinit var logger: BuildLogicLogger

    @BeforeEach
    fun setUp() {
        System.setOut(PrintStream(outContent))
        logger = BuildLogicLogger()
    }

    @AfterEach
    fun tearDown() {
        System.setOut(originalOut)
        outContent.reset()
    }

    @Test
    fun `i(message) should print the message with the global tag`() {
        val message = "This is an info message"

        logger.i(message)

        val expected = "${C.GLOBAL_TAG}: $message$nl"
        assertEquals(expected, outContent.toString())
    }

    @Test
    fun `i(tag, message) should print the message with both global and specific tags`() {
        val tag = "MyTag"
        val message = "This is a tagged message"

        logger.i(tag, message)

        val expected = "${C.GLOBAL_TAG}[$tag]: $message$nl"
        assertEquals(expected, outContent.toString())
    }

    @Test
    fun `title(tag, message) should print a formatted title with separators`() {
        val tag = "SETUP"
        val message = "Configuring project"
        val separator =
            "======================================================================================="

        logger.title(tag, message)

        val expected = buildString {
            append("${C.GLOBAL_TAG}: $separator$nl")
            append("${C.GLOBAL_TAG}[$tag]: $message$nl")
            append("${C.GLOBAL_TAG}: $separator$nl")
        }
        assertEquals(expected, outContent.toString())
    }

    @Test
    fun `i(tag, message) with a blank tag should be treated as if there were no tag`() {
        val message = "Message with blank tag"

        logger.i(" ", message)

        val expected = "${C.GLOBAL_TAG}: $message$nl"
        assertEquals(expected, outContent.toString())
    }
}
