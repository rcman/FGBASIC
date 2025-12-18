10 PRINT "Line 10: Before GOSUB"
20 GOSUB 100
30 PRINT "Line 30: After GOSUB"
40 GOTO 200

100 PRINT "Line 100: Inside subroutine"
110 RETURN

200 PRINT "Line 200: End"
