package com.sanjaya.buildlogic.android.utils

import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import org.gradle.api.Project
import org.gradle.api.plugins.PluginManager
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AndroidProjectTypeCheckerTest {

    private lateinit var project: Project
    private lateinit var pluginManager: PluginManager
    private lateinit var projectTypeChecker: AndroidProjectTypeChecker

    @BeforeEach
    fun setUp() {
        project = mockk(relaxed = true)
        pluginManager = mockk(relaxed = true)

        every { project.pluginManager } returns pluginManager

        projectTypeChecker = AndroidProjectTypeChecker(project)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `isApp returns true when com_android_application plugin is applied`() {
        every { pluginManager.hasPlugin("com.android.application") } returns true

        assertTrue(projectTypeChecker.isApp())
        assertFalse(projectTypeChecker.isLib())
        assertTrue(projectTypeChecker.isAppOrLib())
    }

    @Test
    fun `isLib returns true when com_android_library plugin is applied`() {
        every { pluginManager.hasPlugin("com.android.library") } returns true

        assertFalse(projectTypeChecker.isApp())
        assertTrue(projectTypeChecker.isLib())
        assertTrue(projectTypeChecker.isAppOrLib())
    }

    @Test
    fun `isAppOrLib returns false when no Android plugin is applied`() {
        every { pluginManager.hasPlugin(any()) } returns false

        assertFalse(projectTypeChecker.isApp())
        assertFalse(projectTypeChecker.isLib())
        assertFalse(projectTypeChecker.isAppOrLib())
    }

    @Test
    fun `isAppOrLib returns true when both app and lib plugins are applied`() {
        every { pluginManager.hasPlugin("com.android.application") } returns true
        every { pluginManager.hasPlugin("com.android.library") } returns true

        assertTrue(projectTypeChecker.isApp())
        assertTrue(projectTypeChecker.isLib())
        assertTrue(projectTypeChecker.isAppOrLib())
    }
}
