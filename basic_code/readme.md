# This is for testing

<BR>

MODE 4 REM  1280x720 HD mode<BR>                      
FOR I = 0 TO 99<BR>
SPRITE CREATE I, 32, 32, 0x00FF0000<BR>
NEXT I

FOR J= 0 to 99<BR>
SPRITE MOVE J, RND*1280, RND*720<BR>
NEXT J

FOR X = 0 to 99
SPRITE ROTATE X, RND*360<BR>
NEXT X<BR>

COLOR 255, 255, 0    REM SET THE COLOR OF THE TEXT<BR>
TEXT 100, 100, "100 sprites at 60 FPS!"   REM You can use TEXT or GPRINT - Both work the same <BR>
