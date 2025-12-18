' Simple sprite test - just 10 sprites
PRINT "Creating 10 test sprites..."
CLS

' Create 10 sprites
FOR I = 0 TO 9
    SPRITE CREATE, I, 16, 16
    COLOR 255, 0, 0

    ' Fill sprite with red
    FOR X = 0 TO 15
        FOR Y = 0 TO 15
            SPRITE PIXEL, I, X, Y
        NEXT Y
    NEXT X

    ' Position sprites in a row
    SPRITE MOVE, I, I * 60, 100
    SPRITE SHOW, I
    PRINT "Created sprite ", I
NEXT I

PRINT "All sprites visible!"
PRINT "Press any key..."
WAIT 2

' Test animation
PRINT "Starting animation..."
ANIMATE START, 0, 3, 2, 60, 1
ANIMATE START, 1, -2, 3, 60, 1
ANIMATE START, 2, 4, -1, 60, 1

WAIT 5

PRINT "Stopping..."
ANIMATE STOPALL

' Cleanup
FOR I = 0 TO 9
    SPRITE DELETE, I
NEXT I

PRINT "Test complete!"
END
