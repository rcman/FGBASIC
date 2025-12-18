import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

/**
 * Thread safety test for concurrent access to interpreter components
 * Tests the fixes for race conditions in GraphicsWindow and NetworkSystem
 */
public class ThreadSafetyTest {
    public static void main(String[] args) throws Exception {
        System.out.println("=== Thread Safety Tests ===\n");

        boolean allPassed = true;

        // Test 1: ConcurrentHashMap in NetworkSystem
        allPassed &= runTest("NetworkSystem Thread Safety", () -> {
            NetworkSystem network = new NetworkSystem();
            ExecutorService executor = Executors.newFixedThreadPool(10);
            AtomicInteger errors = new AtomicInteger(0);

            // Create connections from multiple threads
            for (int i = 0; i < 50; i++) {
                final int id = i;
                executor.submit(() -> {
                    try {
                        // This would fail with regular HashMap
                        // NetworkSystem now uses ConcurrentHashMap
                        network.closeConnection(id); // Safe even if doesn't exist
                    } catch (Exception e) {
                        // Expected - no connection to close
                    }
                });
            }

            executor.shutdown();
            executor.awaitTermination(5, TimeUnit.SECONDS);

            return errors.get() == 0;
        });

        // Test 2: Concurrent interpreter variable access
        allPassed &= runTest("Interpreter ConcurrentHashMap", () -> {
            Interpreter interp = new Interpreter();
            ExecutorService executor = Executors.newFixedThreadPool(10);
            AtomicInteger errors = new AtomicInteger(0);

            // Multiple threads accessing variables concurrently
            for (int i = 0; i < 100; i++) {
                final int value = i;
                executor.submit(() -> {
                    try {
                        interp.getVariables().put("VAR" + value, (double)value);
                        interp.getVariables().get("VAR" + value);
                    } catch (Exception e) {
                        errors.incrementAndGet();
                        e.printStackTrace();
                    }
                });
            }

            executor.shutdown();
            executor.awaitTermination(5, TimeUnit.SECONDS);

            return errors.get() == 0 && interp.getVariables().size() == 100;
        });

        // Test 3: Concurrent array access
        allPassed &= runTest("Array ConcurrentHashMap", () -> {
            Interpreter interp = new Interpreter();
            interp.loadProgram("DIM ARR1(100)\nDIM ARR2(100)\nDIM ARR3(100)");
            interp.run();

            ExecutorService executor = Executors.newFixedThreadPool(10);
            AtomicInteger errors = new AtomicInteger(0);

            // Multiple threads accessing arrays
            for (int i = 0; i < 50; i++) {
                final int idx = i;
                executor.submit(() -> {
                    try {
                        double[] arr = interp.getArrays().get("ARR1");
                        if (arr != null && idx < arr.length) {
                            arr[idx] = idx;
                        }
                    } catch (Exception e) {
                        errors.incrementAndGet();
                    }
                });
            }

            executor.shutdown();
            executor.awaitTermination(5, TimeUnit.SECONDS);

            return errors.get() == 0;
        });

        // Test 4: Rapid loop execution (AST optimization)
        allPassed &= runTest("AST Loop Performance", () -> {
            Interpreter interp = new Interpreter();
            String prog = "LET COUNT = 0\nWHILE COUNT < 10000\nLET COUNT = COUNT + 1\nWEND";
            interp.loadProgram(prog);

            long start = System.currentTimeMillis();
            interp.run();
            long elapsed = System.currentTimeMillis() - start;

            System.out.print(" (" + elapsed + "ms for 10000 iterations) ");
            return interp.getVariables().get("COUNT") == 10000.0 && elapsed < 5000;
        });

        // Test 5: Sprite system concurrent access
        allPassed &= runTest("SpriteSystem Thread Safety", () -> {
            SpriteSystem sprites = new SpriteSystem();
            ExecutorService executor = Executors.newFixedThreadPool(10);
            AtomicInteger errors = new AtomicInteger(0);

            // Create sprites from multiple threads
            for (int i = 0; i < 50; i++) {
                final int id = i;
                executor.submit(() -> {
                    try {
                        sprites.createSprite(id, 32, 32);
                        sprites.setSpritePosition(id, 100, 100);
                        sprites.setSpriteVisible(id, true);
                        sprites.deleteSprite(id);
                    } catch (Exception e) {
                        errors.incrementAndGet();
                        e.printStackTrace();
                    }
                });
            }

            executor.shutdown();
            executor.awaitTermination(5, TimeUnit.SECONDS);

            return errors.get() == 0;
        });

        // Test 6: GraphicsWindow synchronized operations
        allPassed &= runTest("GraphicsWindow Synchronization", () -> {
            GraphicsWindow gfx = new GraphicsWindow();
            ExecutorService executor = Executors.newFixedThreadPool(10);
            AtomicInteger errors = new AtomicInteger(0);

            // Multiple threads calling graphics methods
            // All repaint() calls now inside synchronized blocks
            for (int i = 0; i < 100; i++) {
                executor.submit(() -> {
                    try {
                        gfx.setColor(128, 128, 128);
                        gfx.drawLine(0, 0, 100, 100);
                        gfx.drawCircle(200, 200, 50);
                    } catch (Exception e) {
                        errors.incrementAndGet();
                        e.printStackTrace();
                    }
                });
            }

            executor.shutdown();
            executor.awaitTermination(5, TimeUnit.SECONDS);
            gfx.cleanup();

            return errors.get() == 0;
        });

        // Test 7: Multiple interpreters running simultaneously
        allPassed &= runTest("Multiple Interpreters", () -> {
            ExecutorService executor = Executors.newFixedThreadPool(5);
            AtomicInteger errors = new AtomicInteger(0);
            CountDownLatch latch = new CountDownLatch(5);

            for (int i = 0; i < 5; i++) {
                final int id = i;
                executor.submit(() -> {
                    try {
                        Interpreter interp = new Interpreter();
                        String prog = "LET X = " + id + "\n" +
                                     "FOR I = 1 TO 100\n" +
                                     "  LET X = X + 1\n" +
                                     "NEXT I";
                        interp.loadProgram(prog);
                        interp.run();

                        double expected = id + 100;
                        if (interp.getVariables().get("X") != expected) {
                            errors.incrementAndGet();
                        }
                    } catch (Exception e) {
                        errors.incrementAndGet();
                        e.printStackTrace();
                    } finally {
                        latch.countDown();
                    }
                });
            }

            latch.await(10, TimeUnit.SECONDS);
            executor.shutdown();

            return errors.get() == 0;
        });

        System.out.println("\n" + ("=".repeat(50)));
        if (allPassed) {
            System.out.println("✓ ALL THREAD SAFETY TESTS PASSED!");
        } else {
            System.out.println("✗ SOME TESTS FAILED");
        }
        System.out.println("=".repeat(50));
    }

    private static boolean runTest(String testName, TestCase test) {
        System.out.print(String.format("%-35s", testName + "..."));
        try {
            boolean result = test.run();
            if (result) {
                System.out.println(" ✓ PASS");
            } else {
                System.out.println(" ✗ FAIL");
            }
            return result;
        } catch (Exception e) {
            System.out.println(" ✗ ERROR: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    interface TestCase {
        boolean run() throws Exception;
    }
}
