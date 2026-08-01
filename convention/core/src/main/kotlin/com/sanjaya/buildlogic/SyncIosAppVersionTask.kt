package com.sanjaya.buildlogic

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

@CacheableTask
abstract class SyncIosAppVersionTask : DefaultTask() {

    @get:Input
    abstract val versionName: Property<String>

    @get:Input
    abstract val versionCode: Property<String>

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    @TaskAction
    fun writeXcconfig() {
        val name = versionName.get()
        val code = versionCode.get()
        outputFile.get().asFile.writeText(
            """
            |// Generated from gradle/libs.versions.toml — do not edit by hand.
            |// Regenerate: ./gradlew :shared:syncIosAppVersion
            |CURRENT_PROJECT_VERSION=$code
            |MARKETING_VERSION=$name
            |
            """.trimMargin(),
        )
    }
}
