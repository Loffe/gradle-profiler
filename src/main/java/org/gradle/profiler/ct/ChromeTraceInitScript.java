/*
 * Copyright 2003-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gradle.profiler.ct;

import org.gradle.internal.UncheckedException;
import org.gradle.profiler.GeneratedInitScript;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class ChromeTraceInitScript extends GeneratedInitScript {
    private final File chromeTracePlugin;
    private final File traceFile;

    public ChromeTraceInitScript(File outputDir) {
        try {
            chromeTracePlugin = File.createTempFile("chrome-trace", "jar");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        chromeTracePlugin.deleteOnExit();
        traceFile = new File(outputDir, "chrome-trace.html");
    }

    private void unpackChromeTracePlugin() {
        InputStream inputStream = getClass().getResourceAsStream("/META-INF/jars/chrome-trace.jar");
        try {
            Files.copy(inputStream, chromeTracePlugin.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw UncheckedException.throwAsUncheckedException(e);
        }
    }

    @Override
    public void writeContents(final PrintWriter writer) {
        unpackChromeTracePlugin();
        writer.write("initscript {\n");
        writer.write("    dependencies {\n");
        writer.write("        classpath files(\"" + chromeTracePlugin.getAbsolutePath() + "\")\n");
        writer.write("    }\n");
        writer.write("}\n");
        writer.write("\n");
        writer.write("rootProject { ext.chromeTraceFile = new File(\"" + traceFile.getAbsolutePath() + "\") }\n");
        writer.write("apply plugin: org.gradle.trace.GradleTracingPlugin\n");
    }
}
