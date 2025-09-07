package com.sanjaya.buildlogic.common.components

import com.sanjaya.buildlogic.common.utils.ComponentProvider
import org.gradle.api.Project
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam
import org.koin.core.component.KoinComponent

@Factory
class PluginApplicator(
    @InjectedParam private val project: Project,
    private val logger: BuildLogicLogger,
    private val pluginFinder: PluginFinder = ComponentProvider.provide(project)
) : KoinComponent {

    fun applyPluginsByIds(vararg ids: String) {
        logger.i(TAG, "Applying plugin by ids: ")
        ids.forEach { applyPluginById(it) }
    }

    fun applyPluginsByAliases(vararg aliases: String) {
        logger.i(TAG, "Applying plugin by aliases: ")
        aliases.forEach { applyPluginByAlias(it) }
    }

    private fun applyPluginById(id: String) {
        project.pluginManager.apply(id)
        logger.i(TAG, "----> $id")
    }

    private fun applyPluginByAlias(alias: String) {
        val pluginId = pluginFinder.find(alias).pluginId
        project.pluginManager.apply(pluginId)
        logger.i(TAG, "----> $alias")
    }

    companion object {
        private const val TAG = "PluginApplicator"
    }
}