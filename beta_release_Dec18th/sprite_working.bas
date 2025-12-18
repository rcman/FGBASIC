' Working 256 Sprite Test - Fixed version
PRINT "Creating 256 sprites..."

CLS

' Create 256 sprites
LET CREATED = 0
FOR I = 0 TO 255
    SPRITE CREATE, I, 8, 8

    ' Red color for all sprites
    COLOR 255, 0, 0

    ' Fill sprite center pixel
    SPRITE PIXEL, I, 4, 4

    ' Calculate position
    LET ROW = INT(I / 16)
    LET COL = I - (ROW * 16)
    LET XPOS = COL * 40
    LET YPOS = ROW * 30

    SPRITE MOVE, I, XPOS, YPOS
    SPRITE SHOW, I

    LET CREATED = CREATED + 1
NEXT I

PRINT "Created sprites: "
PRINT CREATED

PRINT "Starting animations..."

' Animate first 10 sprites as a test
FOR I = 0 TO 9
    LET VX = 3
    LET VY = 2
    ANIMATE START, I, VX, VY, 60, 1
NEXT I

PRINT "10 sprites animating with bounce"
WAIT 5

ANIMATE STOPALL
PRINT "Animation stopped"

' Cleanup
FOR I = 0 TO 255
    SPRITE DELETE, I
NEXT I

PRINT "Test complete!"
END
