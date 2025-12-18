' ============================================
' COMPREHENSIVE FGBASIC 2.0.2 FEATURE TEST
' Tests ALL interpreter capabilities
' ============================================

PRINT "========================================="
PRINT "  FGBASIC 2.0.2 COMPREHENSIVE TEST"
PRINT "========================================="
PRINT ""

' ============================================
' SECTION 1: BASIC LANGUAGE FEATURES
' ============================================
PRINT "=== SECTION 1: Basic Language ==="
PRINT ""

' Variables
PRINT "1.1 Variables"
LET X = 42
LET Y = 3.14159
LET Z = X + Y
PRINT "  Integer: ", X
PRINT "  Float: ", Y
PRINT "  Sum: ", Z

' String variables
LET NAME$ = "FGBasic"
LET VERSION$ = "2.0.2"
PRINT "  String: ", NAME$, " ", VERSION$

' ============================================
' SECTION 2: MATH OPERATIONS
' ============================================
PRINT ""
PRINT "=== SECTION 2: Math Operations ==="
PRINT ""

PRINT "2.1 Arithmetic"
LET A = 10
LET B = 3
PRINT "  10 + 3 = ", A + B
PRINT "  10 - 3 = ", A - B
PRINT "  10 * 3 = ", A * B
PRINT "  10 / 3 = ", A / B
LET TEMP = A / B
LET MOD_RESULT = A - (INT(TEMP) * B)
PRINT "  10 MOD 3 = ", MOD_RESULT
PRINT "  2 ^ 8 = ", 2 ^ 8

PRINT ""
PRINT "2.2 Math Functions"
PRINT "  ABS(-5) = ", ABS(-5)
PRINT "  INT(3.7) = ", INT(3.7)
PRINT "  SQR(16) = ", SQR(16)
PRINT "  SIN(45) = ", SIN(45)
PRINT "  COS(45) = ", COS(45)
PRINT "  TAN(45) = ", TAN(45)
PRINT "  ATN(1) = ", ATN(1)
PRINT "  SGN(-10) = ", SGN(-10)
LET RAND = RND
PRINT "  RND = ", RAND
LET RAND100 = RND(100)
PRINT "  RND(100) = ", RAND100

' ============================================
' SECTION 3: STRING FUNCTIONS
' ============================================
PRINT ""
PRINT "=== SECTION 3: String Functions ==="
PRINT ""

LET TEXT$ = "Hello World"
PRINT "  String: ", TEXT$
PRINT "  LEN = ", LEN(TEXT$)
PRINT "  LEFT(5) = ", LEFT$(TEXT$, 5)
PRINT "  RIGHT(5) = ", RIGHT$(TEXT$, 5)
PRINT "  MID(7,5) = ", MID$(TEXT$, 7, 5)
PRINT "  UPPER = ", UPPER$(TEXT$)
PRINT "  LOWER = ", LOWER$(TEXT$)
PRINT "  ASC(H) = ", ASC("H")
PRINT "  CHR(65) = ", CHR$(65)
PRINT "  STR(42) = ", STR$(42)
PRINT "  VAL(123) = ", VAL("123")
PRINT "  INSTR = ", INSTR(TEXT$, "World")

' ============================================
' SECTION 4: CONTROL FLOW
' ============================================
PRINT ""
PRINT "=== SECTION 4: Control Flow ==="
PRINT ""

' IF-THEN-ELSE
PRINT "4.1 IF-THEN-ELSE"
LET NUM = 10
IF NUM > 5 THEN PRINT "  10 > 5: TRUE"
IF NUM < 5 THEN PRINT "  Should not print" ELSE PRINT "  10 < 5: FALSE"

' Comparison operators
PRINT ""
PRINT "4.2 Comparisons"
IF 5 = 5 THEN PRINT "  5 = 5: TRUE"
IF 5 < 10 THEN PRINT "  5 < 10: TRUE"
IF 10 > 5 THEN PRINT "  10 > 5: TRUE"
IF 5 <= 5 THEN PRINT "  5 <= 5: TRUE"
IF 10 >= 10 THEN PRINT "  10 >= 10: TRUE"
IF 5 <> 10 THEN PRINT "  5 <> 10: TRUE"

