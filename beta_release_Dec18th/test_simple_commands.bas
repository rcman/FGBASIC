PRINT "Test 1: IF statement"
LET X = 10
IF X > 5 THEN PRINT "X is greater than 5"

PRINT "Test 2: Multi-line IF"
IF X > 5 THEN
  PRINT "X is still greater than 5"
END IF

PRINT "Test 3: DEF FN"
DEF FN DOUBLE(Z) = Z * 2
PRINT FN DOUBLE(5)

PRINT "Test 4: DATA/READ"
READ VAL1, VAL2, VAL3
PRINT VAL1
PRINT VAL2
PRINT VAL3
DATA 10, 20, 30

PRINT "Test 5: SELECT CASE"
LET Y = 2
SELECT CASE Y
CASE 1
  PRINT "One"
CASE 2
  PRINT "Two"
CASE 3
  PRINT "Three"
END SELECT

PRINT "All tests done"
