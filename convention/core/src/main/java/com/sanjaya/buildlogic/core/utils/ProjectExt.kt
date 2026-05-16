package com.sanjaya.buildlogic.core.utils

import org.gradle.api.Project
import org.koin.core.parameter.parametersOf
import org.koin.java.KoinJavaComponent.getKoin

inline fun <reified T : Any> Project.get() = getKoin().get<T>(parameters = { parametersOf(this) })
