package assignment3;

import org.junit.jupiter.api.*;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.Random;

import static org.junit.Assert.*;

public class ExtraTests {

    @BeforeEach
    public void resetFields() {
        Block.gen = new Random(2);
    }


    // Block()
    @Test
    public void blockPDFExample() {
        Block blockDepth2 = new Block(0,2);
        String expected = "0000GREEN0001RED0001YELLOW00010001BLUE0002RED0002YELLOW0002BLUE0002";
        final String output = ISystemOutAcquire.acquire(blockDepth2::printBlock).replaceAll("[^A-Z0-9]", "");
        assertEquals(expected, output);
    }


    // updateSizeAndPosition()
    @Test
    public void updateSizeAndPositionPDFExample() {
        Block blockDepth2 = new Block(0,2);
        blockDepth2.updateSizeAndPosition(16, 0, 0);
        String expected = "00160GREEN8081RED0081YELLOW08818881BLUE12842RED8842YELLOW81242BLUE121242";
        final String output = ISystemOutAcquire.acquire(blockDepth2::printBlock).replaceAll("[^A-Z0-9]", "");
        assertEquals(expected, output);
    }

    @Test
    public void updateSizeAndPositionInputValidation() {
        Block blockDepth2 = new Block(0,2);
        assertThrows(IllegalArgumentException.class, ()->{
            blockDepth2.updateSizeAndPosition(-32,0,0);
        });
        assertThrows(IllegalArgumentException.class, ()->{
            blockDepth2.updateSizeAndPosition(7,0,0);
        });
    }


    // getSelectedBlock()
    @Test
    public void getSelectedBlockCornerCase() throws NoSuchFieldException, IllegalAccessException {
        Block.gen = new Random(46);
        Block testBoard = new Block(0, 3);
        testBoard.updateSizeAndPosition(16,0,0);
        Block expectedGreen20 = testBoard.getSelectedBlock(2,0,3);
        Block expectedBlue02 = testBoard.getSelectedBlock(1, 2, 3);
        Block expectedRed22 = testBoard.getSelectedBlock(2, 2, 3);
        Block expectedYellow40 = testBoard.getSelectedBlock(5,1,3);

        // chmod fields
        Field color = Block.class.getDeclaredField("color");
        color.setAccessible(true);
        Field x = Block.class.getDeclaredField("xCoord");
        x.setAccessible(true);
        Field y = Block.class.getDeclaredField("yCoord");
        y.setAccessible(true);

        // check correct behavior
        assertEquals(GameColors.GREEN, color.get(expectedGreen20));
        assertEquals(2, x.get(expectedGreen20));
        assertEquals(0, y.get(expectedGreen20));

        assertEquals(GameColors.BLUE, color.get(expectedBlue02));
        assertEquals(0, x.get(expectedBlue02));
        assertEquals(2, y.get(expectedBlue02));

        assertEquals(GameColors.RED, color.get(expectedRed22));
        assertEquals(2, x.get(expectedRed22));
        assertEquals(2, y.get(expectedRed22));

        assertEquals(GameColors.YELLOW, color.get(expectedYellow40));
        assertEquals(4, x.get(expectedYellow40));
        assertEquals(0, y.get(expectedYellow40));
    }

    @Test
    public void getSelectedBlockDepthTest() throws NoSuchFieldException, IllegalAccessException {
        Block.gen = new Random(33);
        Block testBoard = new Block(0,3);
        testBoard.updateSizeAndPosition(16,0,0);

        Block expectedBlue00 = testBoard.getSelectedBlock(0,0,3);
        Block expectedParent00 = testBoard.getSelectedBlock(0,0,2);
        boolean grabbedTestBoard = (testBoard.getSelectedBlock(0,0,0) == testBoard);

        // chmod fields
        Field color = Block.class.getDeclaredField("color");
        color.setAccessible(true);
        Field x = Block.class.getDeclaredField("xCoord");
        x.setAccessible(true);
        Field y = Block.class.getDeclaredField("yCoord");
        y.setAccessible(true);
        Field children = Block.class.getDeclaredField("children");
        children.setAccessible(true);
        Block[] parent00Children = (Block[]) children.get(expectedParent00);

        assertEquals(GameColors.BLUE, color.get(expectedBlue00));
        assertEquals(4, (parent00Children.length));
        assertTrue(grabbedTestBoard);
    }