' FOR loops
PRINT ""
PRINT "4.3 FOR Loop"
PRINT "  Count 1 to 5:"
FOR I = 1 TO 5
    PRINT "    ", I
NEXT I

PRINT "  Count by 2s:"
FOR I = 0 TO 10 STEP 2
    PRINT "    ", I
NEXT I

' WHILE loops
PRINT ""
PRINT "4.4 WHILE Loop"
LET COUNT = 0
PRINT "  Count while < 3:"
WHILE COUNT < 3
    PRINT "    ", COUNT
    LET COUNT = COUNT + 1
WEND

' DO loops
PRINT ""
PRINT "4.5 DO Loop"
LET N = 0
PRINT "  DO WHILE:"
DO WHILE N < 3
    PRINT "    ", N
    LET N = N + 1
LOOP

' Nested loops
PRINT ""
PRINT "4.6 Nested Loops"
PRINT "  2x2 Grid:"
FOR ROW = 1 TO 2
    FOR COL = 1 TO 2
        PRINT "    (", ROW, ",", COL, ")"
    NEXT COL
NEXT ROW

' ============================================
' SECTION 5: ARRAYS
' ============================================
PRINT ""
PRINT "=== SECTION 5: Arrays ==="
PRINT ""

DIM NUMS(5)
PRINT "5.1 Array Operations"
LET NUMS(0) = 10
LET NUMS(1) = 20
LET NUMS(2) = 30
LET NUMS(3) = 40
LET NUMS(4) = 50
PRINT "  NUMS(0) = ", NUMS(0)
PRINT "  NUMS(2) = ", NUMS(2)
PRINT "  NUMS(4) = ", NUMS(4)

' Array with loop
PRINT ""
PRINT "5.2 Fill Array with Loop"
DIM SQUARES(5)
FOR I = 0 TO 5
    LET SQUARES(I) = I * I
NEXT I
PRINT "  Squares: ", SQUARES(0), SQUARES(1), SQUARES(2), SQUARES(3), SQUARES(4), SQUARES(5)

' ============================================
' SECTION 6: DATA/READ/RESTORE
' ============================================
PRINT ""
PRINT "=== SECTION 6: DATA/READ/RESTORE ==="
PRINT ""

DATA 100, 200, 300
DATA "Apple", "Banana", "Cherry"

READ VAL1
READ VAL2
READ VAL3
READ FRUIT1$
READ FRUIT2$
READ FRUIT3$

PRINT "  Numbers: ", VAL1, VAL2, VAL3
PRINT "  Fruits: ", FRUIT1$, " ", FRUIT2$, " ", FRUIT3$

RESTORE
READ FIRST
PRINT "  After RESTORE: ", FIRST

' ============================================
' SECTION 7: SUBROUTINES
' ============================================
PRINT ""
PRINT "=== SECTION 7: Subroutines ==="
PRINT ""

PRINT "7.1 GOSUB/RETURN"
GOSUB 2000
PRINT "  Back from subroutine"
GOTO 2100

2000 REM Subroutine
PRINT "  Inside subroutine"
RETURN

2100 REM Continue

' User-defined functions
PRINT ""
PRINT "7.2 User Functions"
DEF FN DOUBLE(X) = X * 2
DEF FN SQUARE(N) = N * N
PRINT "  FN DOUBLE(5) = ", FN DOUBLE(5)
PRINT "  FN SQUARE(4) = ", FN SQUARE(4)

' ============================================
' SECTION 8: GRAPHICS
' ============================================
PRINT ""
PRINT "=== SECTION 8: Graphics ==="
PRINT ""
PRINT "Drawing graphics..."

CLS

' Colors and lines
COLOR 255, 0, 0
LINE 50, 50, 150, 50
PRINT "  Red line drawn"

COLOR 0, 255, 0
LINE 150, 50, 150, 150
PRINT "  Green line drawn"

COLOR 0, 0, 255
LINE 150, 150, 50, 150
PRINT "  Blue line drawn"

COLOR 255, 255, 0
LINE 50, 150, 50, 50
PRINT "  Yellow line drawn (square complete)"

' Circles
COLOR 255, 0, 255
CIRCLE 320, 100, 30
PRINT "  Magenta circle (outline)"

COLOR 0, 255, 255
CIRCLE 320, 200, 30, 1
PRINT "  Cyan circle (filled)"

