' 256 Sprite Test - WORKING VERSION
' Simplified to avoid potential parser issues

PRINT "=== 256 Sprite Bouncing Test ==="
CLS

' Phase 1: Create 256 sprites
PRINT "Creating 256 sprites..."
FOR I = 0 TO 255
    SPRITE CREATE, I, 8, 8
    COLOR 255, 100, 100
    SPRITE PIXEL, I, 4, 4

    ' Position calculation without MOD
    LET ROW = INT(I / 16)
    LET COLTEMP = ROW * 16
    LET COL = I - COLTEMP
    LET XPOS = COL * 40
    LET YPOS = ROW * 30

    SPRITE MOVE, I, XPOS, YPOS
    SPRITE SHOW, I
NEXT I

PRINT "All 256 sprites created!"
WAIT 1

' Phase 2: Animate sprites with different velocities
PRINT "Starting animations..."
FOR I = 0 TO 255
    ' Calculate velocity based on sprite ID
    LET VXBASE = I / 32
    LET VYBASE = I / 64
    LET VX = VXBASE - 4
    LET VY = VYBASE - 2

    ANIMATE START, I, VX, VY, 60, 1
NEXT I

COLOR 255, 255, 0
TEXT 10, 460, "256 SPRITES BOUNCING!"
PRINT "All 256 sprites animating!"
PRINT "Watch for 10 seconds..."

WAIT 10

' Phase 3: Stop and cleanup
PRINT "Stopping animations..."
ANIMATE STOPALL

PRINT "Cleaning up..."
FOR I = 0 TO 255
    SPRITE DELETE, I
NEXT I

CLS
COLOR 0, 255, 0
TEXT 150, 200, "256 SPRITE TEST COMPLETE!"
TEXT 120, 230, "All sprites animated with edge bounce"

PRINT "Test successful!"
WAIT 2
END
