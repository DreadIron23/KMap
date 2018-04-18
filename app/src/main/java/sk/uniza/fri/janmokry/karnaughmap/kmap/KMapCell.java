package sk.uniza.fri.janmokry.karnaughmap.kmap;

import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import sk.uniza.fri.janmokry.karnaughmap.util.GraphicsUtil;

/**
 * Created by Janci on 29.3.2017.
 */
public class KMapCell {

    public static class ConfigurationShape implements Serializable {

        public final ConfigurationShapes shape;
        public final @ColorInt int color;

        public ConfigurationShape(@NonNull ConfigurationShapes shape, @ColorInt int color) {
            this.shape = shape;
            final int alpha70percent = 178;
            this.color = GraphicsUtil.setAlpha(color, alpha70percent);
        }
    }

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

    private final List<ConfigurationShape> mShapes = new ArrayList<>();

    @Nullable
    private transient KMapCollection.OnTapListener mOnTapListener;

    public KMapCell(int value, int bitRepresentation) {
        this.mValue = value;
        this.mBitRepresentation = bitRepresentation;
    }

    public void tapped() {
        changeToNextBit();

        if (mOnTapListener != null) {
            mOnTapListener.onTap(this);
        }
    }

    public void setOnTapListener(@NonNull KMapCollection.OnTapListener listener) {
        mOnTapListener = listener;
    }

    public int getValue() {
        return mValue;
    }

    public int getBitRepresentation() {
        return mBitRepresentation;
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

    public void addShape(ConfigurationShape shape) {
        mShapes.add(shape);
    }

    public List<ConfigurationShape> getShapes() {
        return mShapes;
    }

    public void clearShapes() {
        mShapes.clear();
    }

    /** Resets shapes and sets bit to VALUE_0 */
    public void reset() {
        clearShapes();
        mBitRepresentation = VALUE_0;
    }

    public void setBit(int bit) {
        mBitRepresentation = bit;
    }

    private void changeToNextBit() {
        if (mBitRepresentation == VALUE_1) {
            mBitRepresentation = VALUE_X;
        } else {
            mBitRepresentation++;
        }
    }
}
