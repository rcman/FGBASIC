# SPRITE DEBUGGING GUIDE

## Issue: Sprites Not Displaying

If sprites aren't showing up, try these debugging steps:

### Step 1: Test with Ultra-Simple Program

Run this minimal test first:

```basic
CLS
SPRITE CREATE, 1, 100, 100
COLOR 255, 0, 0
SPRITE FILL, 1
SPRITE MOVE, 1, 270, 190
SPRITE SHOW, 1
WAIT 5
END
```

**What this tests:**
- Creates a 100x100 sprite
- Fills it with RED using the new SPRITE FILL command
- Positions it in the center
- Shows it
- Waits 5 seconds so you can see it

### Step 2: Check Console Output

Look at the console window (not the graphics window) for any error messages like:
- "Graphics not initialized"
- "Array index out of bounds"
- "Sprite not found"

### Step 3: Try Individual Pixel Setting

If FILL doesn't work, try setting pixels manually but with fewer pixels:

```basic
CLS
SPRITE CREATE, 1, 10, 10
COLOR 255, 0, 0
FOR Y = 0 TO 9
    FOR X = 0 TO 9
        SPRITE PIXEL, 1, X, Y
    NEXT X
NEXT Y
SPRITE MOVE, 1, 100, 100
SPRITE SHOW, 1
WAIT 5
END
```

### Step 4: Verify Graphics Window

Make sure:
1. The **Graphics Window** is open (separate from the editor)
2. The Graphics Window has focus (click on it)
3. You're looking at the right window

### Step 5: Check Sprite Visibility

Try this test to verify visibility toggling works:

```basic
CLS
SPRITE CREATE, 1, 50, 50
COLOR 255, 255, 0
SPRITE FILL, 1
SPRITE MOVE, 1, 100, 100

' Show for 2 seconds
SPRITE SHOW, 1
WAIT 2

' Hide for 2 seconds
SPRITE HIDE, 1
WAIT 2

' Show again
SPRITE SHOW, 1
WAIT 2

END
```

## New SPRITE FILL Command

I've added a `SPRITE FILL` command that's MUCH more efficient than setting each pixel:

**Syntax:**
```basic
COLOR r, g, b
SPRITE FILL, id
```

**Example:**
```basic
' Create sprite
SPRITE CREATE, 1, 100, 100

' Fill with blue
COLOR 0, 0, 255
SPRITE FILL, 1

' Show it
SPRITE MOVE, 1, 200, 150
SPRITE SHOW, 1
```

## Performance Note

Setting pixels individually in a loop is **very slow** because:
- Each SPRITE PIXEL command is interpreted
- Loops in BASIC are slower than compiled code

**Solution:** Use `SPRITE FILL` for solid colors, or set fewer pixels.

## Common Issues and Solutions

### Issue: "Sprite shows for a moment then disappears"
**Cause:** CLS command after showing sprite  
**Solution:** Don't call CLS after SPRITE SHOW

### Issue: "Sprite appears in wrong location"
**Cause:** Coordinates are off-screen  
**Solution:** Graphics window is 640x480. Use coordinates: X: 0-640, Y: 0-480

### Issue: "Nothing happens at all"
**Possible causes:**
1. Graphics window not visible - Check taskbar
2. Program hit an error - Check console output
3. Sprite created but not shown - Make sure SPRITE SHOW is called

### Issue: "Sprite is all black"
**Cause:** Transparent pixels or wrong color  
**Solution:** Make sure COLOR is set before SPRITE FILL or SPRITE PIXEL

## Test Files Included

1. **ultra_simple_sprite_test.bas** - Minimal 7-line test
2. **simple_sprite_test.bas** - Basic sprite operations with console output
3. **test_program.bas** - Full featured test (may be too complex if basics don't work)
4. **game_demo.bas** - Complete game example (requires working sprites)

**Recommended order:** Run them in the order listed above.

## Manual Compilation Check

If nothing works, verify compilation:

```bash
# Clean build
rm *.class 2>/dev/null

# Compile all
javac *.java

# Check for errors
echo $?

# Should output: 0 (means success)
```

## Verifying the Fix

The key changes that fix sprite display:

1. **Interpreter.java** - Line ~1214: `graphics.repaint()` after SPRITE SHOW
2. **Interpreter.java** - Line ~1207: `graphics.repaint()` after SPRITE MOVE  
3. **Interpreter.java** - Line ~1220: `graphics.repaint()` after SPRITE HIDE
4. **Interpreter.java** - Added SPRITE FILL command (efficient filling)

Check these lines in your Interpreter.java to ensure the fixes are present.

## Alternative: Drawing Directly to Graphics

If sprites still don't work, you can draw directly to the graphics buffer:

```basic
CLS

' Draw a filled rectangle
COLOR 255, 0, 0
RECT 100, 100, 100, 100, 1

' Draw a circle
COLOR 0, 255, 0
CIRCLE 300, 200, 50, 1

WAIT 5
END
```

This tests if the graphics system itself is working.

## Getting More Help

If sprites still don't show:

1. Run `ultra_simple_sprite_test.bas`
2. Take a screenshot of BOTH windows (editor and graphics)
3. Copy any console output
4. Check if Interpreter.java has the `graphics.repaint()` calls

The sprite system SHOULD work with these fixes. If it doesn't, there may be a Java/Swing threading issue.
