package sk.uniza.fri.janmokry.karnaughmap.kmap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

import sk.uniza.fri.janmokry.karnaughmap.kmap.helper.Position;
import sk.uniza.fri.janmokry.karnaughmap.util.BitOperationUtil;

/**
 * Collection holding KMap cells and operations relation to them.
 */
public class KMapCollection implements Serializable {

    private static final int MAX_NUMBER_OF_VARIABLES = 8;
    private static final int MIN_NUMBER_OF_VARIABLES = 1;
    private static final int NUMBER_OF_CELLS = 256;
    // as for now, we support Karnaugh Map up to 8 variables, i.e. 4x4, 16x16 dimensions respectively
    private final KMapCell[][] mCells = new KMapCell[16][16]; //columns, rows

    private transient ArrayList<KMapCell> mList = new ArrayList<>(NUMBER_OF_CELLS); // TODO: intended for TruthTable linkage

    private int mNumberOfVariables;

    public KMapCollection(int numberOfVariables) {
        if (numberOfVariables > MAX_NUMBER_OF_VARIABLES) {
            throw new IllegalArgumentException("Max number of variables exceeded limit of: "
                    + MAX_NUMBER_OF_VARIABLES + " Provided: " + numberOfVariables);
        }
        mNumberOfVariables = numberOfVariables;

        for (int i = 0; i < NUMBER_OF_CELLS; i++) {
            final KMapCell cell = new KMapCell(i, 0);
            mList.add(cell);

            final Position position = BitOperationUtil.calculatePosition(i);
            mCells[position.x][position.y] = cell;
        }
    }

    public int getRowSize() {
        return 1 << getNumberOfRowVariables();
    }

    public int getColumnSize() {
        return 1 << getNumberOfColumnVariables();
    }

    public int getNumberOfRowVariables() {
        return mNumberOfVariables / 2;
    }

    public int getNumberOfColumnVariables() {
        return mNumberOfVariables / 2 + mNumberOfVariables % 2;
    }

    public KMapCell get(int x, int y) {
        return mCells[x][y];
    }

    public void incrementVariable() {
        // increment up to max limit of MAX_NUMBER_OF_VARIABLES
        mNumberOfVariables = Math.min(mNumberOfVariables + 1, MAX_NUMBER_OF_VARIABLES);
    }

    public void decrementVariable() {
        // decrement to min limit of MIN_NUMBER_OF_VARIABLES
        mNumberOfVariables = Math.max(mNumberOfVariables - 1, MIN_NUMBER_OF_VARIABLES);
    }

    public boolean isValidPosition(int column, int row) {
        return column >= 0 && row >= 0 && column < getColumnSize() && row < getRowSize();
    }

    /** Call this after GSON deserialization. It restores instance to correct state */
    public void afterGsonDeserialization() {
        mList = new ArrayList<>();
        for (KMapCell[] column : mCells) {
            for (KMapCell cell : column) {
                if (cell != null) {
                    mList.add(cell);
                }
            }
        }
        Collections.sort(mList, (first, second) -> first.getValue() - second.getValue());
    }
}
