package sk.uniza.fri.janmokry.karnaughmap.helper;

import android.support.annotation.NonNull;

import java.util.ArrayList;

import sk.uniza.fri.janmokry.karnaughmap.algorithm.quinemccluskey.Number;

/**
 * Created by Janci on 17.4.2018.
 */

public class ImplicantEditor {

    private Number mOriginalImplicant;
    private Number mBuildingImplicant;
    private boolean mIsSetToZero = false;
    private boolean mIsSetToOne = false;

    public ImplicantEditor(@NonNull Number originalImplicant) {
        this.mOriginalImplicant = originalImplicant;
        mBuildingImplicant = new Number(originalImplicant.mNumber);
    }

    public int size() {
        return mBuildingImplicant.size();
    }

    public int getXthBit(int index) {
        return mBuildingImplicant.getXthBit(index);
    }

    public void setXthBit(int index, @NonNull Integer value) {
        mIsSetToZero = false;
        mIsSetToOne = false;
        mBuildingImplicant.setXthBit(index, value);
    }

    public ArrayList<Integer> values() {
        return mBuildingImplicant.mNumber;
    }

    public boolean isBlank() {
        return mBuildingImplicant.isCoveringWholeMap() && !mIsSetToOne && !mIsSetToZero;
    }

    public boolean isCoveringWholeMap() {
        return mBuildingImplicant.isCoveringWholeMap();
    }

    public void setToOne() {
        mIsSetToZero = false;
        mIsSetToOne = true;
        mBuildingImplicant.coverWholeMap();
    }

    public void setToZero() {
        mIsSetToZero = true;
        mIsSetToOne = false;
        mBuildingImplicant.coverWholeMap();
    }

    public boolean isSetToOne() {
        return mIsSetToOne;
    }

    public boolean isSetToZero() {
        return mIsSetToZero;
    }
}
