package sk.uniza.fri.janmokry.karnaughmap.kmap;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sk.uniza.fri.janmokry.karnaughmap.algorithm.quinemccluskey.Number;
import sk.uniza.fri.janmokry.karnaughmap.algorithm.quinemccluskey.Solution;
import sk.uniza.fri.janmokry.karnaughmap.algorithm.quinemccluskey.util.NumberUtil;
import sk.uniza.fri.janmokry.karnaughmap.data.ColorPaletteService;
import sk.uniza.fri.janmokry.karnaughmap.kmap.helper.Position;
import sk.uniza.fri.janmokry.karnaughmap.util.BitOperationUtil;
import sk.uniza.fri.janmokry.karnaughmap.util.SL;

import static sk.uniza.fri.janmokry.karnaughmap.util.MathUtil.floorMod;

/**
 * Collection holding KMap cells and operations related to them. Data holder for KMap.
 * This collection is shared with TruthTable.
 */
public class KMapCollection implements Serializable {

    /** Intended for tap(when bit value changes) change awareness between KMapView and TruthTableView */
    public interface OnTapListener {

        void onTap(KMapCell cell);
    }

    public static class MinTermsAndDontCares {

        public final List<Number> minTerms;
        public final List<Number> dontCares;

        public MinTermsAndDontCares(int numberOfVariables, List<Integer> minTerms, List<Integer> dontCares) {
            this.minTerms = NumberUtil.wrap(numberOfVariables, minTerms);
            this.dontCares = NumberUtil.wrap(numberOfVariables, dontCares);
        }
    }

    private static final int MAX_NUMBER_OF_VARIABLES = 8;
    private static final int MIN_NUMBER_OF_VARIABLES = 1;
    private static final int NUMBER_OF_CELLS = 256;
    // as for now, we support Karnaugh Map up to 8 variables, i.e. 4x4, 16x16 dimensions respectively
    private final KMapCell[][] mCells = new KMapCell[16][16]; //columns, rows

    private Solution mSolution;

    private transient ArrayList<KMapCell> mCellList = new ArrayList<>(NUMBER_OF_CELLS);

    private int mNumberOfVariables;
    private String mTitle;

    private transient List<OnTapListener> mOnTapListeners = new ArrayList<>();

    private transient OnTapListener mOnTapListener = createOnTapListener();


