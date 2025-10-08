# Color

<BR>
This breaks down as:
<BR>

<BR>

FF = Alpha (255, fully opaque)
<BR>
FF = Red (255, full red)
<BR>
00 = Green (0, no green)
<BR>
00 = Blue (0, no blue)
<BR>

  SPRITE CREATE 0, 64, 64, 0xFFFF0000
<BR>

  If you want semi-transparent red, you can adjust the alpha channel:
<BR>

0x80FF0000 = 50% transparent red
<BR>
0x40FF0000 = 75% transparent red
<BR>

The code converts this to a Java Color object with the true parameter in the constructor, which tells it to interpret the value as ARGB including the alpha channel.
