import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.jar.*;
import javax.tools.*;

/**
 * Compiler - Compiles BASIC programs into standalone executables
 */
public class Compiler {

    /**
     * Compiles a BASIC program into a standalone JAR file
     * @param programCode The BASIC source code
     * @param outputName The name for the output files (without extension)
     * @param outputDir The directory to create output files in
     * @return true if compilation successful
     */
    public static boolean compile(String programCode, String outputName, File outputDir) {
        try {
            // Create output directory if it doesn't exist
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }

            // Create a temporary directory for compilation
            File tempDir = new File(outputDir, "temp_compile_" + System.currentTimeMillis());
            tempDir.mkdirs();

            // Step 1: Create StandaloneRunner with embedded program code
            System.out.println("Embedding program code...");
            File runnerFile = createEmbeddedRunner(programCode, tempDir);

            // Step 2: Copy all necessary Java source files to temp directory
            System.out.println("Copying source files...");
            List<File> sourceFiles = copySourceFiles(tempDir);
            sourceFiles.add(runnerFile);

            // Step 3: Compile all Java files
            System.out.println("Compiling Java files...");
            if (!compileJavaFiles(sourceFiles, tempDir)) {
                System.err.println("Compilation failed");
                return false;
            }

            // Step 4: Create JAR file
            System.out.println("Creating JAR file...");
            File jarFile = new File(outputDir, outputName + ".jar");
            if (!createJar(tempDir, jarFile)) {
                System.err.println("JAR creation failed");
                return false;
            }

            // Step 5: Create launcher scripts
            System.out.println("Creating launcher scripts...");
            createLaunchers(outputDir, outputName, jarFile);

            // Step 6: Clean up temp directory
            deleteDirectory(tempDir);

            System.out.println("Compilation successful!");
            System.out.println("Output: " + jarFile.getAbsolutePath());
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static File createEmbeddedRunner(String programCode, File tempDir) throws IOException {
        // Escape the program code for embedding in Java string
        String escapedCode = programCode
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n\" +\n            \"")
            .replace("\r", "");

        // Read the StandaloneRunner template
        File templateFile = new File("StandaloneRunner.java");
        String template = new String(Files.readAllBytes(templateFile.toPath()));

        // Replace the placeholder with actual code
        String runnerCode = template.replace("\"<<PROGRAM_CODE>>\"",
            "\"" + escapedCode + "\"");

        // Write to temp directory
        File runnerFile = new File(tempDir, "StandaloneRunner.java");
        Files.write(runnerFile.toPath(), runnerCode.getBytes());

        return runnerFile;
    }

    private static List<File> copySourceFiles(File tempDir) throws IOException {
        List<File> copiedFiles = new ArrayList<>();
        String[] sourceFiles = {
            "Interpreter.java",
            "GraphicsWindow.java",
            "SpriteSystem.java",
            "SoundSystem.java",
            "NetworkSystem.java",
            "TurtleGraphics.java",
            "Graphics3D.java",
            "InputSystem.java",
            "ExpressionParser.java",
            "CommandParser.java",
            "StringFunctions.java"
        };

        for (String fileName : sourceFiles) {
            File source = new File(fileName);
            if (source.exists()) {
                File dest = new File(tempDir, fileName);
                Files.copy(source.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
                copiedFiles.add(dest);
            } else {
                System.err.println("Warning: " + fileName + " not found");
            }
        }

        return copiedFiles;
    }

    private static boolean compileJavaFiles(List<File> sourceFiles, File outputDir) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            System.err.println("No Java compiler available. Make sure you're running with JDK, not JRE.");
            return false;
        }

        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);

        Iterable<? extends JavaFileObject> compilationUnits =
            fileManager.getJavaFileObjectsFromFiles(sourceFiles);

        List<String> options = Arrays.asList(
            "-d", outputDir.getAbsolutePath(),
            "-source", "1.8",
            "-target", "1.8"
        );

        JavaCompiler.CompilationTask task = compiler.getTask(
            null, fileManager, null, options, null, compilationUnits);

        boolean success = task.call();

        try {
            fileManager.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return success;
    }

    private static boolean createJar(File classDir, File jarFile) throws IOException {
        Manifest manifest = new Manifest();
        manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
        manifest.getMainAttributes().put(Attributes.Name.MAIN_CLASS, "StandaloneRunner");

        try (JarOutputStream jos = new JarOutputStream(
                new FileOutputStream(jarFile), manifest)) {

            addDirectoryToJar(classDir, classDir, jos);
        }

        return true;
    }

    private static void addDirectoryToJar(File baseDir, File source, JarOutputStream jos) throws IOException {
        File[] files = source.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                addDirectoryToJar(baseDir, file, jos);
            } else if (file.getName().endsWith(".class")) {
                String entryName = baseDir.toPath().relativize(file.toPath()).toString();
                entryName = entryName.replace(File.separatorChar, '/');

                JarEntry entry = new JarEntry(entryName);
                entry.setTime(file.lastModified());
                jos.putNextEntry(entry);

                Files.copy(file.toPath(), jos);
                jos.closeEntry();
            }
        }
    }

    private static void createLaunchers(File outputDir, String outputName, File jarFile) throws IOException {
        // Create Unix/Linux/Mac launcher script
        File unixLauncher = new File(outputDir, outputName + ".sh");
        String unixScript = "#!/bin/bash\n" +
            "# Launcher script for " + outputName + "\n" +
            "java -jar \"$(dirname \"$0\")/" + jarFile.getName() + "\" \"$@\"\n";
        Files.write(unixLauncher.toPath(), unixScript.getBytes());
        unixLauncher.setExecutable(true);

        // Create Windows launcher script
        File winLauncher = new File(outputDir, outputName + ".bat");
        String winScript = "@echo off\r\n" +
            "REM Launcher script for " + outputName + "\r\n" +
            "java -jar \"%~dp0" + jarFile.getName() + "\" %*\r\n";
        Files.write(winLauncher.toPath(), winScript.getBytes());

        System.out.println("Created launcher scripts:");
        System.out.println("  Linux/Mac: " + unixLauncher.getAbsolutePath());
        System.out.println("  Windows: " + winLauncher.getAbsolutePath());
    }

    private static void deleteDirectory(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
        directory.delete();
    }
}
