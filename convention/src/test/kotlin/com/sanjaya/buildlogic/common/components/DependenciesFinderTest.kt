package com.sanjaya.buildlogic.common.components

import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import org.gradle.api.Project
import org.gradle.api.artifacts.ExternalModuleDependencyBundle
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.internal.provider.Providers
import org.gradle.api.provider.Provider
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.jvm.optionals.getOrNull
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class DependenciesFinderTest {

    lateinit var project: Project
    lateinit var buildLogicLogger: BuildLogicLogger
    lateinit var versionCatalogProvider: VersionCatalogProvider
    lateinit var dependenciesFinder: DependenciesFinder
    lateinit var catalog: VersionCatalog

    @BeforeEach
    fun setup() {
        project = mockk(relaxed = true)
        buildLogicLogger = mockk(relaxed = true)
        versionCatalogProvider = mockk(relaxed = true)
        catalog = mockk(relaxed = true)

        every { versionCatalogProvider.getAll() } returns listOf(catalog)

        dependenciesFinder = DependenciesFinder(
            project,
            buildLogicLogger,
            versionCatalogProvider
        )
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `findLibrary should return dependency when alias exists`() {
        val alias = "koin-core"
        val dependency: MinimalExternalModuleDependency = mockk()
        val provider: Provider<MinimalExternalModuleDependency> = Providers.of(dependency)

        every { catalog.name } returns "libs"
        every { catalog.findLibrary(alias).getOrNull() } returns provider

        val result = dependenciesFinder.findLibrary(alias)

        assertEquals(dependency, result.get())
        verify {
            buildLogicLogger.i(
                "DependenciesFinder",
                "----> Found $alias on version catalog: libs"
            )
        }
    }

    @Test
    fun `findLibrary should throw when alias does not exist`() {
        val alias = "nonexistent"

        every { catalog.findLibrary(alias).getOrNull() } returns null

        val ex = assertFailsWith<IllegalArgumentException> {
            dependenciesFinder.findLibrary(alias)
        }
        assertEquals(
            "[DependenciesFinder]: Cannot find plugin with alias: $alias. Please check your version catalog.",
            ex.message
        )
    }

    @Test
    fun `findBundle should return bundle when alias exists`() {
        val alias = "koin-bundle"
        val bundle: ExternalModuleDependencyBundle = mockk()
        val provider: Provider<ExternalModuleDependencyBundle> = Providers.of(bundle)

        every { catalog.findBundle(alias).getOrNull() } returns provider

        val result = dependenciesFinder.findBundle(alias)

        assertEquals(bundle, result.get())
    }

    @Test
    fun `findBundle should throw when alias does not exist`() {
        val alias = "missing-bundle"

        every { catalog.findBundle(alias).getOrNull() } returns null

        val ex = assertFailsWith<IllegalArgumentException> {
            dependenciesFinder.findBundle(alias)
        }
        assertEquals(
            "[DependenciesFinder]: Cannot find plugin with alias: $alias. Please check your version catalog.",
            ex.message
        )
    }
}
