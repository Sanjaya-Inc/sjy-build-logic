package com.sanjaya.buildlogic.common.utils

import org.gradle.api.Project
import org.koin.core.parameter.ParametersHolder
import org.koin.core.parameter.parametersOf
import org.koin.java.KoinJavaComponent

object ComponentProvider {
    inline fun <reified T : Any> provide(
        noinline parameterHolder: () -> ParametersHolder = { parametersOf() }
    ): T = KoinJavaComponent.inject<T>(T::class.java, parameters = parameterHolder).value

    inline fun <reified T : Any> provide(project: Project) = provide<T> { parametersOf(project) }
}
