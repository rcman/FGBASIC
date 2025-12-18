REM Test sprite visibility with ANIMATE

10 SCREEN 640, 480, 60
20 CLS

30 REM Create 3 sprites
40 SPRITE CREATE, 1, 40, 40
50 SPRITE CREATE, 2, 30, 30
60 SPRITE CREATE, 3, 50, 50

70 REM Fill with colors
80 COLOR 255, 0, 0
90 SPRITE FILL, 1
100 COLOR 0, 255, 0
110 SPRITE FILL, 2
120 COLOR 0, 0, 255
130 SPRITE FILL, 3

140 REM Position sprites
150 SPRITE MOVE, 1, 100, 200
160 SPRITE MOVE, 2, 300, 200
170 SPRITE MOVE, 3, 500, 200

180 REM Show sprites
190 SPRITE SHOW, 1
200 SPRITE SHOW, 2
210 SPRITE SHOW, 3

220 REM Wait to see static sprites
230 COLOR 255, 255, 255
240 TEXT 200, 50, "Static Sprites - 3 seconds"
250 WAIT 3

260 REM Now animate them
270 TEXT 180, 100, "Now animating - 5 seconds"
280 ANIMATE START, 1, 2, 1, 60, 1
290 ANIMATE START, 2, 3, 2, 60, 1
300 ANIMATE START, 3, 1, 3, 60, 1

310 REM Let them animate
320 WAIT 5

330 REM Stop and display message
340 ANIMATE STOPALL
350 CLS
360 TEXT 200, 220, "Animation Complete!"
370 WAIT 2

380 END
