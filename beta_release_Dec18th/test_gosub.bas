PRINT "Before GOSUB"
GOSUB 100
PRINT "After GOSUB"
GOTO 200

100 PRINT "Inside subroutine"
RETURN

200 PRINT "End"
