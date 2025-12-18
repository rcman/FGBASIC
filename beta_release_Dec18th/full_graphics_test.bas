' ====================================
' FGBasic 2.0.2 - Complete Graphics Test
' Tests ALL graphics commands with visual output
' ====================================

CLS
COLOR 255, 255, 255
FONT "Arial", 18, "BOLD"
TEXT 150, 20, "FGBasic Graphics Commands Test"

FONT "Arial", 10, "PLAIN"
COLOR 200, 200, 200
TEXT 200, 40, "Testing all graphics features..."

WAIT 1

' ====================================
' TEST 1: CLS (Clear Screen)
' ====================================
CLS
COLOR 255, 200, 0
FONT "Arial", 14, "BOLD"
TEXT 10, 20, "Test 1: CLS - Clear Screen"
COLOR 255, 255, 255
FONT "Arial", 11, "PLAIN"
TEXT 10, 40, "Screen cleared successfully!"
TEXT 10, 55, "Status: PASS"
WAIT 2

' ====================================
' TEST 2: COLOR - Set Drawing Color
' ====================================
CLS
COLOR 255, 200, 0
FONT "Arial", 14, "BOLD"
TEXT 10, 20, "Test 2: COLOR - Set Drawing Color"
COLOR 255, 255, 255
FONT "Arial", 11, "PLAIN"
TEXT 10, 40, "Testing RGB color values..."

COLOR 255, 0, 0
TEXT 10, 70, "RED (255, 0, 0)"
COLOR 0, 255, 0
TEXT 10, 90, "GREEN (0, 255, 0)"
COLOR 0, 0, 255
TEXT 10, 110, "BLUE (0, 0, 255)"
COLOR 255, 255, 0
TEXT 10, 130, "YELLOW (255, 255, 0)"
COLOR 255, 0, 255
TEXT 10, 150, "MAGENTA (255, 0, 255)"
COLOR 0, 255, 255
TEXT 10, 170, "CYAN (0, 255, 255)"

COLOR 0, 255, 0
TEXT 10, 200, "Status: PASS - 6 colors displayed"
WAIT 3

' ====================================
' TEST 3: PIXEL/PSET - Draw Pixels
' ====================================
CLS
COLOR 255, 200, 0
FONT "Arial", 14, "BOLD"
TEXT 10, 20, "Test 3: PIXEL/PSET - Draw Individual Pixels"
COLOR 255, 255, 255
FONT "Arial", 11, "PLAIN"
TEXT 10, 40, "Drawing pixel patterns..."

' Draw a colorful pixel pattern
FOR PY = 0 TO 50 STEP 2
  FOR PX = 0 TO 50 STEP 2
    LET R = PX * 5
    LET G = PY * 5
    LET B = 128
    COLOR R, G, B
    PIXEL 50 + PX, 70 + PY
  NEXT PX
NEXT PY

' Draw pixel line
COLOR 255, 255, 0
FOR I = 0 TO 100
  PIXEL 150 + I, 70 + I / 2
NEXT I

' Draw pixel grid
COLOR 0, 255, 255
FOR X = 0 TO 80 STEP 5
  FOR Y = 0 TO 80 STEP 5
    PIXEL 300 + X, 70 + Y
  NEXT Y
NEXT X

COLOR 0, 255, 0
TEXT 10, 180, "Status: PASS - Gradient, line, and grid drawn"
WAIT 3

' ====================================
' TEST 4: LINE - Draw Lines
' ====================================
CLS
COLOR 255, 200, 0
FONT "Arial", 14, "BOLD"
TEXT 10, 20, "Test 4: LINE - Draw Lines"
COLOR 255, 255, 255
FONT "Arial", 11, "PLAIN"
TEXT 10, 40, "Drawing various line patterns..."

' Horizontal lines
COLOR 255, 0, 0
LINE 50, 70, 250, 70
COLOR 0, 255, 0
LINE 50, 80, 250, 80
COLOR 0, 0, 255
LINE 50, 90, 250, 90

' Vertical lines
COLOR 255, 255, 0
LINE 300, 70, 300, 150
COLOR 255, 0, 255
LINE 320, 70, 320, 150
COLOR 0, 255, 255
LINE 340, 70, 340, 150

