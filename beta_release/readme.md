# FGbasic Beta Release

## Project Overview

FGBasic 2.0.2 is a BASIC interpreter written in Java with an integrated IDE, syntax highlighting, debugging capabilities, and comprehensive graphics/multimedia support. The interpreter supports classic BASIC with modern extensions including sprites, 3D graphics, sound, networking, and turtle graphics.

## Building and Running

```bash
# Compile all Java files
javac *.java

# Or use the provided script
./compile.sh

# Run the IDE
java Main

# Run the IDE with a specific file
java Main program.bas
```

The application launches two windows:
- **EditorWindow**: Code editor with syntax highlighting and debugging (F5=Run, F9=Toggle Breakpoint, F10=Step, Ctrl+S=Save, Ctrl+B=Compile)
- **GraphicsWindow**: 640x480 graphics output window

## Compiling BASIC Programs to Executables

The interpreter can compile BASIC programs into standalone JAR executables:

1. In the IDE: File → Compile to Executable... (Ctrl+B)
2. Enter program name and select output directory
3. Three files are created:
   - `program.jar` - Executable JAR
   - `program.sh` - Linux/Mac launcher
   - `program.bat` - Windows launcher

**Requirements**: JDK (not JRE) must be installed to compile.

**Run compiled programs**:
```bash
./program.sh        # Linux/Mac
program.bat         # Windows
java -jar program.jar
```

## Architecture Overview

### Core Execution Engine

**Interpreter.java** (1496 lines) - Central execution engine containing:
- Program state: `variables` (Map<String, Double>), `stringVars` (Map<String, String>), `arrays` (Map<String, double[]>)
- Control flow stacks: `forStack`, `whileStack`, `doStack`, `gosubStack`
- User-defined functions: `userFunctions` (Map<String, UserFunction>)
- Large switch statement (~60+ BASIC commands) in `executeLine()`
- Debug support: `breakpoints` (Set<Integer>), `stepMode`, `waitingForStep`
- DATA/READ/RESTORE: `dataStatements` (List<String>), `dataPointer`
- Integration with all subsystems (graphics, sound, sprites, network, turtle, 3D)

**Inner classes in Interpreter.java**:
- `CodeLine` - Stores line number and code text
- `ForLoop` - FOR loop state (variable name, end value, step)
- `WhileLoop` - WHILE loop state (start line)
- `DoLoop` - DO loop state (start line, condition, isWhile, isUntil)
- `UserFunction` - User-defined function (parameter list, expression)

**ExpressionParser.java** (428 lines) - Evaluates numeric and string expressions:
- Arithmetic operations with proper precedence
- String concatenation and manipulation
- Built-in functions: SIN, COS, TAN, ABS, INT, RND, SGN, SQR, LOG, EXP, ATN
- String functions: LEN, MID, LEFT, RIGHT, CHR, ASC, STR, VAL, INSTR
- Array access
- User-defined function calls
- Shared references to `variables`, `arrays`, `stringVars`, `userFunctions` from Interpreter

**CommandParser.java** (48 lines) - Lightweight parser that splits BASIC lines into command and remainder.

### Execution Flow

1. User writes code in EditorWindow or loads `.bas` file
2. On Run (F5), code parsed into `List<CodeLine>` with line numbers
3. Labels indexed in `labels` map for GOTO/GOSUB
4. `Interpreter.run()` executes lines sequentially
5. Each line processed by `executeLine()` → CommandParser → switch statement → execute method
6. ExpressionParser evaluates expressions in command arguments
7. Commands modify interpreter state or call subsystem methods
8. Graphics commands call GraphicsWindow methods
9. **Critical**: Graphics modifications require `graphics.repaint()` for immediate display

### IDE Components

**EditorWindow.java** (392 lines) - JFrame-based IDE:
- JTextPane with syntax highlighting (via SyntaxHighlighter)
- Line numbers panel
- Menu bar: File (New/Open/Save/Compile/Exit), Edit (Go to Line), Run (Run/Stop/Step/Toggle Breakpoint)
- Integrated DebugPanel on right side
- File I/O with try-with-resources for automatic cleanup
- Keyboard shortcuts: F5 (Run), F9 (Toggle Breakpoint), F10 (Step), Ctrl+N/O/S/B/G

