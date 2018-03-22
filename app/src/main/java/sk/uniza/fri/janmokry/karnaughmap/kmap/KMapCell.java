package sk.uniza.fri.janmokry.karnaughmap.kmap;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Janci on 29.3.2017.
 */
public class KMapCell {

    public static final int VALUE_0 = 0;
    public static final int VALUE_1 = 1;
    public static final int VALUE_X = -1;
    public static final String VALUE_0_STRING = "0";
    public static final String VALUE_1_STRING = "1";
    public static final String VALUE_X_STRING = "X";

    @SerializedName("val")
    private int mValue;

    @SerializedName("bit")
    private int mBitRepresentation;

    public KMapCell(int value, int bitRepresentation) {
        this.mValue = value;
        this.mBitRepresentation = bitRepresentation;
    }

    public void tapped() {
        changeToNextBit();
    }

    @Override
    public String toString() {
        switch(mBitRepresentation) {
            case VALUE_0:
                return  VALUE_0_STRING;
            case VALUE_1:
                return  VALUE_1_STRING;
            case VALUE_X:
                return  VALUE_X_STRING;
            default:
                throw new IllegalStateException("Unsupported value! " + mBitRepresentation);
        }
    }

    private void changeToNextBit() {
        if (mBitRepresentation == VALUE_1) {
            mBitRepresentation = VALUE_X;
        } else {
            mBitRepresentation++;
        }
    }
}