    @Test
    public void getSelectedBlockInputValidation() {
        Block testBoard = new Block(0,3);
        testBoard.updateSizeAndPosition(16,0,0);
        assertThrows(IllegalArgumentException.class, ()->{
            testBoard.getSelectedBlock(0,0,4);
        });
        assertThrows(IllegalArgumentException.class, ()->{
            testBoard.getSelectedBlock(0,0,-1);
        });
        assertThrows(IllegalArgumentException.class, ()->{
            testBoard.getSelectedBlock(500,0,4);
        });
    }

    @Test
    public void getSelectedBlockPDFExample() throws NoSuchFieldException, IllegalAccessException {
        Block.gen = new Random(4);
        Block blockDepth3 = new Block(0,3);
        blockDepth3.updateSizeAndPosition(16,0,0);
        Block b1 = blockDepth3.getSelectedBlock(2,15,1);
        Block b2 = blockDepth3.getSelectedBlock(3,5,2);

        // chmod fields
        Field color = Block.class.getDeclaredField("color");
        color.setAccessible(true);
        Field x = Block.class.getDeclaredField("xCoord");
        x.setAccessible(true);
        Field y = Block.class.getDeclaredField("yCoord");
        y.setAccessible(true);
        Field children = Block.class.getDeclaredField("children");
        children.setAccessible(true);
        Block[] b2Children = (Block[]) children.get(b2);

        assertEquals(GameColors.RED, color.get(b1));
        assertEquals(0, x.get(b1));
        assertEquals(8, y.get(b1));

        assertEquals(0, x.get(b2));
        assertEquals(4, y.get(b2));
        assertEquals(4, b2Children.length);
    }


    // reflect()
    @Test
    public void reflectPDFExample() {
        Block.gen = new Random(4);
        Block testBoard = new Block(0,3);
        testBoard.updateSizeAndPosition(16,0,0);
        testBoard.reflect(0);
        String expected = "REDREDYELLOWBLUEGREENYELLOWGREENYELLOWBLUEREDGREENGREENREDREDGREENYELLOWGREENBLUEBLUEYELLOWGREENYELLOWGREENBLUEYELLOWREDGREENYELLOW";
        final String output = ISystemOutAcquire.acquire(testBoard::printBlock).replaceAll("[^A-Z]","");
        assertEquals(expected, output);
    }

    @Test
    public void reflectTwiceNoChange() {
        Block.gen = new Random(4);
        Block testBoard = new Block(0,3);
        testBoard.updateSizeAndPosition(16,0,0);
        final String before = ISystemOutAcquire.acquire(testBoard::printBlock).replaceAll("[^A-Z]","");
        testBoard.reflect(0);
        testBoard.reflect(1);
        testBoard.reflect(0);
        testBoard.reflect(1);
        final String after = ISystemOutAcquire.acquire(testBoard::printBlock).replaceAll("[^A-Z]","");
        assertEquals(before, after);
    }

    @Test
    public void reflectOverYAxis() {
        Block.gen = new Random(4);
        Block testBoard = new Block(0,3);
        testBoard.updateSizeAndPosition(16,0,0);
        testBoard.reflect(1);
        final String expected = "GREENREDGREENBLUEYELLOWGREENBLUEGREENYELLOWGREENYELLOWBLUEYELLOWYELLOWYELLOWBLUEYELLOWGREENGREENREDREDGREENREDBLUEGREENREDYELLOWRED";
        final String output = ISystemOutAcquire.acquire(testBoard::printBlock).replaceAll("[^A-Z]","");
        assertEquals(expected,output);
    }


    // rotate()
    @Test
    public void rotatePDFExample() {
        Block.gen = new Random(4);
        Block testBoard = new Block(0,3);
        testBoard.updateSizeAndPosition(16,0,0);
        testBoard.rotate(1);
        String expected = "GREENGREENYELLOWBLUEYELLOWGREENYELLOWGREENBLUEREDYELLOWBLUEGREENREDYELLOWGREENYELLOWBLUEBLUEYELLOWREDGREENREDGREENGREENREDREDYELLOW";
        final String output = ISystemOutAcquire.acquire(testBoard::printBlock).replaceAll("[^A-Z]","");
        assertEquals(expected, output);
    }

