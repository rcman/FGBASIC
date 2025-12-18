import java.util.*;
import java.util.concurrent.*;

/**
 * Sprite animation system using separate threads for each sprite.
 * Each sprite can animate at different speeds independently.
 *
 * Uses regular Java threads (not virtual threads).
 */
public class SpriteAnimator {
    private final SpriteSystem spriteSystem;
    private final GraphicsWindow graphics;
    private final Map<Integer, SpriteAnimationThread> animationThreads;
    private final ExecutorService threadPool;
    private volatile boolean running;

    public SpriteAnimator(SpriteSystem spriteSystem, GraphicsWindow graphics) {
        this.spriteSystem = spriteSystem;
        this.graphics = graphics;
        this.animationThreads = new ConcurrentHashMap<>();
        // Fixed thread pool - no virtual threads
        this.threadPool = Executors.newCachedThreadPool(new ThreadFactory() {
            private int threadCount = 0;
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setName("SpriteAnimator-" + (threadCount++));
                t.setDaemon(true);
                return t;
            }
        });
        this.running = true;
    }

    /**
     * Start animating a sprite with given parameters
     * @param spriteId The sprite ID
     * @param vx X velocity (pixels per update)
     * @param vy Y velocity (pixels per update)
     * @param fps Frames per second for this sprite
     * @param bounceMode 0=none, 1=bounce off edges, 2=wrap around
     */
    public void startAnimation(int spriteId, double vx, double vy, int fps, int bounceMode) {
        // Stop existing animation if any
        stopAnimation(spriteId);

        // Create and start new animation thread
        SpriteAnimationThread animThread = new SpriteAnimationThread(
            spriteId, vx, vy, fps, bounceMode);
        animationThreads.put(spriteId, animThread);
        threadPool.execute(animThread);
    }

    /**
     * Stop animating a specific sprite
     */
    public void stopAnimation(int spriteId) {
        SpriteAnimationThread thread = animationThreads.remove(spriteId);
        if (thread != null) {
            thread.stop();
        }
    }

    /**
     * Stop all sprite animations
     */
    public void stopAll() {
        running = false;
        for (SpriteAnimationThread thread : animationThreads.values()) {
            thread.stop();
        }
        animationThreads.clear();
    }

    /**
     * Shutdown the animator and all threads
     */
    public void shutdown() {
        stopAll();
        threadPool.shutdown();
        try {
            if (!threadPool.awaitTermination(2, TimeUnit.SECONDS)) {
                threadPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            threadPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Update sprite velocity
     */
    public void setVelocity(int spriteId, double vx, double vy) {
        SpriteAnimationThread thread = animationThreads.get(spriteId);
        if (thread != null) {
            thread.setVelocity(vx, vy);
        }
    }

    /**
     * Get current sprite position
     */
    public double[] getPosition(int spriteId) {
        SpriteAnimationThread thread = animationThreads.get(spriteId);
        if (thread != null) {
            return thread.getPosition();
        }
        return new double[]{0, 0};
    }

    /**
     * Individual sprite animation thread
     */
    private class SpriteAnimationThread implements Runnable {
        private final int spriteId;
        private volatile double vx;
        private volatile double vy;
        private final int fps;
        private final int bounceMode;
        private volatile boolean active;
        private double x;
        private double y;

        public SpriteAnimationThread(int spriteId, double vx, double vy, int fps, int bounceMode) {
            this.spriteId = spriteId;
            this.vx = vx;
            this.vy = vy;
            this.fps = Math.max(1, Math.min(fps, 120)); // Clamp to 1-120 fps
            this.bounceMode = bounceMode;
            this.active = true;

            // Get current sprite position from sprite system
            SpriteSystem.Sprite sprite = spriteSystem.getSprite(spriteId);
            if (sprite != null) {
                this.x = sprite.x;
                this.y = sprite.y;
            } else {
                this.x = 0;
                this.y = 0;
            }
        }

        public void setVelocity(double vx, double vy) {
            this.vx = vx;
            this.vy = vy;
        }

        public double[] getPosition() {
            return new double[]{x, y};
        }

        public void stop() {
            active = false;
        }

        @Override
        public void run() {
            long frameTimeMs = 1000 / fps;
            long lastUpdateTime = System.currentTimeMillis();

            // Get sprite size
            SpriteSystem.Sprite sprite = spriteSystem.getSprite(spriteId);
            int spriteWidth = sprite != null ? sprite.width : 8;
            int spriteHeight = sprite != null ? sprite.height : 8;

            while (active && running) {
                try {
                    long currentTime = System.currentTimeMillis();
                    long elapsed = currentTime - lastUpdateTime;

                    if (elapsed >= frameTimeMs) {
                        // Update position
                        x += vx;
                        y += vy;

                        // Handle boundary conditions
                        int screenWidth = GraphicsWindow.SCREEN_WIDTH;
                        int screenHeight = GraphicsWindow.SCREEN_HEIGHT;

                        switch (bounceMode) {
                            case 1: // Bounce off edges
                                if (x < 0) {
                                    x = 0;
                                    vx = -vx;
                                } else if (x > screenWidth - spriteWidth) {
                                    x = screenWidth - spriteWidth;
                                    vx = -vx;
                                }

                                if (y < 0) {
                                    y = 0;
                                    vy = -vy;
                                } else if (y > screenHeight - spriteHeight) {
                                    y = screenHeight - spriteHeight;
                                    vy = -vy;
                                }
                                break;

                            case 2: // Wrap around
                                if (x < 0) x = screenWidth - spriteWidth;
                                if (x > screenWidth - spriteWidth) x = 0;
                                if (y < 0) y = screenHeight - spriteHeight;
                                if (y > screenHeight - spriteHeight) y = 0;
                                break;

                            // case 0: no boundary handling
                        }

                        // Update sprite position in sprite system (use setSpritePosition for absolute positioning)
                        spriteSystem.setSpritePosition(spriteId, (int)x, (int)y);

                        // Request repaint
                        if (graphics != null) {
                            graphics.repaint();
                        }

                        lastUpdateTime = currentTime;
                    }

                    // Sleep for a short time to avoid busy-waiting
                    // Using regular Thread.sleep (not virtual threads)
                    Thread.sleep(Math.min(frameTimeMs, 10));

                } catch (InterruptedException e) {
                    active = false;
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }

    /**
     * Get status information
     */
    public String getStatus() {
        return "Active animations: " + animationThreads.size();
    }

    /**
     * Check if a sprite is being animated
     */
    public boolean isAnimating(int spriteId) {
        return animationThreads.containsKey(spriteId);
    }
}
