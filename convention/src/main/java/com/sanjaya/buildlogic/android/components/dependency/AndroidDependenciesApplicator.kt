package com.sanjaya.buildlogic.android.components.dependency

import com.sanjaya.buildlogic.common.components.BuildLogicLogger
import com.sanjaya.buildlogic.common.components.DependenciesFinder
import com.sanjaya.buildlogic.common.utils.ComponentProvider
import org.gradle.api.Project
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.provider.Provider
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam

@Factory
class AndroidDependenciesApplicator(
    @InjectedParam private val project: Project,
    private val logger: BuildLogicLogger,
    private val dependenciesFinder: DependenciesFinder = ComponentProvider.provide(project)
) : DependenciesApplicator {

    override fun implementation(notation: Provider<MinimalExternalModuleDependency>) {
        project.dependencies.add("implementation", notation)
        notation.orNull?.toString()?.let {
            logger.i(TAG, "----> $it")
        }
    }

    override fun implementations(vararg alias: String) {
        logger.i(TAG, "Adding library implementations: ")
        alias.forEach {
            implementation(it)
        }
    }

    override fun implementation(alias: String) {
        val dependency = dependenciesFinder.findLibrary(alias)
        implementation(dependency)
    }

    override fun kapt(notation: Provider<MinimalExternalModuleDependency>) {
        project.dependencies.add("kapt", notation)
        logger.i(TAG, "Adding library kapt: ${notation.orNull?.name}")
    }

    override fun kapt(alias: String) {
        val dependency = dependenciesFinder.findLibrary(alias)
        kapt(dependency)
    }

    override fun ksp(notation: Provider<MinimalExternalModuleDependency>) {
        project.dependencies.add("ksp", notation)
        logger.i(TAG, "Adding ksp implementation: ${notation.orNull?.name}")
    }

    override fun ksp(alias: String) {
        val dependency = dependenciesFinder.findLibrary(alias)
        ksp(dependency)
    }

    override fun implementationPlatform(notation: Provider<MinimalExternalModuleDependency>) {
        project.dependencies.add("implementation", project.dependencies.platform(notation))
        logger.i(TAG, "Adding platform library implementation: ${notation.orNull?.name}")
    }

    override fun implementationPlatform(alias: String) {
        val dependency = dependenciesFinder.findLibrary(alias)
        implementationPlatform(dependency)
    }

    override fun apiPlatform(notation: Provider<MinimalExternalModuleDependency>) {
        project.dependencies.add("api", project.dependencies.platform(notation))
        logger.i(TAG, "Adding platform library implementation: ${notation.orNull?.name}")
    }

    override fun apiPlatform(alias: String) {
        val dependency = dependenciesFinder.findLibrary(alias)
        apiPlatform(dependency)
    }

    override fun testImplementationPlatform(notation: Provider<MinimalExternalModuleDependency>) {
        project.dependencies.add("testImplementation", project.dependencies.platform(notation))
        logger.i(TAG, "Adding platform test implementation: ${notation.orNull?.name}")
    }

    override fun testImplementationPlatform(alias: String) {
        val dependency = dependenciesFinder.findLibrary(alias)
        testImplementationPlatform(dependency)
    }

    override fun androidTestImplementationPlatform(notation: Provider<MinimalExternalModuleDependency>) {
        project.dependencies.add(
            "androidTestImplementation",
            project.dependencies.platform(notation)
        )
        logger.i(
            TAG,
            "Adding android test platform library implementation: ${notation.orNull?.name}"
        )
    }

    override fun androidTestImplementationPlatform(alias: String) {
        val dependency = dependenciesFinder.findLibrary(alias)
        androidTestImplementationPlatform(dependency)
    }

    override fun androidTestImplementation(notation: Provider<MinimalExternalModuleDependency>) {
        project.dependencies.add("androidTestImplementation", notation)
        logger.i(TAG, "Adding android test library implementation: ${notation.orNull?.name}")
    }

    override fun androidTestImplementation(alias: String) {
        val dependency = dependenciesFinder.findLibrary(alias)
        androidTestImplementation(dependency)
    }

    override fun debugImplementation(notation: Provider<MinimalExternalModuleDependency>) {
        project.dependencies.add("debugImplementation", notation)
        notation.orNull?.toString()?.let {
            logger.i(TAG, "----> $it")
        }
    }

    override fun debugImplementation(alias: String) {
        val dependency = dependenciesFinder.findLibrary(alias)
        debugImplementation(dependency)
    }

    override fun debugImplementations(vararg alias: String) {
        logger.i(TAG, "Adding library debug implementations: ")
        alias.forEach { debugImplementation(it) }
    }

    override fun testImplementation(notation: Provider<MinimalExternalModuleDependency>) {
        project.dependencies.add("testImplementation", notation)
        logger.i(TAG, "Adding test library implementation: ${notation.orNull?.name}")
    }

    override fun testImplementation(alias: String) {
        val dependency = dependenciesFinder.findLibrary(alias)
        testImplementation(dependency)
    }

    override fun detektPlugin(notation: Provider<MinimalExternalModuleDependency>) {
        project.dependencies.add("detektPlugins", notation)
    }

    override fun detektPlugin(alias: String) {
        val dependency = dependenciesFinder.findLibrary(alias)
        detektPlugin(dependency)
    }

    override fun detektPlugins(vararg aliases: String) {
        logger.i(TAG, "Adding detekt plugins: ")
        aliases.forEach { detektPlugin(it) }
    }

    override fun testImplementations(vararg alias: String) {
        logger.i(TAG, "Adding library test implementations: ")
        alias.forEach {
            testImplementation(it)
        }
    }

    override fun androidTestImplementations(vararg alias: String) {
        logger.i(TAG, "Adding library android test implementations: ")
        alias.forEach {
            androidTestImplementation(it)
        }
    }

    companion object {
        private const val TAG = "AndroidDependenciesApplicator"
    }
}