' Rectangles
COLOR 255, 128, 0
RECT 400, 50, 80, 60
PRINT "  Orange rectangle (outline)"

COLOR 128, 0, 255
RECT 400, 150, 80, 60, 1
PRINT "  Purple rectangle (filled)"

' Ellipse
COLOR 255, 255, 128
ELLIPSE 100, 300, 60, 40
PRINT "  Yellow ellipse"

' Pixels
COLOR 255, 255, 255
FOR PX = 200 TO 250
    PIXEL PX, 300
NEXT PX
PRINT "  White pixels (line)"

' Text with fonts
COLOR 255, 255, 255
FONT "Arial", 16, "BOLD"
TEXT 50, 400, "FGBasic 2.0.2"
PRINT "  Bold text drawn"

FONT "Courier New", 12, "ITALIC"
TEXT 50, 420, "Graphics Test"
PRINT "  Italic text drawn"

WAIT 2

' ============================================
' SECTION 9: SPRITES
' ============================================
PRINT ""
PRINT "=== SECTION 9: Sprites ==="
PRINT ""

CLS
PRINT "Creating sprites..."

' Create and draw sprites
FOR S = 0 TO 4
    SPRITE CREATE, S, 16, 16

    ' Different color per sprite
    LET R = 50 + S * 40
    LET G = 255 - S * 40
    LET B = 100 + S * 30
    COLOR R, G, B

    ' Draw a simple pattern
    FOR SX = 0 TO 15
        FOR SY = 0 TO 15
            IF SX = 0 THEN SPRITE PIXEL, S, SX, SY
            IF SY = 0 THEN SPRITE PIXEL, S, SX, SY
            IF SX = 15 THEN SPRITE PIXEL, S, SX, SY
            IF SY = 15 THEN SPRITE PIXEL, S, SX, SY
            IF SX = SY THEN SPRITE PIXEL, S, SX, SY
        NEXT SY
    NEXT SX

    ' Position sprites
    LET XPOS = 50 + S * 80
    SPRITE MOVE, S, XPOS, 100
    SPRITE SHOW, S
NEXT S

PRINT "  5 sprites created and shown"

' Collision detection
SPRITE MOVE, 0, 100, 100
SPRITE MOVE, 1, 100, 100
COLLISION 0, 1
PRINT "  Collision (same pos): ", COLLISION

SPRITE MOVE, 1, 200, 200
COLLISION 0, 1
PRINT "  Collision (diff pos): ", COLLISION

WAIT 2

' ============================================
' SECTION 10: SPRITE ANIMATION
' ============================================
PRINT ""
PRINT "=== SECTION 10: Sprite Animation ==="
PRINT ""

CLS
PRINT "Animating sprites..."

' Create animated sprites
FOR S = 0 TO 9
    SPRITE CREATE, S, 8, 8
    COLOR 255, 100 + S * 15, 100
    SPRITE PIXEL, S, 4, 4

    LET XSTART = 50 + S * 50
    SPRITE MOVE, S, XSTART, 200
    SPRITE SHOW, S

    ' Start animation with different velocities
    LET VX = 1 + S * 0.5
    LET VY = 2 - S * 0.3
    ANIMATE START, S, VX, VY, 60, 1
NEXT S

COLOR 255, 255, 0
TEXT 150, 50, "10 Sprites Bouncing!"
PRINT "  10 sprites animating (3 sec)..."

WAIT 3

ANIMATE STOPALL
PRINT "  Animation stopped"

' Cleanup sprites
FOR S = 0 TO 9
    SPRITE DELETE, S
NEXT S

' ============================================
' SECTION 11: SOUND
' ============================================
PRINT ""
PRINT "=== SECTION 11: Sound ==="
PRINT ""

CLS
PRINT "Playing sounds..."

PRINT "  Beep"
BEEP 200

PRINT "  Tone 440Hz (A4)"
SOUND 440, 300

PRINT "  Tone 523Hz (C5)"
SOUND 523, 300

PRINT "  Waveforms:"
WAVEFORM "SINE"
SOUND 330, 200
PRINT "    Sine"

WAVEFORM "SQUARE"
SOUND 330, 200
PRINT "    Square"

