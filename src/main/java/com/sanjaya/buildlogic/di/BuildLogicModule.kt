package com.sanjaya.buildlogic.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

@Module
@ComponentScan(
    "com.sanjaya.buildlogic.components.dependency",
    "com.sanjaya.buildlogic.components.plugin",
    "com.sanjaya.buildlogic.components.misc",
    "com.sanjaya.buildlogic.components.setup"
)
class BuildLogicModule
