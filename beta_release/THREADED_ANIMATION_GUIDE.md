# Threaded Sprite Animation Guide

## Overview

The FGBasic interpreter now supports **threaded sprite animation** where each sprite can animate independently in its own thread at different speeds.

**Key features:**
- ✅ Each sprite runs in its own **regular Java thread** (not virtual threads)
- ✅ Different frame rates per sprite (1-120 FPS)
- ✅ Independent velocities
- ✅ Automatic boundary handling (bounce or wrap)
- ✅ Dynamic velocity changes
- ✅ Clean shutdown on program end

---

## ANIMATE Command Syntax

### Start Animation
```basic
ANIMATE START, id, vx, vy, fps, bounceMode
```

**Parameters:**
- `id` - Sprite ID (must be created with SPRITE CREATE first)
- `vx` - X velocity (pixels per frame, can be negative)
- `vy` - Y velocity (pixels per frame, can be negative)
- `fps` - Frames per second (1-120, will be clamped)
- `bounceMode` - Boundary behavior:
  - `0` = No boundary handling
  - `1` = Bounce off edges (reverses direction)
  - `2` = Wrap around (appears on opposite side)

**Example:**
```basic
ANIMATE START, 0, 2, 1, 30, 1
```
Sprite 0 moves at 2 pixels/frame right, 1 pixel/frame down, at 30 FPS, bouncing off edges.

---

### Stop Animation
```basic
ANIMATE STOP, id
```

Stops animation thread for the specified sprite.

**Example:**
```basic
ANIMATE STOP, 3
```

---

### Stop All Animations
```basic
ANIMATE STOPALL
```

Stops all sprite animation threads.

**Example:**
```basic
ANIMATE STOPALL
```

---

### Change Velocity
```basic
ANIMATE VELOCITY, id, vx, vy
```

Changes velocity of an already-animating sprite (thread-safe).

**Parameters:**
- `id` - Sprite ID
- `vx` - New X velocity
- `vy` - New Y velocity

**Example:**
```basic
ANIMATE VELOCITY, 0, -3, -2
REM Now sprite 0 moves left and up
```

---

## Complete Example

```basic


```

---

## How It Works

### Thread Architecture

Each sprite gets its own dedicated thread:

```
Main BASIC Program Thread
    │
    ├─→ SpriteAnimator (manages all animation threads)
    │       │
    │       ├─→ Thread 1: Sprite 0 (30 FPS)
    │       ├─→ Thread 2: Sprite 1 (60 FPS)
    │       ├─→ Thread 3: Sprite 2 (15 FPS)
    │       └─→ Thread 4: Sprite 3 (45 FPS)
    │
    └─→ BASIC execution continues...
```

### Animation Loop (per sprite)

Each thread runs:
```java
while (active) {
    // Calculate frame time
    frameTime = 1000 / fps;

    // Update position
    x += vx;
    y += vy;

    // Handle boundaries
    if (bounceMode == 1) {
        if (x < 0 || x > screenWidth) vx = -vx;
        if (y < 0 || y > screenHeight) vy = -vy;
    }

    // Update sprite on screen
    spriteSystem.moveSprite(id, x, y);
    graphics.repaint();

    // Sleep until next frame
    Thread.sleep(frameTime);
}
```

---

## Threading Details

### Thread Pool

- Uses `Executors.newCachedThreadPool()` (not virtual threads)
- Threads are daemon threads (auto-cleanup)
- Named threads: `SpriteAnimator-0`, `SpriteAnimator-1`, etc.

### Thread Safety

- Velocities stored as `volatile` for thread-safe updates
- ConcurrentHashMap for thread storage
- Synchronized access to sprite system

### Resource Management

- All threads automatically stop on program shutdown
- Clean shutdown with 2-second timeout
- Forced shutdown if needed

---

## Performance Characteristics

### FPS Impact

| FPS | Frame Time | CPU Impact |
|-----|-----------|------------|
| 10  | 100ms | Very low |
| 30  | 33ms | Low |
| 60  | 16ms | Medium |
| 120 | 8ms | Higher |

**Recommendation:** Use 30-60 FPS for smooth animation without excessive CPU usage.

### Multiple Sprites

- Each sprite runs independently
- 10 sprites at 30 FPS each = manageable
- 100+ sprites may impact performance

