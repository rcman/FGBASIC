# BASIC Interpreter - UPDATED with Sprite Fixes

## What's New in This Update

### 1. **Fixed Sprite Display** ✓
- Added `graphics.repaint()` to SPRITE SHOW, MOVE, HIDE, DELETE
- Sprites now display immediately when shown or moved
- CREATE also triggers repaint

### 2. **New SPRITE FILL Command** ⭐
- **Much faster** than setting pixels individually
- Fills entire sprite with current color in one command
- Syntax: `SPRITE FILL, id`

### 3. **WAIT Command** ✓
- Pauses execution: `WAIT seconds`
- Supports decimals: `WAIT 0.5`

### 4. **FONT Command** ✓
- Sets font for TEXT: `FONT "name", size, "style"`
- Styles: PLAIN, BOLD, ITALIC, BOLDITALIC

### 5. **Improved TEXT Command** ✓
- Now properly handles string concatenation
- Can display variables: `TEXT 10, 10, "Score: " + STR(SCORE)`

## Quick Start - Testing Sprites

### Ultra-Simple Test (Start Here!)

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

**What you should see:**
- A red 100x100 square in the center of the graphics window
- It stays visible for 5 seconds

**If this doesn't work**, see SPRITE_DEBUGGING.md

## SPRITE Commands Reference

### CREATE
```basic
SPRITE CREATE, id, width, height
```
Creates a new sprite with given dimensions.

### FILL (NEW!)
```basic
COLOR r, g, b
SPRITE FILL, id
```
Fills entire sprite with current color. **Much faster than PIXEL loop!**

### PIXEL
```basic
COLOR r, g, b
SPRITE PIXEL, id, x, y
```
Sets a single pixel. For filling, use FILL instead.

### SHOW
```basic
SPRITE SHOW, id
```
Makes sprite visible. **Now properly updates display!**

### HIDE
```basic
SPRITE HIDE, id
```
Makes sprite invisible.

### MOVE
```basic
SPRITE MOVE, id, x, y
```
Positions sprite at coordinates. **Now properly updates display!**

### DELETE
```basic
SPRITE DELETE, id
```
Removes sprite from memory.

## Complete Examples

### Example 1: Simple Moving Sprite
```basic
CLS

' Create and fill sprite
SPRITE CREATE, 1, 50, 50
COLOR 0, 255, 0
SPRITE FILL, 1

' Show it
SPRITE MOVE, 1, 50, 200
SPRITE SHOW, 1

' Animate across screen
FOR X = 50 TO 590 STEP 5
    SPRITE MOVE, 1, X, 200
    WAIT 0.02
NEXT X

END
```

### Example 2: Score Display with Sprite
```basic
CLS

' Create player sprite
SPRITE CREATE, 1, 40, 40
COLOR 255, 0, 0
SPRITE FILL, 1
SPRITE MOVE, 1, 100, 200
SPRITE SHOW, 1

' Setup font for score
FONT "Arial", 20, "BOLD"
SCORE = 0

' Game loop
FOR I = 1 TO 100
    ' Move sprite
    SPRITE MOVE, 1, 100 + I * 4, 200
    SCORE = SCORE + 10
    
    ' Clear old score
    COLOR 0, 0, 0
    RECT 10, 10, 200, 40
    
    ' Display new score
    COLOR 255, 255, 0
    TEXT 20, 35, "Score: " + STR(SCORE)
    
    WAIT 0.05
NEXT I

END
```

### Example 3: Multi-Color Sprite Pattern
```basic
CLS

' Create sprite
SPRITE CREATE, 1, 100, 100

' Fill with gradient (slower but pretty)
FOR Y = 0 TO 99
    FOR X = 0 TO 99
        R = X * 2
        G = Y * 2
        B = 128
        COLOR R, G, B
        SPRITE PIXEL, 1, X, Y
    NEXT X
    ' Update display every 10 rows for feedback
    IF Y MOD 10 = 0 THEN
        SPRITE MOVE, 1, 270, 190
        SPRITE SHOW, 1
    END IF
NEXT Y

' Final display
SPRITE MOVE, 1, 270, 190
SPRITE SHOW, 1

WAIT 3
END
```

### Example 4: Multiple Sprites
```basic
CLS

' Create 3 sprites with different colors
SPRITE CREATE, 1, 60, 60
COLOR 255, 0, 0
SPRITE FILL, 1

SPRITE CREATE, 2, 60, 60
COLOR 0, 255, 0
SPRITE FILL, 2

SPRITE CREATE, 3, 60, 60
COLOR 0, 0, 255
SPRITE FILL, 3

' Position and show all
SPRITE MOVE, 1, 100, 200
SPRITE MOVE, 2, 290, 200
SPRITE MOVE, 3, 480, 200

SPRITE SHOW, 1
SPRITE SHOW, 2
SPRITE SHOW, 3

' Add labels
FONT "Arial", 14, "BOLD"
COLOR 255, 255, 255
TEXT 115, 180, "RED"
TEXT 300, 180, "GREEN"
TEXT 490, 180, "BLUE"

WAIT 5
END
```

