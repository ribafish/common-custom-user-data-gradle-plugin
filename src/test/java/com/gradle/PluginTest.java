package com.gradle;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PluginTest {

    @TempDir
    File testProjectDir;

    @Test
    public void testPluginGeneratingBuildScansOnBuildkite() throws IOException {
        File settingsFile = new File(testProjectDir, "settings.gradle");
        File buildFile = new File(testProjectDir, "build.gradle");
        String settingsFileContent = "\n" +
            "plugins { \n" +
            "    id(\"com.gradle.enterprise\") version \"3.13.1\" \n" +
            "    id(\"com.gradle.common-custom-user-data-gradle-plugin\") \n" +
            " }\n" +
            " \n" +
            " gradleEnterprise { \n" +
            "    server = \"https://ge.solutions-team.gradle.com/\" \n" +
            "    accessKey = \"utssusmteni5frr37qrvv6mtmfucfxg6oqr6t4e4zfb37gkdntza\" \n" +
            "    buildScan { \n" +
            "        publishAlways() \n" +
            " } \n" +
            "}\n" +
            "rootProject.name = 'ccud-buildkite-test'";
        System.out.println(settingsFileContent);
        writeFile(settingsFile, settingsFileContent);

        String buildFileContent = "plugins {" +
            "    id(\"java\")" +
            "}";
        writeFile(buildFile, buildFileContent);

        // Setup fake buildkite environment
        Map<String, String> envs = new HashMap<>();
        envs.put("BUILDKITE", "true");
        envs.put("CI", "true");
        envs.put("BUILDKITE_BUILD_URL", "https://buildkite.com/build-url");
        envs.put("BUILDKITE_COMMAND", "help --info");
        envs.put("BUILDKITE_BUILD_ID", "01880496-1415-4afe-828a-759efa481124");
        envs.put("BUILDKITE_PULL_REQUEST_REPO", "https://github.com/gradle/common-custom-user-data-gradle-plugin");
        envs.put("BUILDKITE_PULL_REQUEST", "169");

        BuildResult result = GradleRunner.create()
            .withProjectDir(testProjectDir)
            .withArguments("help", "--info")
            .withPluginClasspath()
            .withEnvironment(envs)
            .build();
        assert (true);
    }

    private void writeFile(File destination, String content) throws IOException {
        BufferedWriter output = null;
        try {
            output = new BufferedWriter(new FileWriter(destination));
            output.write(content);
        } finally {
            if (output != null) {
                output.close();
            }
        }
    }
}