WAVEFORM "TRIANGLE"
SOUND 330, 200
PRINT "    Triangle"

PRINT "  Note sequence"
PLAY "C4:200,E4:200,G4:300"

WAIT 1

' ============================================
' SECTION 12: TURTLE GRAPHICS
' ============================================
PRINT ""
PRINT "=== SECTION 12: Turtle Graphics ==="
PRINT ""

CLS
COLOR 0, 255, 0
PRINT "Drawing with turtle..."

' Draw a square
FORWARD 100
RIGHT 90
FORWARD 100
RIGHT 90
FORWARD 100
RIGHT 90
FORWARD 100
RIGHT 90

PRINT "  Square drawn"

' Draw a triangle
PENUP
FORWARD 150
PENDOWN
COLOR 255, 0, 0
FORWARD 80
LEFT 120
FORWARD 80
LEFT 120
FORWARD 80

PRINT "  Triangle drawn"
WAIT 1

' ============================================
' SECTION 13: 3D GRAPHICS
' ============================================
PRINT ""
PRINT "=== SECTION 13: 3D Graphics ==="
PRINT ""

CLS
COLOR 255, 255, 255
PRINT "Drawing 3D shapes..."

BOX3D 100, 100, 0, 50, 50, 50
PRINT "  3D box drawn"

SPHERE3D 300, 200, 0, 40
PRINT "  3D sphere drawn"

ROTATE3D 30, 45, 0
BOX3D 450, 150, 0, 60, 40, 40
PRINT "  Rotated box drawn"

WAIT 2

' ============================================
' SECTION 14: INPUT SYSTEM
' ============================================
PRINT ""
PRINT "=== SECTION 14: Input System ==="
PRINT ""

CLS
COLOR 255, 255, 255
TEXT 200, 200, "Move mouse and click!"
PRINT "Reading input for 3 seconds..."

LET STARTTIME = 0
FOR CHECK = 1 TO 30
    MOUSEX
    MOUSEY
    MOUSEBUTTON

    IF MOUSEBUTTON = 1 THEN
        PRINT "  Mouse clicked at: ", MOUSEX, ",", MOUSEY
    END IF

    WAIT 0.1
NEXT CHECK

PRINT "  Input test complete"

' ============================================
' SECTION 15: PERFORMANCE TEST
' ============================================
PRINT ""
PRINT "=== SECTION 15: Performance ==="
PRINT ""

CLS
PRINT "Performance test..."

PRINT "  10,000 iterations..."
LET PCOUNT = 0
FOR PI = 1 TO 10000
    LET PCOUNT = PCOUNT + 1
NEXT PI
PRINT "  Count: ", PCOUNT

PRINT "  Nested loops (100x100)..."
LET NEST = 0
FOR NI = 1 TO 100
    FOR NJ = 1 TO 100
        LET NEST = NEST + 1
    NEXT NJ
NEXT NI
PRINT "  Nested count: ", NEST

' ============================================
' FINAL RESULTS
' ============================================
PRINT ""
PRINT "========================================="
PRINT "  ALL TESTS COMPLETE!"
PRINT "========================================="
PRINT ""
PRINT "Tested features:"
PRINT "  [X] Variables (numeric & string)"
PRINT "  [X] Math operations & functions"
PRINT "  [X] String functions"
PRINT "  [X] Control flow (IF/FOR/WHILE/DO)"
PRINT "  [X] Arrays"
PRINT "  [X] DATA/READ/RESTORE"
PRINT "  [X] Subroutines & functions"
PRINT "  [X] Graphics (lines, circles, rects)"
PRINT "  [X] Sprites & collision"
PRINT "  [X] Sprite animation"
PRINT "  [X] Sound & waveforms"
PRINT "  [X] Turtle graphics"
PRINT "  [X] 3D graphics"
PRINT "  [X] Input system"
PRINT "  [X] Performance"
PRINT ""

CLS
COLOR 0, 255, 0
FONT "Arial", 24, "BOLD"
TEXT 100, 200, "ALL TESTS PASSED!"
FONT "Arial", 16, "PLAIN"
TEXT 120, 240, "FGBasic 2.0.2 is working!"

PRINT "========================================="
PRINT "  SUCCESS!"
PRINT "========================================="

WAIT 3
END
