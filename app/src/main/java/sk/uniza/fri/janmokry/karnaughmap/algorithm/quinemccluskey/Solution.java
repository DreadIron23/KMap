package sk.uniza.fri.janmokry.karnaughmap.algorithm.quinemccluskey;

import android.support.annotation.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents result of {@link Quine} algorithm.
 *
 * Created by Janci on 4.4.2018.
 */

public class Solution implements Serializable {

    public final static int RESULT_1 = 1; // eg. y1 = 1
    public final static int RESULT_0 = 0; // eg. y1 = 0

    private final List<Number> mSolution;
    private String mTitle;

    public Solution(String title) {
        mSolution = new ArrayList<>();
        mTitle = title;
    }

    /** @param solution null in case of Quine execution has been terminated */
    public Solution(@Nullable List<Number> solution) {
        mSolution = solution;
    }

    public Solution(@Nullable List<Number> solution, String title) {
        mSolution = solution;
        mTitle = title;
    }

    public List<Number> getSolution() {
        return mSolution;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public String getTitle() {
        return mTitle;
    }

    /** Checked when returned from Quine solver and then this instance is discarded */
    public boolean isDisposed() {
        return mSolution == null;
    }

    /** Check if Solution is still compatible with KMapCollection. This is needed to check in very
     * specific scenario when user changes map dimension(to smaller), Quine is kicked off. Now, user
     * rotates the screen, maps are saved and loaded on fragment recreation with already changed
     * dimension. New recreated maps subscribe to solutionLiveData, which still have the old obsolete
     * Solution (updated Solution is still in process of solving), which gets and KMapCollection is
     * trying to create configurations which result in ArrayIndexOutOfBoundsException */
    public boolean isObsolete(int numberOfVariables) {
        if (mSolution == null) solutionNullCheckFailed();
        return !mSolution.isEmpty() && mSolution.get(0).size() != numberOfVariables;
    }

    public boolean isEmpty() {
        if (mSolution == null) solutionNullCheckFailed();
        return mSolution.isEmpty();
    }

    public boolean equalsValues(Solution solution) {
        // noinspection ConstantConditions
        return mSolution.equals(solution.mSolution);
    }

    private void solutionNullCheckFailed() {
        throw new IllegalStateException("Solution should't be null except when is disposed from Quine algorithm");
    }
}
