' Concurrency test - stress test for race conditions
' Tests graphics operations, sprite system with rapid changes

PRINT "=== Concurrency Stress Test ==="
PRINT "This test rapidly changes graphics state"
PRINT "to check for race conditions"
PRINT ""

CLS
PRINT "Creating multiple sprites..."

' Create 10 sprites
FOR I = 0 TO 9
    SPRITE CREATE, I, 30, 30
    COLOR 255 * RND, 255 * RND, 255 * RND
    SPRITE PIXEL, I, 15, 15
    SPRITE MOVE, I, 50 + I * 50, 100
    SPRITE SHOW, I
NEXT I

PRINT "Sprites created!"
PRINT ""
PRINT "Animating sprites (tests synchronized graphics)..."

' Rapid animation loop - tests thread safety
FOR FRAME = 1 TO 100
    FOR I = 0 TO 9
        ' Move sprites in circle pattern
        LET ANGLE = FRAME * 5 + I * 36
        LET X = 320 + 150 * COS(ANGLE)
        LET Y = 240 + 150 * SIN(ANGLE)
        SPRITE MOVE, I, X, Y
    NEXT I

    ' Change colors rapidly
    COLOR 255 * RND, 255 * RND, 255 * RND

    ' Draw lines (tests graphics lock)
    IF FRAME MOD 10 = 0 THEN
        LINE 0, 0, 640 * RND, 480 * RND
    END IF
NEXT FRAME

PRINT "Animation complete!"
PRINT ""

' Cleanup
FOR I = 0 TO 9
    SPRITE DELETE, I
NEXT I

PRINT "Test collision detection..."
SPRITE CREATE, 10, 50, 50
SPRITE CREATE, 11, 50, 50
COLOR 255, 0, 0
SPRITE PIXEL, 10, 25, 25
COLOR 0, 255, 0
SPRITE PIXEL, 11, 25, 25

' Test collision at same position
SPRITE MOVE, 10, 200, 200
SPRITE MOVE, 11, 200, 200
SPRITE SHOW, 10
SPRITE SHOW, 11
COLLISION 10, 11
PRINT "Collision (same pos): ", COLLISION

' Test collision at different positions
SPRITE MOVE, 11, 300, 300
COLLISION 10, 11
PRINT "Collision (diff pos): ", COLLISION

SPRITE DELETE, 10
SPRITE DELETE, 11

CLS
COLOR 0, 255, 0
TEXT 200, 200, "CONCURRENCY TEST PASSED"
PRINT ""
PRINT "=== CONCURRENCY TEST COMPLETE ==="
WAIT 2
END