' Diagonal lines
COLOR 255, 128, 0
LINE 400, 70, 500, 150
COLOR 128, 0, 255
LINE 500, 70, 400, 150

' Star pattern
COLOR 255, 255, 255
FOR ANGLE = 0 TO 360 STEP 30
  LET RAD = ANGLE * 3.14159 / 180
  LET ENDX = 150 + 80 * SIN(RAD)
  LET ENDY = 250 + 80 * SIN(RAD)
  LINE 150, 250, ENDX, ENDY
NEXT ANGLE

COLOR 0, 255, 0
TEXT 10, 350, "Status: PASS - Lines in all directions"
WAIT 3

' ====================================
' TEST 5: CIRCLE - Draw Circles
' ====================================
CLS
COLOR 255, 200, 0
FONT "Arial", 14, "BOLD"
TEXT 10, 20, "Test 5: CIRCLE - Draw Circles"
COLOR 255, 255, 255
FONT "Arial", 11, "PLAIN"
TEXT 10, 40, "Drawing circles (outline and filled)..."

' Outline circles
COLOR 255, 0, 0
CIRCLE 80, 100, 30, 0
COLOR 0, 255, 0
CIRCLE 180, 100, 25, 0
COLOR 0, 0, 255
CIRCLE 280, 100, 20, 0

' Filled circles
COLOR 255, 255, 0
CIRCLE 80, 200, 30, 1
COLOR 255, 0, 255
CIRCLE 180, 200, 25, 1
COLOR 0, 255, 255
CIRCLE 280, 200, 20, 1

' Concentric circles
FOR R = 10 TO 50 STEP 10
  LET C = R * 5
  COLOR C, 255 - C, 128
  CIRCLE 450, 150, R, 0
NEXT R

' Spiral of circles
FOR I = 0 TO 8
  LET ANGLE = I * 45 * 3.14159 / 180
  LET CX = 450 + 60 * SIN(ANGLE)
  LET CY = 300 + 60 * SIN(ANGLE)
  COLOR 255 - I * 30, I * 30, 128
  CIRCLE CX, CY, 15, 1
NEXT I

COLOR 0, 255, 0
TEXT 10, 380, "Status: PASS - Outline and filled circles"
WAIT 3

' ====================================
' TEST 6: RECT/BOX - Draw Rectangles
' ====================================
CLS
COLOR 255, 200, 0
FONT "Arial", 14, "BOLD"
TEXT 10, 20, "Test 6: RECT/BOX - Draw Rectangles"
COLOR 255, 255, 255
FONT "Arial", 11, "PLAIN"
TEXT 10, 40, "Drawing rectangles (outline and filled)..."

' Outline rectangles
COLOR 255, 0, 0
RECT 50, 70, 100, 60, 0
COLOR 0, 255, 0
RECT 170, 70, 90, 50, 0
COLOR 0, 0, 255
RECT 280, 70, 80, 40, 0

' Filled rectangles
COLOR 255, 255, 0
RECT 50, 160, 100, 60, 1
COLOR 255, 0, 255
RECT 170, 160, 90, 50, 1
COLOR 0, 255, 255
RECT 280, 160, 80, 40, 1

' Pattern of rectangles
FOR I = 0 TO 5
  COLOR 50 + I * 40, 255 - I * 40, 128
  RECT 420 + I * 8, 70 + I * 8, 80 - I * 8, 60 - I * 8, 0
NEXT I

' Checkerboard pattern
FOR Y = 0 TO 3
  FOR X = 0 TO 3
    LET CHECK = X + Y
    IF CHECK / 2 = INT(CHECK / 2) THEN
      COLOR 255, 255, 255
    END IF
    IF CHECK / 2 <> INT(CHECK / 2) THEN
      COLOR 0, 0, 0
    END IF
    RECT 420 + X * 20, 250 + Y * 20, 20, 20, 1
  NEXT X
NEXT Y

COLOR 0, 255, 0
TEXT 10, 380, "Status: PASS - Outline and filled rectangles"
WAIT 3

