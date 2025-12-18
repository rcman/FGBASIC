' 256 Sprite Stress Test
' Creates 256 sprites moving in random directions with edge bouncing

PRINT "=== 256 Sprite Stress Test ==="
PRINT "Creating 256 sprites with random velocities..."
PRINT ""

CLS

' Create 256 sprites (16x16 grid pattern initially)
FOR I = 0 TO 255
    ' Create small 8x8 sprites
    SPRITE CREATE, I, 8, 8

    ' Random color for each sprite
    COLOR 100 + RND(155), 100 + RND(155), 100 + RND(155)

    ' Fill sprite with color
    FOR X = 0 TO 7
        FOR Y = 0 TO 7
            SPRITE PIXEL, I, X, Y
        NEXT Y
    NEXT X

    ' Position in 16x16 grid initially
    LET ROW = INT(I / 16)
    LET COL = I MOD 16
    SPRITE MOVE, I, COL * 40, ROW * 30

    ' Show sprite
    SPRITE SHOW, I

    ' Progress indicator
    IF I MOD 32 = 0 THEN
        PRINT "Created ", I, " sprites..."
    END IF
NEXT I

PRINT ""
PRINT "All 256 sprites created!"
PRINT "Starting animation with edge bouncing..."
PRINT ""

' Animate all sprites with random velocities
' bounceMode = 1 means bounce off edges
FOR I = 0 TO 255
    ' Random velocity between -5 and 5 pixels per frame
    LET VX = (RND - 0.5) * 10
    LET VY = (RND - 0.5) * 10

    ' 60 FPS, bounceMode=1 (bounce off edges)
    ANIMATE START, I, VX, VY, 60, 1
NEXT I

PRINT "All sprites animating!"
PRINT "Watch them bounce around the screen..."
PRINT ""

' Let them animate for 10 seconds
COLOR 255, 255, 0
TEXT 10, 460, "256 SPRITES BOUNCING!"

WAIT 10

' Stop all animations
PRINT "Stopping animations..."
ANIMATE STOPALL

' Clean up - delete all sprites
PRINT "Cleaning up..."
FOR I = 0 TO 255
    SPRITE DELETE, I
NEXT I

CLS
COLOR 0, 255, 0
TEXT 200, 200, "STRESS TEST COMPLETE!"
TEXT 180, 230, "256 sprites handled successfully"

PRINT ""
PRINT "=== Test Complete ==="
PRINT "256 sprites were animated simultaneously"
PRINT "with edge bouncing for 10 seconds."
PRINT ""

WAIT 2
END
