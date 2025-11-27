# Bug Fixes Summary - FGBasic 2.0.2

## Session Overview
This document summarizes all bugs found and fixed during testing of the BASIC interpreter.

## Critical Bugs Fixed

### 1. **RND Function Not Working**
**Problem:** `RND` without parentheses returned 0 instead of a random number.

**Root Cause:** The expression parser only recognized `RND(max)` but not standalone `RND`.

**Fix:** Added check in ExpressionParser.java line 387:
```java
if (expr.toUpperCase().equals("RND")) {
    return Math.random();  // Returns 0.0 to 1.0
}
```

**Test:** `test_rnd.bas` now shows random values between 0 and 1.

---

### 2. **DIM Command Array Size Off-By-One**
**Problem:** `DIM X(10)` created array with only 10 elements (indices 0-9) instead of 11 (indices 0-10).

**Root Cause:** BASIC arrays are inclusive of upper bound, but code allocated exactly `size` elements.

**Fix:** Changed Interpreter.java line 963:
```java
arrays.put(varName, new double[size + 1]);  // Was: new double[size]
```

**Test:** `test_dim.bas` can now access `X(10)` without error.

---

### 3. **Comparison Operators (<=, >=, <>) Broken**
**Problem:** `WHILE K <= 5` always evaluated to FALSE, causing loops to never execute.

**Root Cause:** Expression parser scanned right-to-left and matched single `=` before checking for `<=` or `>=`.

**Fix:** Modified ExpressionParser.java parseComparison() to check two-character operators FIRST by looking at position i-1 and i together (lines 228-250), before checking single-character operators.

**Test:** `simple_while.bas` now correctly loops while K <= 5.

---

### 4. **Subtraction Operator Broken**
**Problem:** `5 - 3` returned 0 instead of 2. All subtraction operations returned 0.

**Root Cause:** The parseAddSub() method had faulty logic to distinguish between subtraction (`5 - 3`) and negative numbers (`-3`). It skipped the minus sign if the previous character was a space.

**Fix:** Changed ExpressionParser.java line 294:
```java
char prev = expr.charAt(i - 1);
if (Character.isDigit(prev) || prev == ')' || Character.isWhitespace(prev)) {
    return parseAddSub(expr.substring(0, i).trim()) -
           parseMulDiv(expr.substring(i + 1).trim());
}
```

**Test:** `test_math.bas` now shows `5 - 3 = 2.0`.

---

### 5. **Sprite Pixels Not Visible**
**Problem:** Sprites created with SPRITE PIXEL had transparent pixels instead of opaque ones.

**Root Cause:** Sprites use TYPE_INT_ARGB with alpha channel, but setPixel() didn't ensure alpha was 0xFF (opaque).

**Fix:** Changed SpriteSystem.java line 195:
```java
int rgb = color.getRGB() | 0xFF000000;  // Force alpha to fully opaque
image.setRGB(x, y, rgb);
```

**Test:** `direct_pixel_test.bas` now shows colored sprites.

---

### 6. **SPRITE FILL Not Displaying**
**Problem:** Sprites filled with SPRITE FILL appeared transparent.

**Root Cause:** fillRect() didn't set the composite mode to draw opaque pixels.

**Fix:** Added AlphaComposite.Src to SpriteSystem.java line 205:
```java
g.setComposite(AlphaComposite.Src);  // Draw opaque, not blend with transparency
```

**Test:** `one_sprite.bas` shows solid red 100x100 square.

---

### 7. **SPRITE Commands Missing Repaint**
**Problem:** SPRITE FILL didn't update display immediately.

**Root Cause:** graphics.repaint() was not called after SPRITE FILL.

**Fix:** Added repaint() call in Interpreter.java line 1219:
```java
sprite.fillRect(0, 0, sprite.width, sprite.height, graphics.getCurrentColor());
graphics.repaint();  // Force display update
```

**Test:** Sprites now appear immediately when filled.

---

### 8. **SPRITE PIXEL Parameter Count Check Wrong**
**Problem:** `SPRITE PIXEL, 1, 5, 5` didn't execute because code required 5 parameters instead of 4.

**Root Cause:** Command parser splits "SPRITE PIXEL, 1, 5, 5" into 4 parts, but code checked for `parts.length >= 5`.

