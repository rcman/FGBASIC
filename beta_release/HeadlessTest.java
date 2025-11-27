import java.nio.file.*;

/**
 * Headless test runner - runs BASIC programs without GUI
 * Perfect for benchmarking the optimization
 */
public class HeadlessTest {
    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("Usage: java HeadlessTest <program.bas>");
            System.exit(1);
        }

        String filename = args[0];
        String code = new String(Files.readAllBytes(Paths.get(filename)));

        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("Running: " + filename);
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println();

        Interpreter interpreter = new Interpreter();
        GraphicsWindow graphics = new GraphicsWindow();
        interpreter.setGraphicsWindow(graphics);

        long startTime = System.nanoTime();

        interpreter.loadProgram(code);

        long loadTime = System.nanoTime();
        System.out.println("Program loaded in " + String.format("%.3f", (loadTime - startTime) / 1_000_000.0) + " ms");
        System.out.println();

        // Run in separate thread
        Thread runThread = new Thread(() -> {
            try {
                interpreter.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        runThread.start();
        runThread.join(10000); // Wait max 10 seconds

        long endTime = System.nanoTime();

        if (runThread.isAlive()) {
            System.out.println("⚠️  Program timed out after 10 seconds");
            interpreter.stop();
            runThread.interrupt();
            runThread.join(1000);
        } else {
            System.out.println();
            System.out.println("═══════════════════════════════════════════════════════");
            System.out.println("✅ Program completed successfully");
            double totalTime = (endTime - startTime) / 1_000_000.0;
            double execTime = (endTime - loadTime) / 1_000_000.0;
            System.out.println("Total time: " + String.format("%.3f", totalTime) + " ms");
            System.out.println("Execution time: " + String.format("%.3f", execTime) + " ms");
            System.out.println("═══════════════════════════════════════════════════════");
        }
    }
}
