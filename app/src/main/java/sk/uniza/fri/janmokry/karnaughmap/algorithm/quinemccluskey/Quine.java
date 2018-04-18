package sk.uniza.fri.janmokry.karnaughmap.algorithm.quinemccluskey;

import android.util.Log;

import java.lang.*;
import java.util.*;

import io.reactivex.disposables.Disposable;
import sk.uniza.fri.janmokry.karnaughmap.algorithm.quinemccluskey.util.NumberUtil;
import sk.uniza.fri.janmokry.karnaughmap.kmap.KMapCollection;

/**
 * Quine McCluskey table algorithm for finding configurations for karnaugh map.
 *
 * Created by Johny on 27.1.2017.
 */
public class Quine {

    public static final boolean LOG_DEBUG_INFO = false;
    private static final String TAG = Quine.class.getSimpleName();

    // STEP 1
    //  vypísať si všetky jednotky -> napr. 0100, 0000, 0101 ... A takisto vypísať aj X-ka
    //  rozdeliť do skupín podľa počtov jednotkových bitov
    //  Opakovať dookola: nájdem dvojice, ktoré sa líšia len v jednom bite - ten bit nahradím s '-' čím ho rozšírim. Tieto dvojice vymažem a pridám tento do danej skupiny ale až v ďalšom kroku ('-' == 1) Opakujem, dokedy už nemôžem nič spojiť. Duplicity nechceme
    //  zvyšné sú Prime Implicants -> napr. 0--0 je a'd'; 10-1 je ab'd

    // STEP 2
    //  Implicant Table - minterms vertical, prime implicants horizontal
    //  vyplním tabuľku tak aby prime implicants vyhovovali mintermom -> pokrýva jednotku, danú bunku zaznačím že tam je 1
    //  Minimalizácia loop:
    //      prvý krok: Eliminate essential columns - to je taký stĺpec, v ktorom jednotka je samá v riadku. Vyškrtneme riadky, v ktorých dané stĺpce majú jednotky; Tieto stĺpce (minterms) si poznačíme do výsledku; Redraw table
    //      druhý krok: Eliminate dominating rows - to je taký riadok, ktorý má jednotky ako iný riadok, ale možno o jednu viacej -> takých sa zbavíme (ak sú rovnaké, zbavíme sa jednej z nej)
    //      tretí krok: Eliminate dominated columns - to je taký stĺpec, ktorý má jednotky ako iný stĺpec, ale možno o jednu menej -> takých sa zbavíme (ak sú rovnaké, zbavíme sa jednej z nej)
    //      Ak v tomto cykle neeliminujem nič, zvolim si ľubovoľný prime implicant (stĺpec) a pridám ho do výsledku a tento stĺpec vymažem spolu s riadkami, vid krok 1
    //  end loop


    private Disposable mAlgorithmStopper;
    private final int mNumberOfInputVariables;

    public Quine(int numberOfInputVariables) {
        this.mNumberOfInputVariables = numberOfInputVariables;
    }

