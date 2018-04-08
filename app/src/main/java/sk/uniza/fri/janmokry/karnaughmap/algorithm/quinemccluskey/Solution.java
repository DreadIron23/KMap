package sk.uniza.fri.janmokry.karnaughmap.algorithm.quinemccluskey;

import java.util.List;

/**
 * Represents result of {@link Quine} algorithm.
 *
 * Created by Janci on 4.4.2018.
 */

public class Solution {

    private final List<Number> mSolution;
    private String mTitle;

    public Solution(List<Number> solution) {
        mSolution = solution;
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
        return !mSolution.isEmpty() && mSolution.get(0).size() != numberOfVariables;
    }
}
