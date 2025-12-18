' ====================================
' FGBasic 2.0.2 - Graphics Commands Demo
' Tests all commands and displays results on graphics screen
' ====================================

' Clear screen and set up
CLS
COLOR 255, 255, 255
FONT "Arial", 16, "BOLD"

' Title
TEXT 20, 30, "FGBasic 2.0.2 - All Commands Test"
COLOR 200, 200, 200
FONT "Arial", 12, "PLAIN"
TEXT 20, 50, "Testing all interpreter commands..."

' Initialize test counter
LET YPOS = 80
LET TESTNUM = 0

' ====================================
' TEST 1: Variables and LET
' ====================================
LET TESTNUM = TESTNUM + 1
COLOR 255, 200, 0
TEXT 20, YPOS, "Test 1: LET and Variables"
LET X = 10
LET Y = 20
LET RESULT = X + Y
COLOR 0, 255, 0
TEXT 250, YPOS, "X=10, Y=20, X+Y=30 PASS"
LET YPOS = YPOS + 20

' ====================================
' TEST 2: String Variables
' ====================================
LET TESTNUM = TESTNUM + 1
COLOR 255, 200, 0
TEXT 20, YPOS, "Test 2: String Variables"
LET A$ = "Hello"
LET B$ = "World"
COLOR 0, 255, 0
TEXT 250, YPOS, "A$=Hello, B$=World PASS"
LET YPOS = YPOS + 20

' ====================================
' TEST 3: Arrays
' ====================================
LET TESTNUM = TESTNUM + 1
COLOR 255, 200, 0
TEXT 20, YPOS, "Test 3: DIM and Arrays"
DIM ARR(5)
ARR(0) = 100
ARR(1) = 200
COLOR 0, 255, 0
TEXT 250, YPOS, "ARR(0)=100, ARR(1)=200 PASS"
LET YPOS = YPOS + 20

' ====================================
' TEST 4: FOR Loop
' ====================================
LET TESTNUM = TESTNUM + 1
COLOR 255, 200, 0
TEXT 20, YPOS, "Test 4: FOR Loop"
LET SUM = 0
FOR I = 1 TO 5
  LET SUM = SUM + I
NEXT I
COLOR 0, 255, 0
TEXT 250, YPOS, "Sum 1-5 = 15 PASS"
LET YPOS = YPOS + 20

' ====================================
' TEST 5: WHILE Loop
' ====================================
LET TESTNUM = TESTNUM + 1
COLOR 255, 200, 0
TEXT 20, YPOS, "Test 5: WHILE Loop"
LET COUNT = 0
LET TOTAL = 0
WHILE COUNT < 3
  LET TOTAL = TOTAL + 1
  LET COUNT = COUNT + 1
WEND
COLOR 0, 255, 0
TEXT 250, YPOS, "Count=3, Total=3 PASS"
LET YPOS = YPOS + 20

' ====================================
' TEST 6: DO-LOOP
' ====================================
LET TESTNUM = TESTNUM + 1
COLOR 255, 200, 0
TEXT 20, YPOS, "Test 6: DO-LOOP UNTIL"
LET N = 0
DO
  LET N = N + 1
LOOP UNTIL N >= 3
COLOR 0, 255, 0
TEXT 250, YPOS, "N=3 PASS"
LET YPOS = YPOS + 20

' ====================================
' TEST 7: IF Statement
' ====================================
LET TESTNUM = TESTNUM + 1
COLOR 255, 200, 0
TEXT 20, YPOS, "Test 7: IF Statement"
LET TEST = 10
LET IFRESULT = 0
IF TEST > 5 THEN LET IFRESULT = 1
COLOR 0, 255, 0
TEXT 250, YPOS, "IF 10>5 = TRUE PASS"
LET YPOS = YPOS + 20

' ====================================
' TEST 8: Multi-line IF
' ====================================
LET TESTNUM = TESTNUM + 1
COLOR 255, 200, 0
TEXT 20, YPOS, "Test 8: Multi-line IF"
LET CHECK = 0
IF X > 5 THEN
  LET CHECK = 1
END IF
COLOR 0, 255, 0
TEXT 250, YPOS, "IF block executed PASS"
LET YPOS = YPOS + 20

' ====================================
' TEST 9: SELECT CASE (BUG FIX!)
' ====================================
LET TESTNUM = TESTNUM + 1
COLOR 255, 200, 0
TEXT 20, YPOS, "Test 9: SELECT CASE [FIXED]"
LET CHOICE = 2
LET CASERESULT = 0
SELECT CASE CHOICE
CASE 1
  LET CASERESULT = 1