    public Solution compute(List<Number> minterms, List<Number> dontCares) {
        // STEP 1
        //  vypísať si všetky jednotky -> napr. 0100, 0000, 0101 ... A takisto vypísať aj X-ka
        //  rozdeliť do skupín podľa počtov jednotkových bitov
        final long startTime = System.currentTimeMillis();

        final ArrayList<ArrayList<Number>> groups = getNewGroups();
        for (Number number : minterms) { // sort minterms
            addToGroups(groups, number);
        }
        for (Number number : dontCares) { // sort dontCares
            addToGroups(groups, number);
        }

        if (LOG_DEBUG_INFO) {
            int counter = 0;
            for (ArrayList<Number> group : groups) {
                System.out.println("group " + counter++);
                for (Number number : group) {
                    System.out.println(number);
                }
            }
        }

        //  Opakovať dookola: nájdem dvojice, ktoré sa líšia len v jednom bite - ten bit nahradím s '-' čím ho rozšírim. Tieto dvojice vymažem a pridám tento do danej skupiny ale až v ďalšom kroku ('-' == 1) Opakujem, dokedy už nemôžem nič spojiť. Duplicity nechceme

        ArrayList<ArrayList<Number>> currentGroups;
        ArrayList<ArrayList<Number>> newGroups = groups;
        boolean merged;
        do {
            merged = false;
            currentGroups = newGroups;
            newGroups = getNewGroups();

            for (int groupIndex = 0; groupIndex < currentGroups.size() - 1; groupIndex++) { // iterating trough groups

                final ArrayList<Number> numbers = currentGroups.get(groupIndex);
                for (Number implicant : numbers) { // iterating through group

                    for (Number otherImplicant : currentGroups.get(groupIndex + 1)) {
                        if (implicant.areThisNumbersDifferentInJustOneBit(otherImplicant)) {

                            final Number mergedNumber = NumberUtil.merge(implicant, otherImplicant);
                            if (!newGroups.get(mergedNumber.getCountOfOnes()).contains(mergedNumber)) { // ignore duplicities
                                addToGroups(newGroups, mergedNumber);
                                merged = true;
                            }

                            if (LOG_DEBUG_INFO) {
                                System.out.println("Merged: " + implicant + " and " + otherImplicant);
                            }
                        }
                    }
                }
            }

            for (ArrayList<Number> group : currentGroups) {
                for (Number number : group) {
                    if (!number.isMerged()) {
                        addToGroups(newGroups, number);
                    }
                }
            }

            if (mAlgorithmStopper.isDisposed()) {
                logOnDisposeEvent(startTime, System.currentTimeMillis());

                final List<Number> nullList = null;
                return new Solution(nullList);
            }

        } while (merged);

        if (LOG_DEBUG_INFO) {
            int counter = 0;
            for (ArrayList<Number> group : currentGroups) {
                System.out.println("group " + counter++);
                for (Number number : group) {
                    System.out.println(number);
                }
            }
        }

        //  zvyšné sú Prime Implicants -> napr. 0--0 je a'd'; 10-1 je ab'd

        // STEP 2
        //  Implicant Table - minterms vertical, prime implicants horizontal

        final ArrayList<Number> minterms2ndPhase = new ArrayList<>(minterms); // vertical
        final ArrayList<Number> primeImplicants2ndPhase = new ArrayList<>(); // horizontal
        for (ArrayList<Number> group : currentGroups) { // addPrimeImplicants
            primeImplicants2ndPhase.addAll(group);
        }

        //  vyplním tabuľku tak aby prime implicants vyhovovali mintermom -> pokrýva jednotku, danú bunku zaznačím že tam je 1

        final ImplicantTable implicantTable = new ImplicantTable(minterms2ndPhase, primeImplicants2ndPhase);

        //  Minimalizácia loop:
        int tableLength;
        do {
            tableLength = implicantTable.getLength();
            //  prvý krok: Eliminate essential columns - to je taký stĺpec, v ktorom jednotka je samá v riadku. Vyškrtneme riadky, v ktorých dané stĺpce majú jednotky; Tieto stĺpce (minterms) si poznačíme do výsledku; Redraw table
            if (implicantTable.eliminateEssentialColumns()) {
                break;
            }
            //  druhý krok: Eliminate dominating rows - to je taký riadok, ktorý má jednotky ako iný riadok, ale možno o jednu viacej -> takých sa zbavíme (ak sú rovnaké, zbavíme sa jednej z nej)
            if (implicantTable.eliminateDominatingRows()) {
                break;
            }
            //  tretí krok: Eliminate dominated columns - to je taký stĺpec, ktorý má jednotky ako iný stĺpec, ale možno o jednu menej -> takých sa zbavíme (ak sú rovnaké, zbavíme sa jednej z nej)
            if (implicantTable.eliminateDominatedColumns()) {
                break;
            }
            //  Ak v tomto cykle neeliminujem nič, zvolim si ľubovoľný prime implicant (stĺpec) a pridám ho do výsledku a tento stĺpec vymažem spolu s riadkami, vid krok 1
            if (tableLength == implicantTable.getLength()) {
                implicantTable.chooseFirstColumnAsEssential();
            }

            if (mAlgorithmStopper.isDisposed()) {
                logOnDisposeEvent(startTime, System.currentTimeMillis());

                final List<Number> nullList = null;
                return new Solution(nullList);
            }

            //  end loop
        } while (true);

        final List<Number> solution = implicantTable.getSolution();

        final long endTime = System.currentTimeMillis();
        Log.i(TAG, "Quine McCluskey compute time: " + (endTime - startTime) + "ms for solution: " + solution);

        return new Solution(solution);
    }

    private void logOnDisposeEvent(long startTime, long endTime) {
        Log.i(TAG, "Quine McCluskey STOPPED as it has been disposed. Compute time: " + (endTime - startTime));
    }

    private void addToGroups(ArrayList<ArrayList<Number>> groups, Number number) {
        final int numberOfOnes = number.getCountOfOnes();
        groups.get(numberOfOnes).add(number);
    }

    private ArrayList<ArrayList<Number>> getNewGroups() {
        final ArrayList<ArrayList<Number>> groups = new ArrayList<>();
        for (int i = 0; i < mNumberOfInputVariables + 1; i++) { // init groups
            groups.add(new ArrayList<>());
        }
        return groups;
    }

    public Solution compute(KMapCollection collection, Disposable algorithmStopper) {
        mAlgorithmStopper = algorithmStopper;
        final KMapCollection.MinTermsAndDontCares minTermsAndDontCares = collection.getMinTermsAndDontCares();
        final Solution solution = compute(minTermsAndDontCares.minTerms, minTermsAndDontCares.dontCares);
        solution.setTitle(collection.getTitle());
        return solution;
    }
}
