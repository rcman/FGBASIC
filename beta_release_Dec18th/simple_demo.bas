PRINT "=========================================="
PRINT "FGBasic 2.0.2 Bug Fixes Demo"
PRINT "=========================================="
PRINT ""

PRINT "BUG FIX #1: SELECT CASE"
PRINT "------------------------------------------"
PRINT "Before fix: Cases would fall through"
PRINT "After fix: Only matching case executes"
PRINT ""

LET X = 2
PRINT "Testing SELECT CASE with X = 2"
SELECT CASE X
CASE 1
  PRINT "  Result: ONE"
CASE 2
  PRINT "  Result: TWO (correct!)"
CASE 3
  PRINT "  Result: THREE"
END SELECT
PRINT "Code after END SELECT continues!"
PRINT ""

PRINT "BUG FIX #2: DEF FN"
PRINT "------------------------------------------"
PRINT "Before fix: Always returned 0.0"
PRINT "After fix: Functions work correctly"
PRINT ""

DEF FN DOUBLE(X) = X * 2
DEF FN SQUARE(Y) = Y * Y

PRINT "FN DOUBLE(5) ="
PRINT FN DOUBLE(5)

PRINT "FN SQUARE(4) ="
PRINT FN SQUARE(4)

PRINT "FN DOUBLE(3) + FN SQUARE(2) ="
PRINT FN DOUBLE(3) + FN SQUARE(2)

PRINT ""
PRINT "=========================================="
PRINT "All bugs fixed and working correctly!"
PRINT "=========================================="
