package com.sanjaya.buildlogic.components.dependency

import com.sanjaya.buildlogic.components.dependency.AndroidDependenciesApplicator.Companion
import com.sanjaya.buildlogic.components.misc.BuildLogicLogger
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

@Factory
class KotlinDependenciesApplicator(
    @InjectedParam private val handler: KotlinDependencyHandler,
    private val logger: BuildLogicLogger
) : DependenciesApplicator {

    private val dependenciesFinder: DependenciesFinder by inject { parametersOf(handler.project) }

    override fun implementation(notation: Provider<MinimalExternalModuleDependency>) {
        handler.implementation(notation)
        logger.i(TAG, "Adding library implementation: ${notation.orNull?.name}")
    }

    override fun implementation(alias: String) {
        val notation = dependenciesFinder.findLibrary(alias)
        handler.implementation(notation)
    }

    override fun implementations(vararg alias: String) {
        alias.forEach { implementation(it) }
    }

    override fun kapt(notation: Provider<MinimalExternalModuleDependency>) {
        handler.project.dependencies.add("kapt", notation)
        logger.i(TAG, "Adding library kapt: ${notation.orNull?.name}")
    }

    override fun kapt(alias: String) {
        val notation = dependenciesFinder.findLibrary(alias)
        kapt(notation)
    }

    override fun ksp(notation: Provider<MinimalExternalModuleDependency>) {
        handler.project.dependencies.add("ksp", notation)
        logger.i(TAG, "Adding ksp implementation: ${notation.orNull?.name}")
    }

    override fun ksp(alias: String) {
        val notation = dependenciesFinder.findLibrary(alias)
        ksp(notation)
    }

    override fun implementationPlatform(notation: Provider<MinimalExternalModuleDependency>) {
        handler.implementation(handler.project.dependencies.platform(notation))
        logger.i(TAG, "Adding platform library implementation: ${notation.orNull?.name}")
    }

    override fun implementationPlatform(alias: String) {
        val notation = dependenciesFinder.findLibrary(alias)
        implementationPlatform(notation)
    }

    override fun apiPlatform(notation: Provider<MinimalExternalModuleDependency>) {
        handler.api(handler.project.dependencies.platform(notation))
        logger.i(TAG, "Adding platform library implementation: ${notation.orNull?.name}")
    }

    override fun apiPlatform(alias: String) {
        val notation = dependenciesFinder.findLibrary(alias)
        apiPlatform(notation)
    }

    override fun testImplementationPlatform(notation: Provider<MinimalExternalModuleDependency>) {
        handler.project.dependencies.add(
            "testImplementation",
            handler.project.dependencies.platform(notation)
        )
        logger.i(
            TAG,
            "Adding android test platform implementation: ${notation.orNull?.name}"
        )
    }

    override fun testImplementationPlatform(alias: String) {
        val dependency = dependenciesFinder.findLibrary(alias)
        testImplementationPlatform(dependency)
    }

    override fun androidTestImplementationPlatform(notation: Provider<MinimalExternalModuleDependency>) {
        handler.project.dependencies.add(
            "androidTestImplementation",
            handler.project.dependencies.platform(notation)
        )
        logger.i(
            TAG,
            "Adding android test platform library implementation: ${notation.orNull?.name}"
        )
    }

    override fun androidTestImplementationPlatform(alias: String) {
        val notation = dependenciesFinder.findLibrary(alias)
        androidTestImplementationPlatform(notation)
    }

    override fun androidTestImplementation(notation: Provider<MinimalExternalModuleDependency>) {
        handler.project.dependencies.add("androidTestImplementation", notation)
        logger.i(TAG, "Adding android test library implementation: ${notation.orNull?.name}")
    }

    override fun androidTestImplementation(alias: String) {
        val notation = dependenciesFinder.findLibrary(alias)
        androidTestImplementation(notation)
    }

    override fun debugImplementation(notation: Provider<MinimalExternalModuleDependency>) {
        handler.project.dependencies.add("debugImplementation", notation)
        logger.i(TAG, "Adding debug library implementation: ${notation.orNull?.name}")
    }

    override fun debugImplementation(alias: String) {
        val notation = dependenciesFinder.findLibrary(alias)
        debugImplementation(notation)
    }

    override fun debugImplementations(vararg alias: String) {
        alias.forEach { debugImplementation(it) }
    }

    override fun testImplementation(notation: Provider<MinimalExternalModuleDependency>) {
        handler.project.dependencies.add("testImplementation", notation)
        logger.i(TAG, "Adding test library implementation: ${notation.orNull?.name}")
    }

    override fun testImplementation(alias: String) {
        val notation = dependenciesFinder.findLibrary(alias)
        testImplementation(notation)
    }

    override fun detektPlugin(notation: Provider<MinimalExternalModuleDependency>) {
        handler.project.dependencies.add("detektPlugins", notation)
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
        private const val TAG = "KotlinDependenciesApplicator"
    }
}