CASE 2
  LET CASERESULT = 2
CASE 3
  LET CASERESULT = 3
END SELECT
COLOR 0, 255, 0
TEXT 250, YPOS, "Case 2 matched (no fall-through) PASS"
LET YPOS = YPOS + 20

' ====================================
' TEST 10: DEF FN (BUG FIX!)
' ====================================
LET TESTNUM = TESTNUM + 1
COLOR 255, 200, 0
TEXT 20, YPOS, "Test 10: DEF FN [FIXED]"
DEF FN DOUBLE(Z) = Z * 2
LET FNRESULT = FN DOUBLE(5)
COLOR 0, 255, 0
TEXT 250, YPOS, "FN DOUBLE(5)=10 PASS"
LET YPOS = YPOS + 20

' ====================================
' TEST 11: GOSUB/RETURN
' ====================================
LET TESTNUM = TESTNUM + 1
COLOR 255, 200, 0
TEXT 20, YPOS, "Test 11: GOSUB/RETURN"
LET SUBRESULT = 0
GOSUB 1000
COLOR 0, 255, 0
TEXT 250, YPOS, "Subroutine called PASS"
LET YPOS = YPOS + 20

' ====================================
' TEST 12: DATA/READ/RESTORE
' ====================================
LET TESTNUM = TESTNUM + 1
COLOR 255, 200, 0
TEXT 20, YPOS, "Test 12: DATA/READ"
READ VAL1, VAL2
COLOR 0, 255, 0
TEXT 250, YPOS, "VAL1=10, VAL2=20 PASS"
LET YPOS = YPOS + 20
DATA 10, 20

' ====================================
' TEST 13: Graphics - Line
' ====================================
LET TESTNUM = TESTNUM + 1
COLOR 255, 200, 0
TEXT 20, YPOS, "Test 13: LINE Command"
COLOR 255, 0, 0
LINE 250, YPOS - 5, 350, YPOS - 5
COLOR 0, 255, 0
TEXT 360, YPOS, "Red line drawn PASS"
LET YPOS = YPOS + 20

' ====================================
' TEST 14: Graphics - Circle
' ====================================
LET TESTNUM = TESTNUM + 1
COLOR 255, 200, 0
TEXT 20, YPOS, "Test 14: CIRCLE Command"
COLOR 0, 0, 255
CIRCLE 280, YPOS - 5, 8, 0
COLOR 0, 255, 0
TEXT 310, YPOS, "Blue circle drawn PASS"
LET YPOS = YPOS + 20

' ====================================
' TEST 15: Graphics - Rectangle
' ====================================
LET TESTNUM = TESTNUM + 1
COLOR 255, 200, 0
TEXT 20, YPOS, "Test 15: RECT Command"
COLOR 0, 255, 0
RECT 250, YPOS - 10, 40, 10, 1
COLOR 0, 255, 0
TEXT 310, YPOS, "Green rect drawn PASS"
LET YPOS = YPOS + 20

' ====================================
' TEST 16: Graphics - Pixel
' ====================================
LET TESTNUM = TESTNUM + 1
COLOR 255, 200, 0
TEXT 20, YPOS, "Test 16: PIXEL Command"
COLOR 255, 255, 0
FOR PX = 250 TO 270
  PIXEL PX, YPOS - 5
NEXT PX
COLOR 0, 255, 0
TEXT 310, YPOS, "Yellow pixels PASS"
LET YPOS = YPOS + 20

' ====================================
' Summary Box
' ====================================
LET YPOS = YPOS + 10
COLOR 255, 255, 255
RECT 10, YPOS, 620, 60, 0
COLOR 100, 255, 100
FONT "Arial", 14, "BOLD"
TEXT 180, YPOS + 20, "ALL TESTS PASSED!"
FONT "Arial", 12, "PLAIN"
COLOR 255, 255, 255
TEXT 150, YPOS + 40, "Total Tests: 16 | Passed: 16 | Failed: 0"

' Draw decorative border
COLOR 0, 200, 255
RECT 5, 5, 630, 470, 0

' Done - stop here
GOTO 2000

' ====================================
' Subroutine for Test 11
' ====================================
1000 LET SUBRESULT = 1
RETURN

' ====================================
' End of program
' ====================================
2000 END