**Fix:** Changed Interpreter.java line 1256:
```java
if (parts.length >= 4 && graphics != null) {  // Was: >= 5
```

**Test:** SPRITE PIXEL now sets pixels correctly.

---

### 9. **Double Buffering Added**
**Enhancement:** Added double buffering to eliminate sprite flickering during animation.

**Implementation:** GraphicsWindow.java GraphicsPanel now:
1. Renders main buffer to backBuffer (TYPE_INT_RGB)
2. Draws all sprites on top of backBuffer
3. Displays complete backBuffer to screen in one operation

**Code:** GraphicsWindow.java lines 259-269:
```java
Graphics2D backG = backBuffer.createGraphics();
try {
    backG.setComposite(AlphaComposite.SrcOver);  // Blend sprites properly
    backG.drawImage(buffer, 0, 0, null);
    spriteSystem.drawAllSprites(backG);
} finally {
    backG.dispose();
}
g.drawImage(backBuffer, 0, 0, null);
```

**Test:** `three_sprites_fast.bas` shows smooth animation with no flicker.

---

### 10. **EditorWindow Missing Return Statement**
**Problem:** readFileData() method declared boolean return but had no return statement.

**Fix:** Added return statements in EditorWindow.java lines 176, 179:
```java
return true;   // On success
return false;  // On IOException
```

---

## Test Files Created

1. **comprehensive_test.bas** - Tests all core BASIC commands
2. **graphics_test.bas** - Tests all drawing commands
3. **sprite_test.bas** - Tests all sprite commands
4. **test_rnd.bas** - Tests random number generation
5. **test_dim.bas** - Tests array dimension
6. **test_math.bas** - Tests arithmetic operators
7. **simple_while.bas** - Tests WHILE loops
8. **test_do_until.bas** - Tests DO UNTIL loops
9. **one_sprite.bas** - Simplest sprite test
10. **three_sprites_fast.bas** - 3 bouncing sprites
11. **10_sprites_fast.bas** - 10 bouncing sprites (performance demo)

---

## Performance Notes

### SPRITE PIXEL vs SPRITE FILL

**SLOW (avoid):**
```basic
FOR Y = 0 TO 29
    FOR X = 0 TO 29
        SPRITE PIXEL, 1, X, Y  ' 900 interpreter calls!
    NEXT X
NEXT Y
```

**FAST (recommended):**
```basic
SPRITE FILL, 1  ' Single operation!
```

**Performance Difference:** SPRITE FILL is ~900x faster for a 30x30 sprite.

---

## All Tests Passing

Run the comprehensive test suite:
```bash
java Main comprehensive_test.bas
```

Expected results:
- ✅ All arithmetic operations correct
- ✅ All comparison operators work
- ✅ RND returns random values
- ✅ Arrays work with correct indexing
- ✅ FOR loops work (including STEP)
- ✅ WHILE loops work
- ✅ DO WHILE loops work
- ✅ DO UNTIL loops work
- ✅ String functions work
- ✅ User-defined functions work
- ✅ DATA/READ/RESTORE works
- ✅ GOSUB/RETURN works

Graphics tests:
```bash
java Main graphics_test.bas   # Lines, circles, text
java Main sprite_test.bas     # Sprite operations
```

Performance demos:
```bash
java Main 10_sprites_fast.bas  # 10 bouncing sprites
```

---

## Files Modified

1. **ExpressionParser.java** - Fixed RND, comparison operators, subtraction
2. **Interpreter.java** - Fixed DIM, SPRITE PIXEL count, added repaints
3. **SpriteSystem.java** - Fixed alpha channel in setPixel and fillRect
4. **GraphicsWindow.java** - Added double buffering
5. **EditorWindow.java** - Fixed return statement

---

## Conclusion

All critical bugs have been fixed. The BASIC interpreter now correctly handles:
- Math operations (including subtraction)
- Comparison operators (<=, >=, <>, <, >, =)
- Random numbers (RND)
- Arrays with proper BASIC indexing
- All loop types (FOR, WHILE, DO WHILE, DO UNTIL)
- Sprite creation and rendering with proper alpha channel
- Smooth animation with double buffering

The interpreter is now production-ready for writing BASIC programs with graphics and sprites!