**DebugPanel.java** (77 lines) - Displays during execution:
- Current line number
- Variables list (name=value)
- Arrays list
- Breakpoints list

**SyntaxHighlighter.java** (135 lines) - Applies color coding to BASIC keywords using StyledDocument.

### Graphics System

**GraphicsWindow.java** (254 lines) - JFrame containing GraphicsPanel:
- Maintains BufferedImage (640x480) for drawing
- Thread-safe with `graphicsLock` object synchronizing all drawing operations
- Tracks current color, font
- Integrates SpriteSystem and InputSystem
- Methods: `clear()`, `setColor()`, `drawLine()`, `drawRectangle()`, `drawCircle()`, `drawText()`, `setFont()`, `repaint()`
- **Important**: All Graphics2D objects properly disposed in synchronized blocks to prevent memory leaks

**SpriteSystem.java** (224 lines) - Manages sprites:
- ConcurrentHashMap<Integer, Sprite> for thread-safe storage
- Each sprite: id, BufferedImage, x, y, visible flag
- Methods: `createSprite()`, `deleteSprite()`, `setSpritePixel()`, `moveSprite()`, `setSpriteVisible()`, `checkCollision()`
- Input validation: max sprite dimensions 2048x2048
- Proper Graphics2D disposal in finally blocks

**InputSystem.java** (110 lines) - MouseListener + MouseMotionListener + KeyListener:
- Tracks mouse position (mouseX, mouseY)
- Mouse button state (mouseButton: 0=none, 1=left, 2=right, 3=middle)
- Last key pressed (lastKey)
- Keyboard state array (keys[256])

**TurtleGraphics.java** (79 lines) - Logo-style turtle graphics:
- State: position (x, y), heading (angle), penDown flag
- Commands: FORWARD, BACKWARD, LEFT, RIGHT, PENUP, PENDOWN, HOME

**Graphics3D.java** (137 lines) - Basic 3D rendering:
- Rotation matrices, perspective projection
- Wireframe drawing

### Multimedia Systems

**SoundSystem.java** (274 lines) - Waveform generation:
- Generates sine, square, sawtooth, triangle, noise waveforms
- Commands: SOUND (freq, duration), BEEP, PLAY (note string), RECORD, PLAYBACK
- Proper audio line cleanup with try-finally blocks
- Executor service for threaded sound playback
- `shutdown()` method for cleanup

**NetworkSystem.java** (118 lines) - Socket-based networking:
- ConcurrentHashMap for thread-safe connection storage
- Commands: NETOPEN (host, port), NETSEND, NETRECV, NETCLOSE
- 30-second socket timeout
- Input validation for host/port
- Proper resource cleanup on exceptions
- `closeAll()` method for shutdown

### Compilation System

**Compiler.java** (225 lines) - Compiles BASIC programs to standalone JARs:
1. Embeds program code into StandaloneRunner template
2. Copies required source files to temp directory
3. Compiles all Java files using ToolProvider.getSystemJavaCompiler()
4. Creates JAR with manifest (Main-Class: StandaloneRunner)
5. Generates launcher scripts (.sh and .bat)
6. Cleans up temp directory

**StandaloneRunner.java** (57 lines) - Main class for compiled programs:
- Template with `<<PROGRAM_CODE>>` placeholder
- Creates Interpreter and GraphicsWindow
- Loads and runs embedded BASIC code
- No EditorWindow (runtime only)

**Utility Classes**:
- **StringFunctions.java** (86 lines) - String manipulation utilities

## Critical Implementation Details

### Graphics Display Update Pattern

**All graphics-modifying commands MUST call `graphics.repaint()` after changes** to ensure immediate display update. This includes:
- Sprite operations (SHOW, HIDE, MOVE, PIXEL)
- Drawing commands (LINE, RECT, CIRCLE, PIXEL, TEXT)
- CLS (clear screen)

Example pattern:
```java
sprites.setSpriteVisible(id, true);
graphics.repaint();  // Required!
```

### Thread Safety

