import org.gradle.api.plugins.jvm.JvmTestSuite

plugins {
    `kotlin-dsl`
}


val writeConventionCoreClasspath = tasks.register<WriteConventionCoreClasspath>("writeConventionCoreClasspath") {
    group = "verification"
    description = "Write the convention core runtime classpath for TestKit"
    runtimeClasspath.setFrom(sourceSets.named("main").map { it.runtimeClasspath })
    classpathList.set(layout.buildDirectory.file("convention-core-classpath.txt"))
}

testing {
    suites {
        register<JvmTestSuite>("functionalTest") {
            useJUnitJupiter()
            dependencies {
                implementation(gradleTestKit())
            }
            targets.configureEach {
                testTask.configure {
                    inputs.file(writeConventionCoreClasspath.flatMap { it.classpathList })
                }
            }
        }
    }
}

tasks.matching { it.name == "check" }.configureEach {
    dependsOn(testing.suites.named("functionalTest"))
}

abstract class WriteConventionCoreClasspath : DefaultTask() {
    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val runtimeClasspath: ConfigurableFileCollection

    @get:OutputFile
    abstract val classpathList: RegularFileProperty

    @TaskAction
    fun writeClasspath() {
        classpathList.get().asFile.writeText(runtimeClasspath.asPath)
    }
}