    @Test
    public void rotateMultipleDoNothing() {
        // rotating CW then CCW should cancel each other and do nothing
        // four rotations should return to original position
        Block.gen = new Random(4);
        Block testBoard = new Block(0,3);
        testBoard.updateSizeAndPosition(16,0,0);
        final String before = ISystemOutAcquire.acquire(testBoard::printBlock).replaceAll("[^A-Z]","");

        testBoard.rotate(0);
        testBoard.rotate(1);
        final String after = ISystemOutAcquire.acquire(testBoard::printBlock).replaceAll("[^A-Z]","");

        testBoard.rotate(0);
        testBoard.rotate(0);
        testBoard.rotate(0);
        testBoard.rotate(0);
        final String after2 = ISystemOutAcquire.acquire(testBoard::printBlock).replaceAll("[^A-Z]","");

        assertEquals(before, after);
        assertEquals(before, after2);
    }


    // smash()
    @Test
    public void smashPDFExample() {
        Block.gen = new Random(4);
        Block testBoard = new Block(0,3);
        testBoard.updateSizeAndPosition(16,0,0);
        Block blockToSmash = testBoard.getSelectedBlock(0,0,2);
        blockToSmash.smash();
        final String output = ISystemOutAcquire.acquire(blockToSmash::printBlock).replaceAll("[^A-Z]", "");
        assertEquals("GREENGREENYELLOWRED", output);

        Block.gen = new Random(4);
        Block testBoard2 = new Block(0,3);
        testBoard2.updateSizeAndPosition(16,0,0);
        Block blockToSmash2 = testBoard2.getSelectedBlock(0,0,1);
        blockToSmash2.smash();
        final String output2 = ISystemOutAcquire.acquire(blockToSmash2::printBlock).replaceAll("[^A-Z]", "");
        assertEquals("YELLOWREDBLUEBLUEBLUEYELLOWYELLOWBLUEGREENREDREDBLUEYELLOW", output2);
    }

    @Test
    public void smashDoNothingTopLevel() {
        // smash should not do anything on the top level
        Block.gen = new Random(4);
        Block testBoard = new Block(0,3);
        testBoard.updateSizeAndPosition(16,0,0);
        final String before = ISystemOutAcquire.acquire(testBoard::printBlock).replaceAll("[^A-Z]","");
        Boolean returnsFalse = testBoard.smash();
        final String after = ISystemOutAcquire.acquire(testBoard::printBlock).replaceAll("[^A-Z]","");
        assertEquals(before, after);
        assertFalse(returnsFalse);
    }

    @Test
    public void smashDoNothingBottomLevel() {
        Block.gen = new Random(4);
        Block testBoard = new Block(0,3);
        testBoard.updateSizeAndPosition(16,0,0);
        Block blockToSmash = testBoard.getSelectedBlock(5,0,3);
        final String before = ISystemOutAcquire.acquire(blockToSmash::printBlock).replaceAll("[^A-Z]","");
        Boolean returnsFalse = blockToSmash.smash();
        final String after = ISystemOutAcquire.acquire(blockToSmash::printBlock).replaceAll("[^A-Z]","");
        assertEquals(before, after);
        assertFalse(returnsFalse);
    }

    // flatten()
    @Test
    public void testFlatten2x2() {
        Block.gen = new Random(2);
        Block testBlock = new Block(0,1);
        testBlock.updateSizeAndPosition(16,0,0);
        final String output = ISystemOutAcquire.acquire(testBlock::printColoredBlock).replaceAll("[^A-Z]", "");
        String expectedOutput = "RYGY";
        assertEquals(expectedOutput, output);
    }

    @Test
    public void testFlatten3x3() {
        Block testBlock = new Block(0,2);
        testBlock.updateSizeAndPosition(16,0,0);
        final String output = ISystemOutAcquire.acquire(testBlock::printColoredBlock).replaceAll("[^A-Z]", "");
        String expectedOutput = "\nRRGG\nRRGG\nYYRB\nYYYB]".replaceAll("[^A-Z]", "");
        assertEquals(expectedOutput, output);
    }