- **GraphicsWindow**: All drawing operations synchronized on `graphicsLock`
- **SpriteSystem**: Uses ConcurrentHashMap
- **NetworkSystem**: Uses ConcurrentHashMap
- **Interpreter**: Uses ConcurrentHashMap for variables, stringVars, arrays

### Resource Cleanup

The interpreter has proper resource management:
- **Shutdown hook** in Main.java calls `interpreter.shutdown()`
- **Interpreter.shutdown()**: closes all network connections, shuts down sound system executor
- **File I/O**: Uses try-with-resources in EditorWindow
- **Audio lines**: Properly closed in try-finally blocks
- **Graphics2D**: Disposed in finally blocks after use

### Sprite Command Structure

Sprite commands use pattern `SPRITE action, arguments`:
- `SPRITE CREATE, id, width, height` - Create sprite with dimensions
- `SPRITE SHOW, id` - Make sprite visible
- `SPRITE HIDE, id` - Make sprite invisible
- `SPRITE MOVE, id, x, y` - Move sprite to position
- `SPRITE PIXEL, id, x, y` - Set pixel using current COLOR
- `SPRITE DELETE, id` - Remove sprite
- `COLLISION id1, id2` - Check collision (stores result in COLLISION variable)

### Font Management

The FONT command sets persistent state:
```basic
FONT "Arial", 20, "BOLD"
```
Styles: PLAIN, BOLD, ITALIC, BOLDITALIC. All subsequent TEXT commands use this font.

### Control Flow Implementation

Control structures use stacks in Interpreter:
- **FOR loops**: Push ForLoop with (varName, endValue, stepValue), pop on NEXT
- **WHILE loops**: Push WhileLoop with startLine, pop on WEND
- **DO loops**: Push DoLoop with startLine and condition (WHILE/UNTIL), pop on LOOP
- **GOSUB**: Push return line number to gosubStack, pop on RETURN

Each structure can be nested. The stacks maintain execution context.

### User-Defined Functions

Functions defined with `DEF FN` statement:
```basic
DEF FN DOUBLE(X) = X * 2
PRINT FN DOUBLE(5)  ' Prints 10
```
Stored in `userFunctions` map. ExpressionParser evaluates function calls by substituting parameters.

### DATA/READ/RESTORE

- DATA statements collected during `loadProgram()` into `dataStatements` list
- READ command advances `dataPointer` and assigns values to variables
- RESTORE resets `dataPointer` to 0 (or specified line)

## Common Development Patterns

### Adding New BASIC Commands

1. Add keyword to `SyntaxHighlighter.java` keywords array
2. Add case to switch statement in `Interpreter.executeLine()`
3. Implement `executeCommandName()` method in Interpreter.java
4. Use `expressionParser.evaluate()` for numeric arguments
5. Use `expressionParser.evaluateString()` for string arguments
6. Call `graphics.repaint()` if command modifies display
7. Handle exceptions appropriately

### Modifying Graphics Subsystems

Graphics subsystems (SpriteSystem, TurtleGraphics, Graphics3D) are instantiated by GraphicsWindow and accessed via Interpreter. Changes to rendering logic go in subsystem classes; changes to command parsing go in Interpreter.

### Debug Features

- Breakpoints stored in `breakpoints` HashSet (line numbers)
- Step mode controlled by `stepMode` and `waitingForStep` boolean flags
- DebugPanel updates via interpreter state during execution
- Current line tracked in `currentLine` variable
- F9 toggles breakpoint on current editor line
- F10 steps through one line when paused

## Testing

Example programs:
- `test_program.bas` - Comprehensive sprite, font, animation test
- `game_demo.bas` - Game example with collision detection

Load via File → Open in IDE to test functionality.

## Known Patterns and Conventions

- **Error handling**: Commands print to System.err when graphics/subsystems not initialized
- **Thread interruption**: Execution loop checks `Thread.currentThread().isInterrupted()` for clean shutdown
- **Case insensitivity**: Commands converted to uppercase before switch statement
- **Line numbers**: Optional in modern mode, required for GOTO/GOSUB targets
- **Variables**: Numeric by default, string variables end with `$`
- **Arrays**: Allocated with DIM, zero-indexed in implementation
