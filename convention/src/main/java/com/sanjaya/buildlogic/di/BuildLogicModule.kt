package com.sanjaya.buildlogic.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

@Module
@ComponentScan(
    "com.sanjaya.buildlogic.android.components.dependency",
    "com.sanjaya.buildlogic.android.components.plugin",
    "com.sanjaya.buildlogic.android.components.misc",
    "com.sanjaya.buildlogic.android.components.setup",
    "com.sanjaya.buildlogic.common.components",
    "com.sanjaya.buildlogic.multiplatform.components",
)
object BuildLogicModule