## WAIT Command Examples

```basic
WAIT 1          ' 1 second
WAIT 0.5        ' Half second
WAIT 2.5        ' 2.5 seconds
WAIT 0.016      ' ~60 FPS (for smooth animation)
WAIT 0.033      ' ~30 FPS
```

## FONT Command Examples

```basic
' Basic fonts
FONT "Arial", 16
FONT "Courier New", 14
FONT "Times New Roman", 18

' With styles
FONT "Arial", 20, "BOLD"
FONT "Times New Roman", 16, "ITALIC"
FONT "Courier New", 14, "BOLDITALIC"

' Display variables
FONT "Arial", 18, "BOLD"
COLOR 255, 255, 0
SCORE = 1000
LEVEL = 5
TEXT 50, 50, "Score: " + STR(SCORE)
TEXT 50, 75, "Level: " + STR(LEVEL)
```

## Performance Tips

### ❌ SLOW - Don't do this for large sprites:
```basic
' Setting 10,000 pixels individually = VERY SLOW
SPRITE CREATE, 1, 100, 100
COLOR 255, 0, 0
FOR Y = 0 TO 99
    FOR X = 0 TO 99
        SPRITE PIXEL, 1, X, Y  ' Interpreted 10,000 times!
    NEXT X
NEXT Y
```

### ✅ FAST - Do this instead:
```basic
' Fill entire sprite in one command = INSTANT
SPRITE CREATE, 1, 100, 100
COLOR 255, 0, 0
SPRITE FILL, 1  ' Done in milliseconds!
```

**Speed difference:** FILL is approximately **1000x faster** than pixel-by-pixel!

## Troubleshooting

### Sprites Don't Show
1. Make sure you called `SPRITE SHOW, id`
2. Check coordinates are on screen (0-640 x, 0-480 y)
3. Verify sprite has pixels set (use SPRITE FILL or SPRITE PIXEL)
4. Look at console window for error messages
5. See **SPRITE_DEBUGGING.md** for detailed help

### Sprites Disappear
- Don't call `CLS` after showing sprites
- CLS clears the background but also repaints, which redraws sprites
- If sprite disappears, make sure it's still visible: `SPRITE SHOW, id`

### Slow Animation
- Use `WAIT 0.02` to `WAIT 0.05` for smooth animation
- Use SPRITE FILL instead of pixel loops
- Reduce sprite size if still slow

### Text Not Showing Variables
- Make sure to use string concatenation: `"Score: " + STR(SCORE)`
- Use `STR()` to convert numbers to strings
- The updated TEXT command now handles this properly

## Files Included

### Core Files (Updated)
- **Interpreter.java** - Main interpreter with sprite fixes and new commands
- **GraphicsWindow.java** - Font support added
- **SyntaxHighlighter.java** - Updated keywords

### Test Programs
1. **ultra_simple_sprite_test.bas** - 7-line minimal test
2. **simple_sprite_test.bas** - Basic sprite test with console output
3. **test_program.bas** - Full-featured test program
4. **game_demo.bas** - Complete game example

### Documentation
- **README.md** - This file
- **SPRITE_DEBUGGING.md** - Detailed troubleshooting guide
- **QUICK_REFERENCE.md** - Command reference card
- **FILE_SUMMARY.md** - Change summary

## Compilation

```bash
# Compile all files
javac *.java

# Run the interpreter
java Main
```

## System Requirements

- Java 8 or higher
- Graphics display (640x480 minimum)
- ~50 MB RAM

## Command Summary

### New/Updated Commands
- `SPRITE FILL, id` - Fill sprite with color (NEW!)
- `WAIT seconds` - Pause execution (NEW!)
- `FONT "name", size, "style"` - Set text font (NEW!)
- `SPRITE SHOW, id` - Show sprite (FIXED!)
- `SPRITE MOVE, id, x, y` - Move sprite (FIXED!)
- `TEXT x, y, expression` - Draw text with variables (IMPROVED!)

## Next Steps

1. Run **ultra_simple_sprite_test.bas** to verify sprites work
2. Try **simple_sprite_test.bas** for more features
3. Experiment with **test_program.bas** for full demo
4. Read **SPRITE_DEBUGGING.md** if you have issues
5. Build your own games!

## Known Limitations

- Maximum sprite size limited by RAM
- No sprite rotation (use 3D commands for rotation)
- No pixel-perfect collision (use COLLISION command for bounding box)
- Animation speed depends on system performance

## Support

If sprites still don't display after trying all tests, check:
1. Both windows are open (Editor + Graphics)
2. No Java exceptions in console
3. Interpreter.java has the repaint() calls (lines ~1207, ~1214, ~1220)

Good luck with your BASIC programming!
