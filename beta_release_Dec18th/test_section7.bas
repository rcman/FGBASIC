PRINT "7.1 GOSUB/RETURN"
GOSUB 2000
PRINT "  Back from subroutine"
GOTO 2100

2000 REM Subroutine
PRINT "  Inside subroutine"
RETURN

2100 REM Continue
PRINT "Continued after GOTO"