    public KMapCollection(int numberOfVariables) {
        if (numberOfVariables > MAX_NUMBER_OF_VARIABLES) {
            throw new IllegalArgumentException("Max number of variables exceeded limit of: "
                    + MAX_NUMBER_OF_VARIABLES + " Provided: " + numberOfVariables);
        }
        mNumberOfVariables = numberOfVariables;

        for (int i = 0; i < NUMBER_OF_CELLS; i++) {
            final KMapCell cell = new KMapCell(i, 0);
            cell.setOnTapListener(mOnTapListener);
            mCellList.add(cell);

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

    public int getSize() {
        return 1 << mNumberOfVariables;
    }

    public int getNumberOfRowVariables() {
        return mNumberOfVariables / 2;
    }

    public int getNumberOfColumnVariables() {
        return mNumberOfVariables / 2 + mNumberOfVariables % 2;
    }

    public int getNumberOfVariables() {
        return mNumberOfVariables;
    }

    public KMapCell get(int x, int y) {
        return mCells[x][y];
    }

    public KMapCell get(int listIndex) {
        return mCellList.get(listIndex);
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
        mOnTapListeners = new ArrayList<>();
        mOnTapListener = createOnTapListener();

        mCellList = new ArrayList<>();
        for (KMapCell[] column : mCells) {
            for (KMapCell cell : column) {
                if (cell != null) {
                    cell.setOnTapListener(mOnTapListener);
                    mCellList.add(cell);
                }
            }
        }
        Collections.sort(mCellList, (first, second) -> first.getValue() - second.getValue());
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setSolution(@NonNull Solution solution) {
        mSolution = solution;
        prepareCellsForConfigurationDrawing();
    }

    public void registerOnTapListener(@NonNull OnTapListener onTapListener) {
        mOnTapListeners.add(onTapListener);
    }

    public void unregisterOnTapListener(@NonNull OnTapListener onTapListener) {
        mOnTapListeners.remove(onTapListener);
    }

    public ArrayList<KMapCell> getKMapCellList() {
        return mCellList;
    }

    public MinTermsAndDontCares getMinTermsAndDontCares() {
        final List<Integer> minTerms = new ArrayList<>();
        final List<Integer> dontCares = new ArrayList<>();
        for (int columnIndex = 0; columnIndex < getColumnSize(); columnIndex++) {
            for (int rowIndex = 0; rowIndex < getRowSize(); rowIndex++) {
                final KMapCell cell = mCells[columnIndex][rowIndex];
                switch (cell.getBitRepresentation()) {
                    case KMapCell.VALUE_1: {
                        minTerms.add(cell.getValue());
                        break;
                    }
                    case KMapCell.VALUE_X: {
                        dontCares.add(cell.getValue());
                        break;
                    }
                }
            }
        }

        return new MinTermsAndDontCares(mNumberOfVariables, minTerms, dontCares);
    }

    private void prepareCellsForConfigurationDrawing() {
        if (mSolution.isObsolete(mNumberOfVariables)) {
            return;
        }
        for (KMapCell cell : mCellList) {
            cell.reset();
        }

        final int[] colorPalette = SL.get(ColorPaletteService.class).providePaletteFor(mSolution.getSolution().size());

        int paletteIndex = 0;
        for (Number configuration : mSolution.getSolution()) {
            final int color = colorPalette[paletteIndex++ % (colorPalette.length - 1)];
            boolean[][] kMapConfiguration = new boolean[getColumnSize()][getRowSize()];
            final List<Integer> allCoveredMinTerms = configuration.getAllCoveredMinTerms();
            final ArrayList<Position> positionBuffer = new ArrayList<>();

            for (Integer coveredMinTerm : allCoveredMinTerms) { // set kMapConfiguration
                final Position position = BitOperationUtil.calculatePosition(coveredMinTerm);
                kMapConfiguration[position.x][position.y] = true;
                positionBuffer.add(position);
            }

            // calculate graphics for mCells
            for (Position position : positionBuffer) {
                // check top, right, bottom, left neighbours
                final int xSize = kMapConfiguration.length;
                final int ySize = kMapConfiguration[0].length;

                // checks: if its size 1 then its a point; if its boundary item -> if so we check if its set across all column/row; check relevant neighbour
                final boolean left = xSize > 1 && !(position.x == 0 && isWholeRowSet(kMapConfiguration, position.y)) &&
                        kMapConfiguration[floorMod(position.x - 1, xSize)][position.y];
                final boolean top = ySize > 1 && !(position.y == 0 && isWholeColumnSet(kMapConfiguration[position.x])) &&
                        kMapConfiguration[position.x][floorMod(position.y - 1, ySize)];
                final boolean right = xSize > 1 && !(position.x == xSize - 1 && isWholeRowSet(kMapConfiguration, position.y)) &&
                        kMapConfiguration[floorMod(position.x + 1, xSize)][position.y];
                final boolean bottom = ySize > 1 && !(position.y == ySize - 1 && isWholeColumnSet(kMapConfiguration[position.x])) &&
                        kMapConfiguration[position.x][floorMod(position.y + 1, ySize)];

                final ConfigurationShapes shape = ConfigurationShapes.getType(left, top, right, bottom);
                mCells[position.x][position.y].addShape(new KMapCell.ConfigurationShape(shape, color));
            }
        }
    }

    @NonNull
    private OnTapListener createOnTapListener() {
        return cell -> {
            for (OnTapListener listener : mOnTapListeners) {
                listener.onTap(cell);
            }
        };
    }

    private boolean isWholeRowSet(boolean[][] kMapConfiguration, int nthRow) {
        for (int index = 0; index < kMapConfiguration.length; index++) {
            if (!kMapConfiguration[index][nthRow]) return false;
        }
        return true;
    }

    private boolean isWholeColumnSet(boolean[] kMapConfigurationLine) {
        for (boolean value : kMapConfigurationLine) {
            if (!value) return false;
        }
        return true;
    }
}