    @Test
    public void testFlatten4x4() {
        Block testBlock = new Block(0,3);
        testBlock.updateSizeAndPosition(16,0,0);
        final String output = ISystemOutAcquire.acquire(testBlock::printColoredBlock).replaceAll("[^A-Z]", "");
        String expectedOutput = "RRRRGGGGRRRRGGGGRRRRGGGGRRRRGGGGYYYYBRBYYYYYYYRYYYYYGGBBYYYYYGGG".replaceAll("[^A-Z]", "");
        assertEquals(expectedOutput, output);
    }


    // undiscoveredBlobSize()
    @Test
    public void undiscoveredBlobSizeWrongColor() {
        Block testBlock = new Block(0,2);
        testBlock.updateSizeAndPosition(16,0,0);
        BlobGoal theGoal = new BlobGoal(GameColors.RED);
        Color[][] flattenedBlock = testBlock.flatten();
        boolean[][] bools = new boolean[flattenedBlock.length][flattenedBlock[0].length];
        assertEquals(0, theGoal.undiscoveredBlobSize(2,0,flattenedBlock, bools));
        assertEquals(0, theGoal.undiscoveredBlobSize(1,2,flattenedBlock, bools));
        assertEquals(0, theGoal.undiscoveredBlobSize(3,3,flattenedBlock, bools));
    }

    @Test
    public void undiscoveredBlobSizeBooleanFields() {
        Block testBlock = new Block(0,2);
        testBlock.updateSizeAndPosition(16,0,0);
        BlobGoal theGoal = new BlobGoal(GameColors.RED);
        Color[][] flattenedBlock = testBlock.flatten();
        boolean[][] bools = new boolean[flattenedBlock.length][flattenedBlock[0].length];
        assertEquals(4, theGoal.undiscoveredBlobSize(0,0, flattenedBlock, bools));
        // should be 0 afterward since the blob has alr been counted
        assertEquals(0, theGoal.undiscoveredBlobSize(0,0,flattenedBlock,bools));
    }

    @Test
    public void undiscoveredBlobSizeLarge() {
        Block.gen = new Random(88);
        Block testBlock = new Block(0,4);
        testBlock.updateSizeAndPosition(16,0,0);
        BlobGoal theGoal = new BlobGoal(GameColors.RED);
        Color[][] flattenedBlock = testBlock.flatten();
        boolean[][] bools = new boolean[flattenedBlock.length][flattenedBlock[0].length];
        assertEquals(85,theGoal.undiscoveredBlobSize(9,9,flattenedBlock,bools));
    }

    @Test
    public void undiscoveredBlobSizeNoDiagonals() {
        // make sure diagonals do NOT count
        Block testBlock = new Block(0,2);
        testBlock.updateSizeAndPosition(16,0,0);
        BlobGoal theGoal = new BlobGoal(GameColors.RED);
        Color[][] flattenedBlock = testBlock.flatten();
        boolean[][] bools = new boolean[flattenedBlock.length][flattenedBlock[0].length];
        assertEquals(1, theGoal.undiscoveredBlobSize(2,2, flattenedBlock, bools));
    }


    // PerimeterGoal.score()
    @Test
    public void perimeterGoalCornerDoublePoint() {
        // make sure corners give 2x points
        Block testBlock = new Block(0,2);
        testBlock.updateSizeAndPosition(16,0,0);
        PerimeterGoal theGoal = new PerimeterGoal(GameColors.GREEN);
        assertEquals(4,theGoal.score(testBlock));
    }

    @Test
    public void perimeterGoalSingleCell() {
        Block testBlock = new Block(0,0);
        testBlock.updateSizeAndPosition(2,0,0);
        PerimeterGoal theGoal = new PerimeterGoal(GameColors.BLUE);
        assertEquals(2, theGoal.score(testBlock));
    }

    @Test
    public void perimeterGoalSmall() {
        Block testBlock = new Block(0,2);
        testBlock.updateSizeAndPosition(16,0,0);
        PerimeterGoal theGoal = new PerimeterGoal(GameColors.RED);
        assertEquals(4,theGoal.score(testBlock));
    }