---

## Boundary Modes Explained

### Mode 0: No Boundary Handling
```basic
ANIMATE START, 0, 2, 1, 30, 0
```
- Sprite moves freely
- Can go off-screen
- Never stops or wraps

### Mode 1: Bounce
```basic
ANIMATE START, 0, 2, 1, 30, 1
```
- Sprite bounces off screen edges
- Direction reverses on collision
- Classic "DVD logo" behavior

**Example:**
```
Screen edge hit:
Before: vx = 2  (moving right)
After:  vx = -2 (moving left)
```

### Mode 2: Wrap
```basic
ANIMATE START, 0, 2, 1, 30, 2
```
- Sprite wraps to opposite edge
- Appears on other side
- Classic "Pac-Man" behavior

**Example:**
```
Right edge hit:
x = 640 → x = 0
```

---

## Advanced Patterns

### Random Bouncing Sprites
```basic
10 FOR I = 0 TO 9
20   SPRITE CREATE, I, 8, 8
30   REM Fill with color...
40   SPRITE SHOW, I
50   VX = (RND * 6) - 3
60   VY = (RND * 6) - 3
70   FPS = 20 + RND * 40
80   ANIMATE START, I, VX, VY, FPS, 1
90 NEXT I
```

### Sprite Following Pattern
```basic
10 REM Leader sprite
20 ANIMATE START, 0, 2, 0, 30, 1

30 REM Follower sprite (slower)
40 ANIMATE START, 1, 1.5, 0, 30, 1
```

### Orbiting Sprites (Manual Control)
```basic
10 ANGLE = 0
20 WHILE ANGLE < 360
30   VX = COS(ANGLE) * 2
40   VY = SIN(ANGLE) * 2
50   ANIMATE VELOCITY, 0, VX, VY
60   WAIT 0.1
70   ANGLE = ANGLE + 10
80 WEND
```

---

## Differences from Loop-Based Animation

### Traditional Loop Approach
```basic
10 WHILE 1
20   FOR I = 0 TO 9
30     SX(I) = SX(I) + VX(I)
40     SPRITE MOVE, I, SX(I), SY(I)
50   NEXT I
60 WEND
```
**Problems:**
- All sprites same speed (limited by loop speed)
- BASIC program blocked during animation
- Can't run other code while animating

### Threaded Approach
```basic
10 FOR I = 0 TO 9
20   ANIMATE START, I, VX(I), VY(I), FPS(I), 1
30 NEXT I
40 REM Program continues, sprites animate independently!
50 PRINT "Doing other work..."
```
**Benefits:**
- ✅ Each sprite has independent speed
- ✅ BASIC program continues running
- ✅ Can do other work while sprites animate
- ✅ Different frame rates per sprite

---

## Troubleshooting

### Sprite not animating
- Check sprite was created with `SPRITE CREATE`
- Check sprite is visible with `SPRITE SHOW`
- Check FPS is in range 1-120
- Check velocity is non-zero

### Choppy animation
- Reduce FPS (try 30 instead of 60)
- Reduce number of animated sprites
- Check CPU usage

### Sprites all same speed
- Make sure you're using different FPS values
- Check velocities are different

### Program hangs on exit
- Animations auto-stop on shutdown
- If stuck, may need to force-quit

---

## Best Practices

1. **Use reasonable FPS:** 30-60 FPS is smooth enough
2. **Stop animations explicitly:** Call `ANIMATE STOPALL` before ending
3. **Don't create too many threads:** Limit to 20-50 animated sprites
4. **Use WAIT for timing:** Let animations run, don't busy-loop
5. **Test different speeds:** Experiment with FPS values

---

## Summary

The threaded animation system provides:

✅ **Independent animation** - Each sprite moves at its own speed
✅ **Regular Java threads** - No virtual threads, standard threading
✅ **Clean API** - Simple ANIMATE commands
✅ **Automatic management** - Threads start/stop cleanly
✅ **Thread-safe** - Safe concurrent sprite updates
✅ **High performance** - Double-pass optimization applies!

**Use Case:** Perfect for games, simulations, and visual demos where sprites need different movement patterns and speeds.

---

## Example Program

See `threaded_sprite_demo.bas` for a complete working example with 8 sprites at different speeds!

```bash
java Main threaded_sprite_demo.bas
```
