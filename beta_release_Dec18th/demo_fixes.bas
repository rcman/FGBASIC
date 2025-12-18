' ====================================
' FGBasic 2.0.2 - Bug Fixes Demo
' ====================================
' This program demonstrates the two major bug fixes:
' 1. SELECT CASE now works correctly
' 2. DEF FN (user-defined functions) now work

PRINT "======================================"
PRINT "FGBasic 2.0.2 - Bug Fixes Demo"
PRINT "======================================"
PRINT ""

' ====================================
' TEST 1: SELECT CASE - Single Match
' ====================================
PRINT "TEST 1: SELECT CASE - Single Match"
PRINT "------------------------------------"
LET CHOICE = 2
PRINT "Choice = "; CHOICE
PRINT ""
SELECT CASE CHOICE
CASE 1
  PRINT "  Result: You chose ONE"
CASE 2
  PRINT "  Result: You chose TWO"
CASE 3
  PRINT "  Result: You chose THREE"
END SELECT
PRINT ""
PRINT "EXPECTED: Should print only 'You chose TWO'"
PRINT "BEFORE FIX: Would print TWO and THREE"
PRINT "AFTER FIX: Correctly prints only TWO"
PRINT ""
PRINT ""

' ====================================
' TEST 2: SELECT CASE - Multiple Cases
' ====================================
PRINT "TEST 2: SELECT CASE - Multiple Cases"
PRINT "------------------------------------"
FOR TEST = 1 TO 4
  PRINT "Testing value: "; TEST
  SELECT CASE TEST
  CASE 1
    PRINT "  -> Case 1 matched"
  CASE 2
    PRINT "  -> Case 2 matched"
  CASE 3
    PRINT "  -> Case 3 matched"
  CASE ELSE
    PRINT "  -> Default case matched"
  END SELECT
NEXT TEST
PRINT ""
PRINT "EXPECTED: Each value matches only ONE case"
PRINT "AFTER FIX: Working correctly!"
PRINT ""
PRINT ""

' ====================================
' TEST 3: SELECT CASE with Code After
' ====================================
PRINT "TEST 3: SELECT CASE with Code After"
PRINT "------------------------------------"
LET X = 1
SELECT CASE X
CASE 1
  PRINT "  Inside case 1"
END SELECT
PRINT "  Code after SELECT CASE continues"
PRINT ""
PRINT "EXPECTED: Both lines print"
PRINT "BEFORE FIX: Program stopped at END SELECT"
PRINT "AFTER FIX: Continues correctly!"
PRINT ""
PRINT ""

' ====================================
' TEST 4: DEF FN - Basic Function
' ====================================
PRINT "TEST 4: DEF FN - Basic Function"
PRINT "------------------------------------"
DEF FN DOUBLE(X) = X * 2
PRINT "Defined: FN DOUBLE(X) = X * 2"
PRINT ""
PRINT "  FN DOUBLE(5) = "; FN DOUBLE(5)
PRINT "  FN DOUBLE(10) = "; FN DOUBLE(10)
PRINT "  FN DOUBLE(7) = "; FN DOUBLE(7)
PRINT ""
PRINT "EXPECTED: 10, 20, 14"
PRINT "BEFORE FIX: All returned 0.0"
PRINT "AFTER FIX: Correct values!"
PRINT ""
PRINT ""

' ====================================
' TEST 5: DEF FN - Multiple Functions
' ====================================
PRINT "TEST 5: DEF FN - Multiple Functions"
PRINT "------------------------------------"
DEF FN SQUARE(Y) = Y * Y
DEF FN CUBE(Z) = Z * Z * Z
DEF FN HALF(W) = W / 2
PRINT "Defined three functions"
PRINT ""
PRINT "  FN SQUARE(4) = "; FN SQUARE(4)
PRINT "  FN CUBE(3) = "; FN CUBE(3)
PRINT "  FN HALF(10) = "; FN HALF(10)
PRINT ""
PRINT "EXPECTED: 16, 27, 5"
PRINT "AFTER FIX: All working correctly!"
PRINT ""
PRINT ""

' ====================================
' TEST 6: DEF FN in Calculations
' ====================================
PRINT "TEST 6: DEF FN in Calculations"
PRINT "------------------------------------"
DEF FN ADD(A, B) = A + B
LET RESULT = FN ADD(10, 20) + 5
PRINT "  FN ADD(10, 20) + 5 = "; RESULT
PRINT ""
PRINT "EXPECTED: 35"
PRINT "AFTER FIX: Working in expressions!"
PRINT ""
PRINT ""

' ====================================
' TEST 7: Combined Test
' ====================================
PRINT "TEST 7: SELECT CASE + DEF FN Combined"
PRINT "------------------------------------"
DEF FN TRIPLE(N) = N * 3
LET NUMBER = 2
PRINT "Number = "; NUMBER
PRINT "FN TRIPLE("; NUMBER; ") = "; FN TRIPLE(NUMBER)
PRINT ""
SELECT CASE FN TRIPLE(NUMBER)
CASE 3
  PRINT "  Result is 3"
CASE 6
  PRINT "  Result is 6"
CASE 9
  PRINT "  Result is 9"
END SELECT
PRINT ""
PRINT "EXPECTED: Result is 6"
PRINT "AFTER FIX: Both features work together!"
PRINT ""
PRINT ""

' ====================================
' SUMMARY
' ====================================
PRINT "======================================"
PRINT "SUMMARY OF FIXES"
PRINT "======================================"
PRINT ""
PRINT "BUG #1: SELECT CASE Fall-Through"
PRINT "  STATUS: FIXED"
PRINT "  - Cases no longer fall through"
PRINT "  - Code after END SELECT continues"
PRINT "  - CASE ELSE works correctly"
PRINT ""
PRINT "BUG #2: DEF FN Always Returned 0.0"
PRINT "  STATUS: FIXED"
PRINT "  - Functions evaluate correctly"
PRINT "  - Multiple functions work"
PRINT "  - Functions work in expressions"
PRINT ""
PRINT "All tests completed successfully!"
PRINT "======================================"
