package sk.uniza.fri.janmokry.karnaughmap.util;

import sk.uniza.fri.janmokry.karnaughmap.kmap.helper.Position;

/**
 * Utils for bit operations.
 *
 * Created by Janci on 31.3.2017.
 */
public class BitOperationUtil {

    public static boolean isNthBitSet(int value, int nthBit) {
        return ((value >> nthBit) & 1) != 0;
    }

    public static Position calculatePosition(int value) {
        final int xNthBit = 6;
        final int yNthBit = 7;

        return new Position(calculateForOneSide(value, xNthBit), calculateForOneSide(value, yNthBit));
    }

    private static int calculateForOneSide(int value, int nthBit) {
        int x1 = 0;
        int x2 = 16; // 16x16 max KMap; 4x4 variables
        boolean lastShiftToRight = false;

        while (x1 + 1 != x2) {
            if (isNthBitSet(value, nthBit) ^ lastShiftToRight) { // going to right
                x1 = x1 + ((x2 - x1) / 2);
                lastShiftToRight = true;
            } else { // going to left
                x2 = x2 - ((x2 - x1) / 2);
                lastShiftToRight = false;
            }
            nthBit -= 2;
        }
        return x2 - 1;
    }

    public static int findRightmostOnePosition(int value) {
        int shifts = 0;
        while (!isNthBitSet(value, 0)) {
            value >>= 1;
            shifts++;
        }
        return shifts;
    }
}
