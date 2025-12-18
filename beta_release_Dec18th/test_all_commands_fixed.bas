' Comprehensive test to check all BASIC commands
PRINT "Testing all commands..."

' Test 1: Variables and expressions
PRINT "1. Testing LET and PRINT"
LET X = 10
LET Y = 20
PRINT X
PRINT Y
PRINT X + Y

' Test 2: String variables
PRINT "2. Testing string variables"
LET A$ = "Hello"
LET B$ = "World"
PRINT A$
PRINT B$

' Test 3: Arrays
PRINT "3. Testing DIM and arrays"
DIM ARR(5)
ARR(0) = 10
ARR(1) = 20
ARR(2) = 30
PRINT ARR(0)
PRINT ARR(1)
PRINT ARR(2)

' Test 4: FOR loop
PRINT "4. Testing FOR loop"
FOR I = 1 TO 3
  PRINT I
NEXT I

' Test 5: WHILE loop
PRINT "5. Testing WHILE loop"
LET COUNT = 0
WHILE COUNT < 3
  PRINT COUNT
  LET COUNT = COUNT + 1
WEND

' Test 6: DO-LOOP
PRINT "6. Testing DO-LOOP"
LET N = 0
DO
  PRINT N
  LET N = N + 1
LOOP UNTIL N >= 3

' Test 7: GOSUB/RETURN
PRINT "7. Testing GOSUB"
GOSUB 100
PRINT "After GOSUB"
GOTO 200

100 PRINT "In subroutine"
RETURN

200 PRINT "8. Testing IF"
IF X > 5 THEN PRINT "X is greater than 5"

' Test 9: DEF FN
PRINT "9. Testing DEF FN"
DEF FN DOUBLE(Z) = Z * 2
PRINT FN DOUBLE(5)

' Test 10: DATA/READ/RESTORE
PRINT "10. Testing DATA/READ"
READ VAL1, VAL2, VAL3
PRINT VAL1
PRINT VAL2
PRINT VAL3
DATA 10, 20, 30

PRINT "All tests completed!"
