package sk.uniza.fri.janmokry.karnaughmap.algorithm.quinemccluskey;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import sk.uniza.fri.janmokry.karnaughmap.util.BitOperationUtil;


/**
 * Represents minterm or later as part of {@link Solution} configuration.
 *
 * Created by Johny on 28.1.2017.
 */
public class Number implements Serializable {

    public static final Integer COMBINED_VALUE = 2;

    /** Factory method. Sets all values to COMBINED_VALUE */
    public static Number getBlankNumber(int numberOfVariables) {
        final ArrayList<Integer> blankNumber = new ArrayList<>(numberOfVariables);
        for (int index = 0; index < numberOfVariables; index++) {
            blankNumber.add(COMBINED_VALUE);
        }
        return new Number(blankNumber);
    }

    public ArrayList<Integer> mNumber = new ArrayList<>();

    private boolean mMerged = false;

    public Number(ArrayList<Integer> mNumber) {
        this.mNumber.addAll(mNumber);
    }

    public Number(int numberOfInputVariables, int number) {
        final StringBuilder sb = new StringBuilder(numberOfInputVariables);

        final String binaryNumber = Integer.toBinaryString(number);
        for (int i = binaryNumber.length(); i < numberOfInputVariables ; i++) {
            sb.append("0");
        }
        sb.append(binaryNumber);
        final String[] binary = sb.toString().split("");
        for (String bit : binary) {
            if (!bit.isEmpty()) {
                mNumber.add(Integer.valueOf(bit));
            }
        }
    }

    public int size() {
        return mNumber.size();
    }

    public int getCountOfOnes() {
        int counter = 0;
        for (Integer bit : mNumber) {
            if (bit.equals(1) || bit.equals(COMBINED_VALUE)) {
                counter++;
            }
        }
        return counter;
    }

    public boolean areThisNumbersDifferentInJustOneBit(Number other) {
        int difference = 0;
        for (int i = 0; i < mNumber.size(); i++) {
            if (!mNumber.get(i).equals(other.mNumber.get(i))) {
                difference++;
            }
        }
        return difference == 1;
    }

    public void flagMerged() {
        mMerged = true;
    }

    public boolean isMerged() {
        return mMerged;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Number number = (Number) o;

        return mNumber.equals(number.mNumber);
    }

    @Override
    public int hashCode() {
        return mNumber.hashCode();
    }

    @Override
    public String toString() {
        return mNumber.toString();
    }

    public boolean isCoveringWholeMap() {
        for (Integer bit : mNumber) {
            if (!bit.equals(COMBINED_VALUE)) {
                return false;
            }
        }
        return true;
    }

    public void coverWholeMap() {
        for (int index = 0; index < mNumber.size(); index++) {
            mNumber.set(index, COMBINED_VALUE);
        }
    }

    /** Returns all minTerms which are covered in this configuration (in Solution) */
    public List<Integer> getAllCoveredMinTerms() {
        final ArrayList<Integer> minTerms = new ArrayList<>();
        final int combinedBits = getNumberOfCombinedBits(mNumber);

        for (int minTermIndex = 0; minTermIndex < (1 << combinedBits); minTermIndex++) { // this many minTerms we have in this configuration
            int minTermValue = 0; // building up value according to bits
            int combinedValueShiftCounter = 0; // counting how many combined bits we processed in mNumber.size()
            for (int index = mNumber.size() - 1; index >= 0 ; index--) { // iterating through bits from right to left
                final Integer bitValue = mNumber.get(index);
                int bitValueIndex =  mNumber.size() - (index + 1); // bit value ...16 8 4 2 1 <=
                if (bitValue.equals(COMBINED_VALUE)) {
                    minTermValue += (BitOperationUtil.isNthBitSet(minTermIndex, combinedValueShiftCounter++) ? 1 : 0) * (1 << bitValueIndex);
                } else {
                    minTermValue += bitValue * (1 << bitValueIndex);
                }
            }
            minTerms.add(minTermValue);
        }

        return minTerms;
    }

    private int getNumberOfCombinedBits(ArrayList<Integer> minTerms) {
        int counter = 0;
        for (Integer bit : minTerms) {
            if (bit.equals(COMBINED_VALUE)) {
                counter++;
            }
        }
        return counter;
    }

    public int getXthBit(int index) {
        return mNumber.get(mNumber.size() - 1 - index);
    }

    public void setXthBit(int index, @NonNull Integer value) {
        mNumber.set(mNumber.size() - 1 - index, value);
    }
}
