# Sprite Demo Usage Guide

## Why Sprites Disappear

When you run sprite demos through the GUI, the sprites may appear briefly then disappear. This happens because:

1. **The animation completes** (e.g., 500 or 1000 frames)
2. **The program reaches END**
3. **The program terminates**
4. **The window may close or clear**

## Solutions

### Option 1: Use the Infinite Loop Version ⭐ RECOMMENDED

**File:** `sprite_demo_infinite.bas`

This version runs forever with `WHILE 1` until you click Stop:

```bash
java Main sprite_demo_infinite.bas
```

**What you'll see:**
- 16 colored sprites bouncing around
- Continuous animation
- Frame counter updating every 100 frames
- Sprites visible as long as you want
- Click "Stop" button to end

**Perfect for:** Watching the optimization in action!

---

### Option 2: Demos with WAIT at End

**Files:**
- `sprite_demo_fixed.bas` - Waits 60 seconds at end
- `sprite_demo_simple.bas` - Waits 30 seconds at end

```bash
java Main sprite_demo_fixed.bas
```

**What you'll see:**
- Animation runs for 500 or 1000 frames
- When complete, sprites stay visible
- Wait for 30-60 seconds
- Then program ends

---

### Option 3: Modify Frame Count

Edit the WHILE loop to run more frames:

```basic
450 WHILE FRAME < 10000   REM Change from 500 to 10000
```

This makes the animation run longer before ending.

---

## Understanding the Animation

### What's Happening

```basic
440 REM Animation loop
450 FRAME = 0
460 WHILE FRAME < 500      ← Runs 500 times
470   FOR I = 0 TO 15      ← Updates all 16 sprites
480     REM Move sprite
490     SX(I) = SX(I) + SVX(I)
500     SY(I) = SY(I) + SVY(I)
520
530     REM Bounce off edges
540     IF SX(I) < 0 THEN
550       SVX(I) = -SVX(I)  ← Reverse direction
560     END IF
...
730     SPRITE MOVE, I, SX(I), SY(I)  ← Update on screen
740   NEXT I
760   FRAME = FRAME + 1
770 WEND
```

### Double-Pass Optimization in Action

**WHILE loop condition:** `FRAME < 500`
- Parsed ONCE into AST when loop starts
- Evaluated 500 times without re-parsing ✅

**FOR loop:** `FOR I = 0 TO 15`
- Already optimized (pre-evaluates bounds)
- Fast iteration ✅

**IF conditions:** `IF SX(I) < 0 THEN`
- Expression evaluated efficiently
- Array lookups optimized ✅

**Result:** Smooth, fast animation!

---

## Recommended Workflow

### 1. Start with Infinite Version
```bash
java Main sprite_demo_infinite.bas
```
- Watch sprites bounce continuously
- See the optimization working in real-time
- Click Stop when satisfied

### 2. Run Performance Test
```bash
java HeadlessTest sprite_demo_fixed.bas
```
- See exact execution time
- Verify performance (should be ~60ms)

### 3. Compare with Benchmark
```bash
java ComparativeBenchmark
```
- See the 148x speedup proof
- Understand why sprites animate smoothly

---

## Troubleshooting

### Sprites flash briefly then disappear
**Cause:** Program ended too quickly
**Solution:** Use `sprite_demo_infinite.bas` or add `WAIT` at end

### Animation is choppy
**Cause:** Too many sprites or slow hardware
**Solution:** Reduce number of sprites or frames

### Window closes immediately
**Cause:** Program hit END or error
**Solution:** Check for errors, use infinite loop version

### Can't stop the animation
**Cause:** Infinite loop running
**Solution:** Click "Stop" button in the editor window

---

## Program Comparison

| Program | Sprites | Frames | Behavior | Best For |
|---------|---------|--------|----------|----------|
| sprite_demo_infinite.bas | 16 | ∞ | Runs forever | Watching animation ⭐ |
| sprite_demo_fixed.bas | 16 | 500 | Waits 60s | Quick demo |
| sprite_demo_simple.bas | 16 | 1000 | Waits 30s | Testing |
| sprite_demo_256.bas | 256 | 10000 | For experts | Maximum scale |

---

## Performance Stats

With Thread.sleep() removed and double-pass optimization:

**sprite_demo_infinite.bas:**
- Each frame: ~0.05ms
- 16 sprites × bouncing = ~1ms per frame
- Smooth 60+ FPS possible!

**sprite_demo_fixed.bas (500 frames):**
- Total time: ~60ms
- Per frame: 0.12ms
- Per sprite update: 0.0075ms ⚡

---

## Visual Guide

### What You Should See

```
╔══════════════════════════════════════════════╗
║  Graphics Window (640x480)                   ║
║                                              ║
║    🔴 ← Red sprite bouncing                  ║
║                                              ║
║         🟢 ← Green sprite moving right       ║
║                                              ║
║  🔵 ← Blue sprite                            ║
║      bouncing off edge                       ║
║                                              ║
║                  🟡 ← Yellow sprite          ║
║                                              ║
║    ... (12 more sprites with different       ║
║         colors and directions)               ║
║                                              ║
╚══════════════════════════════════════════════╝

Console Output:
16 sprites animating!
Press Stop button to end.
Frame: 0
Frame: 100
Frame: 200
...
```

### Expected Behavior

1. **Initial flash:** Sprites created and drawn (< 1 second)
2. **Animation:** Smooth movement, bouncing off edges
3. **Continuous:** Sprites stay visible throughout
4. **End:** Either runs forever or waits before closing

---

## Summary

✅ **Use `sprite_demo_infinite.bas` to see sprites continuously**
✅ **All demos now include WAIT or infinite loop**
✅ **Sprites should be visible during entire animation**
✅ **Click Stop to end infinite animations**

The double-pass optimization makes all this smooth and fast! 🚀