    @Test
    public void perimeterGoalMedium() {
        Block testBlock = new Block(0,4);
        testBlock.updateSizeAndPosition(16,0,0);
        PerimeterGoal theGoal = new PerimeterGoal(GameColors.RED);
        assertEquals(26,theGoal.score(testBlock));
    }

    @Test
    public void perimeterGoalLarge() {
        Block testBlock = new Block(0, 6);
        testBlock.updateSizeAndPosition(256,0,0);
        PerimeterGoal theGoal = new PerimeterGoal(GameColors.YELLOW);
        assertEquals(64,theGoal.score(testBlock));
    }


    // BlobGoal.score()
    @Test
    public void blobGoalSpreadOut() {
        Block.gen = new Random(78);
        Block testBlock = new Block(0,3);
        testBlock.updateSizeAndPosition(16,0,0);
        BlobGoal theGoal = new BlobGoal(GameColors.BLUE);
        assertEquals(4,theGoal.score(testBlock));
    }

    @Test
    public void blobGoalSmall() {
        Block testBlock = new Block(0,2);
        testBlock.updateSizeAndPosition(16,0,0);
        BlobGoal theGoal = new BlobGoal(GameColors.RED);
        assertEquals(4,theGoal.score(testBlock));
    }

    @Test
    public void blobGoalMedium() {
        Block testBlock = new Block(0,3);
        testBlock.updateSizeAndPosition(16,0,0);
        BlobGoal theGoal = new BlobGoal(GameColors.RED);
        assertEquals(16,theGoal.score(testBlock));
    }

    @Test
    public void blobGoalLarge() {
        Block testBlock = new Block(0,5);
        testBlock.updateSizeAndPosition(32,0,0);
        BlobGoal theGoal = new BlobGoal(GameColors.GREEN);
        assertEquals(256,theGoal.score(testBlock));
    }


    // output capturing stolen from stackoverflow
    // https://stackoverflow.com/questions/8708342/redirect-console-output-to-string-in-java
    public interface ISystemOutAcquire extends AutoCloseable {

        PrintStream getOriginalOut();

        PrintStream getNewOut();

        /**
         * Restores the original stream.
         */
        @Override
        void close();

        /**
         * Replaces the original {@link PrintStream} with another one.
         */
        class ToPrintStream implements ISystemOutAcquire {
            private final PrintStream originalOut;
            private final PrintStream newOut;

            public ToPrintStream(PrintStream newOut) {
                this.originalOut = System.out;
                this.newOut = newOut;
                originalOut.flush();
                newOut.flush();
                System.setOut(newOut);
            }

            @Override
            public void close() {
                originalOut.flush();
                newOut.flush();
                System.setOut(originalOut);
            }

            @Override
            public PrintStream getOriginalOut() {
                return originalOut;
            }

            @Override
            public PrintStream getNewOut() {
                return newOut;
            }
        }

        public static ToPrintStream acquire(final PrintStream newOut) {
            return new ToPrintStream(newOut);
        }

        /**
         * Captures the {@link System#out} to {@link ByteArrayOutputStream}.
         * <p>
         * Overrides the {@link #toString()} method, so all captured text is accessible.
         */
        class ToByteArray extends ToPrintStream {

            private final ByteArrayOutputStream byteArrayOutputStream;

            public ToByteArray(final ByteArrayOutputStream byteArrayOutputStream) {
                super(new PrintStream(byteArrayOutputStream));
                this.byteArrayOutputStream = byteArrayOutputStream;
            }

            @Override
            public String toString() {
                return byteArrayOutputStream.toString();
            }

            public ByteArrayOutputStream getByteArrayOutputStream() {
                return byteArrayOutputStream;
            }
        }

        static ToByteArray acquire(final ByteArrayOutputStream byteArrayOutputStream) {
            return new ToByteArray(byteArrayOutputStream);
        }

        static ToByteArray acquire() {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            return acquire(byteArrayOutputStream);
        }

        static String acquire(final Runnable runnable) {
            try (ISystemOutAcquire acquire = ISystemOutAcquire.acquire()) {
                runnable.run();
                return acquire.toString();
            }
        }
    }

}
