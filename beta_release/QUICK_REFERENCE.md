# QUICK REFERENCE - New Commands

## WAIT Command

**Syntax:**
```basic
WAIT seconds
```

**Description:** Pauses program execution for the specified number of seconds

**Examples:**
```basic
WAIT 1          ' Wait 1 second
WAIT 0.5        ' Wait 0.5 seconds
WAIT 2.5        ' Wait 2.5 seconds

' Example: Flashing text
FOR I = 1 TO 10
    COLOR 255, 0, 0
    TEXT 100, 100, "FLASH!"
    WAIT 0.3
    COLOR 0, 0, 0
    RECT 100, 85, 100, 30
    WAIT 0.3
NEXT I
```

---

## FONT Command

**Syntax:**
```basic
FONT "fontname", size
FONT "fontname", size, "style"
```

**Parameters:**
- `fontname` - Font family name (e.g., "Arial", "Courier New")
- `size` - Font size in points (e.g., 12, 16, 24)
- `style` - Optional: "PLAIN", "BOLD", "ITALIC", or "BOLDITALIC"

**Description:** Sets the font for subsequent TEXT commands

**Examples:**
```basic
' Set basic font
FONT "Arial", 16

' Set bold font
FONT "Arial", 20, "BOLD"

' Set italic font
FONT "Times New Roman", 18, "ITALIC"

' Set bold italic
FONT "Courier New", 14, "BOLDITALIC"

' Example: Multi-style display
CLS
FONT "Arial", 24, "BOLD"
COLOR 255, 255, 0
TEXT 100, 50, "GAME TITLE"

FONT "Courier New", 14, "PLAIN"
COLOR 0, 255, 0
SCORE = 1000
TEXT 100, 100, "Score: " + STR(SCORE)

FONT "Times New Roman", 12, "ITALIC"
COLOR 200, 200, 200
TEXT 100, 130, "Press any key..."
```

---

## SPRITE Commands (Fixed!)

**All sprite commands now properly update the display immediately**

**Create Sprite:**
```basic
SPRITE CREATE, id, width, height
```

**Show Sprite:**
```basic
SPRITE SHOW, id          ' Now displays immediately!
```

**Move Sprite:**
```basic
SPRITE MOVE, id, x, y    ' Now updates immediately!
```

**Hide Sprite:**
```basic
SPRITE HIDE, id          ' Now hides immediately!
```

**Set Pixel:**
```basic
COLOR r, g, b
SPRITE PIXEL, id, x, y   ' Now shows immediately!
```

**Example: Create and display sprite**
```basic
CLS

' Create sprite
SPRITE CREATE, 1, 50, 50

' Fill with color
COLOR 255, 0, 0
FOR Y = 0 TO 49
    FOR X = 0 TO 49
        SPRITE PIXEL, 1, X, Y
    NEXT X
NEXT Y

' Show sprite - works immediately now!
SPRITE MOVE, 1, 100, 100
SPRITE SHOW, 1

' Animate
FOR X = 100 TO 500 STEP 5
    SPRITE MOVE, 1, X, 100
    WAIT 0.02
NEXT X
```

---

## Common Font Names

**Sans-Serif:**
- Arial
- Verdana
- Helvetica

**Serif:**
- Times New Roman
- Georgia
- Garamond

**Monospace:**
- Courier New
- Consolas
- Monaco

**Display:**
- Comic Sans MS
- Impact
- Papyrus

---

## Combining Features Example

```basic
' Complete game-style display
CLS

' Title
FONT "Impact", 32, "BOLD"
COLOR 255, 255, 0
TEXT 200, 50, "SUPER GAME"

' Create player sprite
SPRITE CREATE, 1, 40, 40
COLOR 0, 255, 0
FOR Y = 0 TO 39
    FOR X = 0 TO 39
        SPRITE PIXEL, 1, X, Y
    NEXT X
NEXT Y

SPRITE MOVE, 1, 100, 200
SPRITE SHOW, 1

' Status display
FONT "Arial", 16, "BOLD"
SCORE = 0
LIVES = 3

' Game loop
FOR I = 1 TO 100
    ' Move player
    SPRITE MOVE, 1, 100 + I * 4, 200
    SCORE = SCORE + 10
    
    ' Clear old status
    COLOR 0, 0, 0
    RECT 20, 100, 200, 60
    
    ' Display new status
    COLOR 255, 255, 255
    TEXT 20, 120, "Score: " + STR(SCORE)
    TEXT 20, 140, "Lives: " + STR(LIVES)
    
    WAIT 0.03
NEXT I
```

---

## Tips

1. **Set font before TEXT** - The FONT command affects all subsequent TEXT commands
2. **Use WAIT for animation** - Controls frame rate and timing
3. **Clear before updating** - Draw black rectangles to erase old text
4. **Sprites update automatically** - No need to manually refresh
5. **Combine with colors** - Use COLOR before FONT/TEXT for colored text

---

## Troubleshooting

**Sprites not showing?**
- ✓ Make sure to call SPRITE SHOW
- ✓ Check that sprite has pixels set
- ✓ Verify position is on screen (0-640 x, 0-480 y)

**Text not displaying?**
- ✓ Font must be set before TEXT command
- ✓ Coordinates must be on screen
- ✓ Color must not be black on black

**Animation too fast/slow?**
- ✓ Adjust WAIT time (smaller = faster)
- ✓ Use WAIT 0.01 to 0.1 for smooth animation
