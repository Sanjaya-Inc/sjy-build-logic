package com.sanjaya.buildlogic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.gradle.testkit.runner.TaskOutcome;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.File;

class SyncIosAppVersionTaskTest {

    @TempDir
    File projectDir;

    @Test
    void writesXcconfigFromTaskProperties() throws IOException {
        BuildResult result = runner("syncIosAppVersion").build();

        assertEquals(TaskOutcome.SUCCESS, result.task(":syncIosAppVersion").getOutcome());
        String xcconfig = Files.readString(xcconfigFile().toPath(), StandardCharsets.UTF_8);
        assertTrue(xcconfig.contains("CURRENT_PROJECT_VERSION=45"));
        assertTrue(xcconfig.contains("MARKETING_VERSION=1.2.3"));
    }

    @Test
    void writesTheSameBytesOnASecondRun() throws IOException {
        runner("syncIosAppVersion").build();
        byte[] first = Files.readAllBytes(xcconfigFile().toPath());

        BuildResult second = runner("syncIosAppVersion").build();

        assertEquals(TaskOutcome.UP_TO_DATE, second.task(":syncIosAppVersion").getOutcome());
        assertTrue(Arrays.equals(first, Files.readAllBytes(xcconfigFile().toPath())));
    }

    @Test
    void reusesTheCachedOutput() throws IOException {
        runner("syncIosAppVersion", "--build-cache").build();
        byte[] first = Files.readAllBytes(xcconfigFile().toPath());
        assertTrue(xcconfigFile().delete());

        BuildResult cached = runner("syncIosAppVersion", "--build-cache").build();

        assertEquals(TaskOutcome.FROM_CACHE, cached.task(":syncIosAppVersion").getOutcome());
        assertTrue(Arrays.equals(first, Files.readAllBytes(xcconfigFile().toPath())));
    }

    private GradleRunner runner(String... arguments) throws IOException {
        writeBuildScript();
        return GradleRunner.create()
            .withProjectDir(projectDir)
            .withArguments(Arrays.asList(arguments))
            .forwardOutput();
    }

    private void writeBuildScript() throws IOException {
        String classpath = Files.readString(classpathFile().toPath(), StandardCharsets.UTF_8);
        String notation = Arrays.stream(classpath.split(File.pathSeparator))
            .filter(path -> !path.isBlank())
            .map(path -> "\"" + path.replace("\\", "\\\\").replace("\"", "\\\"") + "\"")
            .collect(Collectors.joining(", "));
        String script = """
            buildscript {
                dependencies {
                    classpath(files(%s))
                }
            }

            plugins {
                id("base")
            }

            tasks.register<com.sanjaya.buildlogic.SyncIosAppVersionTask>("syncIosAppVersion") {
                group = "versioning"
                description = "Write Version.xcconfig"
                versionName.set("1.2.3")
                versionCode.set("45")
                outputFile.set(layout.projectDirectory.file("Version.xcconfig"))
            }
            """.formatted(notation);
        Files.writeString(new File(projectDir, "build.gradle.kts").toPath(), script, StandardCharsets.UTF_8);
        Files.writeString(new File(projectDir, "settings.gradle.kts").toPath(), "rootProject.name = \"ios-version-fixture\"\n", StandardCharsets.UTF_8);
    }

    private File classpathFile() {
        return new File("build/convention-core-classpath.txt");
    }

    private File xcconfigFile() {
        return new File(projectDir, "Version.xcconfig");
    }
}
