package sk.uniza.fri.janmokry.karnaughmap.algorithm.quinemccluskey;

import java.util.ArrayList;
import java.util.List;

import static sk.uniza.fri.janmokry.karnaughmap.algorithm.quinemccluskey.Quine.LOG_DEBUG_INFO;

/**
 * Implicant table which is part of {@link Quine} algorithm.
 *
 * Created by Johny on 30.1.2017.
 */
public class ImplicantTable {

    private ArrayList<Number> mMinterms;
    private ArrayList<Number> mPrimeImplicants;
    private int[][] mImplicantTable;
    private List<Number> mSolution = new ArrayList<>();

    public ImplicantTable(ArrayList<Number> minterms, ArrayList<Number> primeImplicants) {
        mMinterms = minterms;
        mPrimeImplicants = primeImplicants;

        reconstructTable();
    }

    public int getLength() {
        return mMinterms.size() + mPrimeImplicants.size();
    }

    /**
     *
     * @return true if table is empty and therefore solution is ready, false otherwise
     */
    public boolean eliminateEssentialColumns() {
        //  prvý krok: Eliminate essential columns - to je taký stĺpec, v ktorom jednotka je samá v riadku. Vyškrtneme riadky, v ktorých dané stĺpce majú jednotky; Tieto stĺpce (minterms) si poznačíme do výsledku; Redraw table
        final List<Number> eliminatedPrimeImplicants = new ArrayList<>();
        final List<Number> eliminatedMinterms = new ArrayList<>();

        for (int row = 0; row < mImplicantTable.length; row++) {
            int numberOfOnes = 0;
            int foundPrimeImplicantIndex = -1;
            for (int column = 0; column < mImplicantTable[0].length; column++) {
                if (mImplicantTable[row][column] == 1 ) {
                    numberOfOnes++;
                    foundPrimeImplicantIndex = column;
                }
            }
            if (numberOfOnes == 1) {
                // eliminate column
                final Number foundPrimeImplicant = mPrimeImplicants.get(foundPrimeImplicantIndex);
                if (eliminatedPrimeImplicants.contains(foundPrimeImplicant)) {
                    continue;
                }
                eliminatedPrimeImplicants.add(foundPrimeImplicant);
                if (LOG_DEBUG_INFO) {
                    System.out.println("Adding to solution: " + foundPrimeImplicant);
                }
                mSolution.add(foundPrimeImplicant);

                // eliminate rows
                for (int rowIndex = 0; rowIndex < mImplicantTable.length; rowIndex++) {
                    if (mImplicantTable[rowIndex][foundPrimeImplicantIndex] == 1) {
                        eliminatedMinterms.add(mMinterms.get(rowIndex));
                    }
                }
            }
        }

        mPrimeImplicants.removeAll(eliminatedPrimeImplicants);
        mMinterms.removeAll(eliminatedMinterms);

        if (mMinterms.isEmpty() || mPrimeImplicants.isEmpty()) {
            return true;
        } else {
            if (!eliminatedPrimeImplicants.isEmpty()) {
                reconstructTable();
            }
            return false;
        }
    }

    public boolean eliminateDominatingRows() {
        //  druhý krok: Eliminate dominating rows - to je taký riadok, ktorý má jednotky ako iný riadok, ale možno o jednu viacej -> takých sa zbavíme (ak sú rovnaké, zbavíme sa jednej z nej)
        final boolean[] eliminatedRows = new boolean[mMinterms.size()];
        final ArrayList<Number> eliminatedMinterms = new ArrayList<>();

        for (int outerRow = 0; outerRow < mImplicantTable.length; outerRow++) { // checking for this row
            CheckingRow:
            for (int innerRow = 0; innerRow < mImplicantTable.length; innerRow++) { // checking against this row
                if (innerRow == outerRow) {
                    continue;
                }
                for (int column = 0; column < mImplicantTable[0].length; column++) { // iterating through columns
                    if (eliminatedRows[innerRow] || mImplicantTable[outerRow][column] == 0 && mImplicantTable[innerRow][column] == 1) { // fail
                        continue CheckingRow;
                    }
                }
                eliminatedRows[outerRow] = true;
                eliminatedMinterms.add(mMinterms.get(outerRow));
            }
        }

        mMinterms.removeAll(eliminatedMinterms);

        if (mMinterms.isEmpty() || mPrimeImplicants.isEmpty()) {
            return true;
        } else {
            if (!eliminatedMinterms.isEmpty()) {
                reconstructTable();
            }
            return false;
        }
    }