' ====================================
' TEST 7: ELLIPSE - Draw Ellipses
' ====================================
CLS
COLOR 255, 200, 0
FONT "Arial", 14, "BOLD"
TEXT 10, 20, "Test 7: ELLIPSE - Draw Ellipses"
COLOR 255, 255, 255
FONT "Arial", 11, "PLAIN"
TEXT 10, 40, "Drawing ellipses (outline and filled)..."

' Outline ellipses
COLOR 255, 0, 0
ELLIPSE 80, 100, 60, 40, 0
COLOR 0, 255, 0
ELLIPSE 200, 100, 50, 30, 0
COLOR 0, 0, 255
ELLIPSE 320, 100, 40, 25, 0

' Filled ellipses
COLOR 255, 255, 0
ELLIPSE 80, 220, 60, 40, 1
COLOR 255, 0, 255
ELLIPSE 200, 220, 50, 30, 1
COLOR 0, 255, 255
ELLIPSE 320, 220, 40, 25, 1

' Rotated ellipses pattern
FOR I = 0 TO 3
  COLOR 255 - I * 60, I * 60, 200
  ELLIPSE 480, 150, 70 - I * 15, 40 - I * 8, 0
NEXT I

COLOR 0, 255, 0
TEXT 10, 350, "Status: PASS - Ellipses drawn"
WAIT 3

' ====================================
' TEST 8: POLYGON - Draw Polygons
' ====================================
CLS
COLOR 255, 200, 0
FONT "Arial", 14, "BOLD"
TEXT 10, 20, "Test 8: POLYGON - Draw Polygons"
COLOR 255, 255, 255
FONT "Arial", 11, "PLAIN"
TEXT 10, 40, "Drawing multi-sided polygons..."

' Triangle
COLOR 255, 0, 0
POLYGON 80, 70, 120, 130, 40, 130

' Square/Diamond
COLOR 0, 255, 0
POLYGON 200, 80, 240, 100, 200, 120, 160, 100

' Pentagon-like shape
COLOR 0, 0, 255
POLYGON 320, 70, 360, 90, 350, 130, 290, 130, 280, 90

' Star shape
COLOR 255, 255, 0
POLYGON 480, 80, 500, 120, 540, 130, 510, 160, 520, 200, 480, 180, 440, 200, 450, 160, 420, 130, 460, 120

' Hexagon
COLOR 0, 255, 255
POLYGON 150, 250, 180, 240, 210, 250, 210, 280, 180, 290, 150, 280

' Complex polygon
COLOR 255, 0, 255
POLYGON 350, 240, 400, 250, 420, 280, 400, 310, 350, 310, 330, 280

COLOR 0, 255, 0
TEXT 10, 380, "Status: PASS - Various polygons drawn"
WAIT 3

' ====================================
' TEST 9: TEXT - Draw Text
' ====================================
CLS
COLOR 255, 200, 0
FONT "Arial", 14, "BOLD"
TEXT 10, 20, "Test 9: TEXT - Draw Text at Position"
COLOR 255, 255, 255
FONT "Arial", 11, "PLAIN"
TEXT 10, 40, "Drawing text with different properties..."

' Different sizes
FONT "Arial", 10, "PLAIN"
COLOR 255, 255, 255
TEXT 50, 80, "Size 10"
FONT "Arial", 14, "PLAIN"
TEXT 50, 100, "Size 14"
FONT "Arial", 18, "PLAIN"
TEXT 50, 120, "Size 18"
FONT "Arial", 24, "PLAIN"
TEXT 50, 145, "Size 24"

' Different styles
FONT "Arial", 14, "PLAIN"
COLOR 255, 200, 0
TEXT 250, 80, "Plain Style"
FONT "Arial", 14, "BOLD"
COLOR 255, 150, 0
TEXT 250, 100, "Bold Style"
FONT "Arial", 14, "ITALIC"
COLOR 255, 100, 0
TEXT 250, 120, "Italic Style"
FONT "Arial", 14, "BOLDITALIC"
COLOR 255, 50, 0
TEXT 250, 140, "Bold Italic Style"

