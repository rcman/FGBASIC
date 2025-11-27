#!/bin/bash

echo "========================================="
echo "RUNNING ALL BASIC INTERPRETER TESTS"
echo "========================================="
echo ""

echo "Compiling Java files..."
javac *.java
if [ $? -ne 0 ]; then
    echo "COMPILATION FAILED!"
    exit 1
fi
echo "Compilation successful!"
echo ""

echo "========================================="
echo "TEST 1: Core Commands (comprehensive_test.bas)"
echo "========================================="
echo "This tests arithmetic, strings, loops, arrays, etc."
echo "Press Enter to start, then review console output..."
read
java Main comprehensive_test.bas
echo ""

echo "========================================="
echo "TEST 2: Graphics Commands (graphics_test.bas)"
echo "========================================="
echo "This tests drawing: lines, circles, text, etc."
echo "Press Enter to start, then check graphics window..."
read
java Main graphics_test.bas
echo ""

echo "========================================="
echo "TEST 3: Sprite Commands (sprite_test.bas)"
echo "========================================="
echo "This tests sprite creation, movement, collision, etc."
echo "Press Enter to start, then check graphics window..."
read
java Main sprite_test.bas
echo ""

echo "========================================="
echo "TEST 4: DIM Command (test_dim.bas)"
echo "========================================="
echo "This tests array dimensioning..."
read
java Main test_dim.bas
echo ""

echo "========================================="
echo "TEST 5: RND Function (test_rnd.bas)"
echo "========================================="
echo "This tests random number generation..."
read
java Main test_rnd.bas
echo ""

echo "========================================="
echo "ALL TESTS COMPLETE!"
echo "========================================="
echo ""
echo "Summary:"
echo "1. Review console output from comprehensive_test.bas"
echo "2. Verify graphics appeared in graphics_test.bas"
echo "3. Verify sprites appeared and moved in sprite_test.bas"
echo "4. Verify array values in test_dim.bas"
echo "5. Verify random values in test_rnd.bas"
echo ""