    public boolean eliminateDominatedColumns() {
        //  tretí krok: Eliminate dominated columns - to je taký stĺpec, ktorý má jednotky ako iný stĺpec, ale možno o jednu menej -> takých sa zbavíme (ak sú rovnaké, zbavíme sa jednej z nej)
        final boolean[] eliminatedColumns = new boolean[mPrimeImplicants.size()];
        final ArrayList<Number> eliminatedPrimeImplicants = new ArrayList<>();

        for (int outerColumn = 0; outerColumn < mImplicantTable[0].length; outerColumn++) { // checking for this column
            CheckingColumn:
            for (int innerColumn = 0; innerColumn < mImplicantTable[0].length; innerColumn++) { // checking against this column
                if (innerColumn == outerColumn) {
                    continue;
                }
                for (int row = 0; row < mImplicantTable.length; row++) { // iterating through rows
                    if (eliminatedColumns[innerColumn] || eliminatedColumns[outerColumn] || mImplicantTable[row][outerColumn] == 0 && mImplicantTable[row][innerColumn] == 1) { // fail
                        continue CheckingColumn;
                    }
                }
                eliminatedColumns[innerColumn] = true;
                eliminatedPrimeImplicants.add(mPrimeImplicants.get(innerColumn));
                if (LOG_DEBUG_INFO) {
                    System.out.println("Eliminating columns: " + outerColumn + " >" + innerColumn + "<");
                }
            }
        }

        mPrimeImplicants.removeAll(eliminatedPrimeImplicants);

        if (mMinterms.isEmpty() || mPrimeImplicants.isEmpty()) {
            return true;
        } else {
            if (!eliminatedPrimeImplicants.isEmpty()) {
                reconstructTable();
            }
            return false;
        }
    }

    public void chooseFirstColumnAsEssential() {
        final List<Number> eliminatedMinterms = new ArrayList<>();

        // eliminate column
        final Number foundPrimeImplicant = mPrimeImplicants.get(0);
        mSolution.add(foundPrimeImplicant);

        // eliminate rows
        for (int rowIndex = 0; rowIndex < mImplicantTable.length; rowIndex++) {
            if (mImplicantTable[rowIndex][0] == 1) {
                eliminatedMinterms.add(mMinterms.get(rowIndex));
            }
        }

        mPrimeImplicants.remove(0);
        mMinterms.removeAll(eliminatedMinterms);

        reconstructTable();
    }

    private void reconstructTable() {
        mImplicantTable = new int[mMinterms.size()][mPrimeImplicants.size()];

        if (LOG_DEBUG_INFO) {
            System.out.println();
            System.out.print("            ");
            for (Number primeImplicant : mPrimeImplicants) {
                System.out.print(primeImplicant);
            }
            System.out.println();
        }

        for (int mintermIndex = 0; mintermIndex < mMinterms.size(); mintermIndex++) {
            final Number minterm = mMinterms.get(mintermIndex);
            if (LOG_DEBUG_INFO) {
                System.out.print(minterm);
            }
            for (int primeImplicantIndex = 0; primeImplicantIndex < mPrimeImplicants.size(); primeImplicantIndex++) {
                final Number primeImplicant = mPrimeImplicants.get(primeImplicantIndex);

                if (isMintermSatisfied(minterm, primeImplicant)) {
                    mImplicantTable[mintermIndex][primeImplicantIndex] = 1;
                } else {
                    mImplicantTable[mintermIndex][primeImplicantIndex] = 0;
                }
                if (LOG_DEBUG_INFO) {
                    System.out.print("     " + mImplicantTable[mintermIndex][primeImplicantIndex] + "      ");
                }
            }
            if (LOG_DEBUG_INFO) {
                System.out.println();
            }
        }
    }

    /**
     *
     * @param minterm eg 0010
     * @param primeImplicant eg 0220 where 2 represent either 1 or 0
     */
    private boolean isMintermSatisfied(Number minterm, Number primeImplicant) {
        final ArrayList<Integer> mintermNum = minterm.mNumber;
        final ArrayList<Integer> primeImplicantNum = primeImplicant.mNumber;
        for (int i = 0; i < mintermNum.size(); i++) {
            if (!mintermNum.get(i).equals(primeImplicantNum.get(i)) && !primeImplicantNum.get(i).equals(Number.COMBINED_VALUE)) {
                return false;
            }
        }
        return true;
    }

    public List<Number> getSolution() {
        return mSolution;
    }
}