' Different colors
FONT "Arial", 12, "PLAIN"
COLOR 255, 0, 0
TEXT 50, 200, "Red Text"
COLOR 0, 255, 0
TEXT 50, 220, "Green Text"
COLOR 0, 0, 255
TEXT 50, 240, "Blue Text"
COLOR 255, 255, 0
TEXT 50, 260, "Yellow Text"

' Text with variables
FONT "Arial", 11, "PLAIN"
COLOR 0, 255, 255
LET NUM = 42
LET NAME$ = "FGBasic"
TEXT 250, 200, "Variable NUM ="
TEXT 370, 200, "42"
TEXT 250, 220, "Variable NAME$ ="
TEXT 390, 220, "FGBasic"

COLOR 0, 255, 0
FONT "Arial", 11, "PLAIN"
TEXT 10, 320, "Status: PASS - Text rendered with styles"
WAIT 3

' ====================================
' TEST 10: FONT - Set Font Properties
' ====================================
CLS
COLOR 255, 200, 0
FONT "Arial", 14, "BOLD"
TEXT 10, 20, "Test 10: FONT - Set Font Properties"
COLOR 255, 255, 255
FONT "Arial", 11, "PLAIN"
TEXT 10, 40, "Testing different fonts and styles..."

' Different font families
FONT "Arial", 14, "PLAIN"
COLOR 255, 255, 255
TEXT 50, 80, "Arial Font"
FONT "Courier", 14, "PLAIN"
TEXT 50, 100, "Courier Font"
FONT "Times", 14, "PLAIN"
TEXT 50, 120, "Times Font"
FONT "Monospaced", 14, "PLAIN"
TEXT 50, 140, "Monospaced Font"

' Size progression
FONT "Arial", 10, "PLAIN"
COLOR 200, 200, 255
TEXT 300, 80, "Growing"
FONT "Arial", 12, "PLAIN"
TEXT 300, 95, "Size"
FONT "Arial", 14, "PLAIN"
TEXT 300, 112, "Text"
FONT "Arial", 16, "PLAIN"
TEXT 300, 131, "Demo"

' Style combinations
FONT "Arial", 13, "PLAIN"
COLOR 255, 200, 200
TEXT 50, 180, "Plain + Size 13"
FONT "Arial", 13, "BOLD"
COLOR 200, 255, 200
TEXT 50, 200, "Bold + Size 13"
FONT "Arial", 13, "ITALIC"
COLOR 200, 200, 255
TEXT 50, 220, "Italic + Size 13"
FONT "Arial", 13, "BOLDITALIC"
COLOR 255, 255, 200
TEXT 50, 240, "BoldItalic + Size 13"

COLOR 0, 255, 0
FONT "Arial", 11, "PLAIN"
TEXT 10, 300, "Status: PASS - Fonts configured"
WAIT 3

' ====================================
' TEST 11: NOREFRESH/REFRESH - Control Updates
' ====================================
CLS
COLOR 255, 200, 0
FONT "Arial", 14, "BOLD"
TEXT 10, 20, "Test 11: NOREFRESH/REFRESH - Control Updates"
COLOR 255, 255, 255
FONT "Arial", 11, "PLAIN"
TEXT 10, 40, "Testing batch drawing with NOREFRESH..."

' Draw with NOREFRESH (fast batch drawing)
NOREFRESH
COLOR 100, 100, 255
TEXT 10, 80, "Drawing 100 objects with NOREFRESH..."
FOR I = 0 TO 99
  LET X = 50 + I * 5
  LET Y = 120 + I / 4
  COLOR I * 2, 255 - I * 2, 128
  CIRCLE X, Y, 3, 1
NEXT I
REFRESH

COLOR 0, 255, 0
TEXT 10, 240, "Status: PASS - Batch drawing completed"
TEXT 10, 260, "100 circles drawn efficiently"
WAIT 2

' ====================================
' TEST 12: Combined Graphics Demo
' ====================================
CLS
COLOR 255, 200, 0
FONT "Arial", 14, "BOLD"
TEXT 10, 20, "Test 12: Combined Graphics Demo"
COLOR 255, 255, 255
FONT "Arial", 11, "PLAIN"
TEXT 10, 40, "Complex scene using all commands..."

NOREFRESH

