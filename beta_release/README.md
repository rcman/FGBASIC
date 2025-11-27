# BASIC Interpreter Updates

## Fixed Issues & New Features

### 1. **Fixed Sprite Display Issues**
The main problem was that sprite commands (SHOW, MOVE, HIDE, DELETE, PIXEL) were not calling `graphics.repaint()` after making changes. This has been fixed - all sprite modification commands now properly trigger a repaint, so sprites will display immediately.

### 2. **Added WAIT Command**
Pauses program execution for a specified number of seconds.

**Syntax:**
```basic
WAIT seconds
```

**Examples:**
```basic
WAIT 1          ' Wait for 1 second
WAIT 0.5        ' Wait for half a second
WAIT 2.5        ' Wait for 2.5 seconds
```

### 3. **Added FONT Command**
Sets the font for text displayed on the graphics screen using the TEXT command. This allows you to customize font family, size, and style for displaying variables and text directly on the graphics window.

**Syntax:**
```basic
FONT "fontname", size
FONT "fontname", size, "style"
```

**Style options:** "PLAIN", "BOLD", "ITALIC", "BOLDITALIC"

**Examples:**
```basic
FONT "Arial", 20
FONT "Courier New", 16, "BOLD"
FONT "Times New Roman", 24, "ITALIC"
```

## Complete Example Programs

### Example 1: Sprite Animation with WAIT
```basic
' Create a sprite and animate it across the screen
CLS
COLOR 255, 255, 0

' Create a sprite
SPRITE CREATE, 1, 50, 50

' Fill the sprite with pixels
FOR Y = 0 TO 49
    FOR X = 0 TO 49
        SPRITE PIXEL, 1, X, Y
    NEXT X
NEXT Y

' Show the sprite
SPRITE SHOW, 1

' Animate it across the screen
FOR X = 0 TO 590 STEP 10
    SPRITE MOVE, 1, X, 200
    WAIT 0.05
NEXT X
```

### Example 2: Display Variables on Graphics Screen
```basic
' Display score and status using different fonts
CLS

' Set a large bold font for the title
FONT "Arial", 32, "BOLD"
COLOR 255, 255, 255
TEXT 200, 50, "GAME STATUS"

' Set a medium font for labels
FONT "Courier New", 20, "PLAIN"
COLOR 0, 255, 0

SCORE = 1000
LEVEL = 5
LIVES = 3

TEXT 50, 150, "Score: " + STR(SCORE)
TEXT 50, 200, "Level: " + STR(LEVEL)
TEXT 50, 250, "Lives: " + STR(LIVES)

' Update display in a loop
FOR I = 1 TO 50
    WAIT 0.1
    SCORE = SCORE + 10
    
    ' Clear previous score area (draw black box)
    COLOR 0, 0, 0
    RECT 150, 135, 200, 30
    
    ' Redraw score with new value
    COLOR 0, 255, 0
    FONT "Courier New", 20, "PLAIN"
    TEXT 50, 150, "Score: " + STR(SCORE)
NEXT I
```

### Example 3: Moving Sprite with Real-Time Display
```basic
' Game-style display with sprite and score
CLS

' Create player sprite
SPRITE CREATE, 1, 32, 32
COLOR 255, 0, 0

' Draw a simple square sprite
FOR Y = 0 TO 31
    FOR X = 0 TO 31
        SPRITE PIXEL, 1, X, Y
    NEXT X
NEXT Y

SPRITE MOVE, 1, 100, 200
SPRITE SHOW, 1

' Setup fonts
FONT "Arial", 16, "BOLD"

X = 100
SCORE = 0

' Main loop
FOR I = 1 TO 100
    ' Move sprite
    X = X + 5
    SPRITE MOVE, 1, X, 200
    SCORE = SCORE + 10
    
    ' Clear and redraw score
    COLOR 0, 0, 0
    RECT 10, 10, 200, 30
    
    COLOR 255, 255, 0
    TEXT 20, 30, "Score: " + STR(SCORE)
    
    WAIT 0.05
NEXT I
```

### Example 4: Multiple Sprites with Collision Detection
```basic
' Create two sprites and detect collision
CLS

' Create two sprites
SPRITE CREATE, 1, 40, 40
SPRITE CREATE, 2, 40, 40

' Fill first sprite (red)
COLOR 255, 0, 0
FOR Y = 0 TO 39
    FOR X = 0 TO 39
        SPRITE PIXEL, 1, X, Y
    NEXT X
NEXT Y

' Fill second sprite (blue)
COLOR 0, 0, 255
FOR Y = 0 TO 39
    FOR X = 0 TO 39
        SPRITE PIXEL, 2, X, Y
    NEXT X
NEXT Y

' Position and show sprites
SPRITE MOVE, 1, 100, 200
SPRITE MOVE, 2, 500, 200
SPRITE SHOW, 1
SPRITE SHOW, 2

' Setup display font
FONT "Arial", 18, "BOLD"

' Move sprites toward each other
FOR I = 0 TO 200
    SPRITE MOVE, 1, 100 + I * 2, 200
    SPRITE MOVE, 2, 500 - I * 2, 200
    
    ' Check collision
    COLLISION 1, 2
    
    ' Display status
    COLOR 0, 0, 0
    RECT 10, 10, 250, 40
    
    COLOR 255, 255, 255
    IF COLLISION = 1 THEN
        TEXT 20, 35, "COLLISION!"
        WAIT 2
        END
    ELSE
        TEXT 20, 35, "Distance: " + STR(400 - I * 4)
    END IF
    
    WAIT 0.02
NEXT I
```

## Technical Changes

### Interpreter.java
- Added `executeWait()` method to pause execution
- Added `executeFont()` method to set font properties
- Added WAIT and FONT to the command switch in `executeLine()`
- Added `graphics.repaint()` calls to all sprite commands:
  - SPRITE SHOW
  - SPRITE MOVE
  - SPRITE HIDE
  - SPRITE DELETE
  - SPRITE PIXEL

### GraphicsWindow.java
- Added `currentFont` field to track the current font
- Added `setFont(String fontName, int fontSize, int fontStyle)` method
- Added `getFont()` method
- Updated `drawText()` to use the current font
- Initialized default font to "Arial", 12pt, PLAIN in constructor

### SyntaxHighlighter.java
- Added WAIT and FONT to the keywords array for syntax highlighting

## Usage Notes

1. **Sprites now display immediately** - No need for extra code to refresh the display
2. **WAIT is interruptible** - If you stop the program, WAIT will terminate gracefully
3. **FONT affects TEXT command** - Set the font before calling TEXT to use that font
4. **Font changes persist** - Once you set a font, it remains active until you change it again
5. **String concatenation** - Use `+` to combine strings with variables for TEXT command

## Common Font Names
- Arial
- Times New Roman
- Courier New
- Verdana
- Georgia
- Comic Sans MS
- Impact

## Compiling and Running

```bash
javac *.java
java Main
```

All files have been updated and are ready to use!
