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

    private transient final int NUMBER_OF_CELLS = 256;
    // as for now, we support Karnaugh Map up to 8 variables, i.e. 4x4, 16x16 dimensions respectively
    private final KMapCell[][] mCells = new KMapCell[16][16]; //columns, rows

    private transient ArrayList<KMapCell> mList = new ArrayList<>(NUMBER_OF_CELLS);

    private int mNumberOfVariables;

    public KMapCollection(int numberOfVariables) {
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

    private void addVariable() { // TODO
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