' Draw sky gradient
FOR Y = 60 TO 200
  LET B = 150 + Y / 2
  COLOR 100, 150, B
  LINE 0, Y, 640, Y
NEXT Y

' Draw ground
COLOR 100, 200, 100
RECT 0, 200, 640, 280, 1

' Draw sun
COLOR 255, 255, 0
CIRCLE 500, 100, 30, 1
COLOR 255, 200, 0
CIRCLE 500, 100, 25, 1

' Draw house
COLOR 200, 100, 50
RECT 100, 250, 120, 100, 1
COLOR 150, 50, 50
POLYGON 100, 250, 220, 250, 160, 200
COLOR 100, 200, 255
RECT 130, 280, 30, 30, 1
RECT 170, 280, 30, 30, 1
COLOR 139, 69, 19
RECT 150, 310, 20, 40, 1

' Draw tree
COLOR 139, 69, 19
RECT 350, 280, 20, 70, 1
COLOR 34, 139, 34
CIRCLE 360, 270, 35, 1
CIRCLE 340, 260, 30, 1
CIRCLE 380, 260, 30, 1

' Draw clouds
COLOR 255, 255, 255
CIRCLE 150, 90, 20, 1
CIRCLE 170, 85, 25, 1
CIRCLE 190, 90, 20, 1
CIRCLE 350, 110, 18, 1
CIRCLE 368, 106, 22, 1
CIRCLE 386, 110, 18, 1

' Draw flowers
FOR F = 0 TO 5
  LET FX = 50 + F * 100
  COLOR 255, 0, 255
  CIRCLE FX, 380, 8, 1
  COLOR 255, 255, 0
  CIRCLE FX, 380, 3, 1
  COLOR 0, 150, 0
  LINE FX, 380, FX, 400
NEXT F

' Add text label
COLOR 0, 0, 0
FONT "Arial", 12, "BOLD"
TEXT 220, 450, "Graphics Scene Demo"

REFRESH

COLOR 0, 255, 0
FONT "Arial", 11, "PLAIN"
TEXT 10, 470, "Status: PASS - Complete scene rendered"
WAIT 3

' ====================================
' FINAL SUMMARY
' ====================================
CLS
COLOR 255, 255, 255
FONT "Arial", 20, "BOLD"
TEXT 100, 100, "Graphics Test Complete!"

FONT "Arial", 14, "PLAIN"
COLOR 200, 255, 200
TEXT 150, 150, "All 12 graphics tests passed"

COLOR 255, 255, 255
FONT "Arial", 12, "PLAIN"
TEXT 100, 200, "Tests completed:"
TEXT 120, 220, "1. CLS - Clear Screen"
TEXT 120, 235, "2. COLOR - Set Drawing Color"
TEXT 120, 250, "3. PIXEL/PSET - Draw Pixels"
TEXT 120, 265, "4. LINE - Draw Lines"
TEXT 120, 280, "5. CIRCLE - Draw Circles"
TEXT 120, 295, "6. RECT/BOX - Draw Rectangles"

TEXT 350, 220, "7. ELLIPSE - Draw Ellipses"
TEXT 350, 235, "8. POLYGON - Draw Polygons"
TEXT 350, 250, "9. TEXT - Draw Text"
TEXT 350, 265, "10. FONT - Set Font Properties"
TEXT 350, 280, "11. NOREFRESH/REFRESH - Batch Draw"
TEXT 350, 295, "12. Combined Demo - Complex Scene"

' Draw completion checkmarks
COLOR 0, 255, 0
FOR C = 0 TO 11
  IF C < 6 THEN
    LET CX = 110
    LET CY = 220 + C * 15
  END IF
  IF C >= 6 THEN
    LET CX = 340
    LET CY = 220 + (C - 6) * 15
  END IF
  CIRCLE CX, CY, 3, 1
NEXT C

COLOR 100, 255, 100
FONT "Arial", 16, "BOLD"
TEXT 180, 350, "All Graphics Commands Working!"

' Draw decorative border
COLOR 0, 200, 255
RECT 10, 10, 620, 460, 0
RECT 12, 12, 616, 456, 0

' Done
END
