# Added

<BR>

- Added option for GPRINT to place text on a graphic screen.<BR>

<BR>

# Fixes

<BR>

- Fixed the double quotes for filenames in sprite names
- Sprites were being rendered so fast that there's no continuous refresh loop - they render once and that's it. Fix this.
- Lines starting with @ are treated as labels
- Lines with : (not in quotes) are treated as labels
- Lines starting with numbers are traditional BASIC line numbers
- Lines without any of the above get automatic line numbers (10, 20, 30...)
