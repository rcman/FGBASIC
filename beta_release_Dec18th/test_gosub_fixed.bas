PRINT "Before GOSUB"
GOSUB 100
PRINT "After GOSUB returned"
PRINT "Program continues..."
END

100 PRINT "In subroutine at line 100"
110 RETURN
