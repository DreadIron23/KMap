package sk.uniza.fri.janmokry.karnaughmap.algorithm.quinemccluskey.util;

import java.util.ArrayList;
import java.util.List;

import sk.uniza.fri.janmokry.karnaughmap.algorithm.quinemccluskey.Number;

import static sk.uniza.fri.janmokry.karnaughmap.algorithm.quinemccluskey.Number.COMBINED_VALUE;

/**
 * Util functions related to {@link Number}.
 *
 * Created by Johny on 28.1.2017.
 */
public class NumberUtil {

    public static List<Number> wrap(int numberOfInputVariables, List<Integer> numbers) {
        final ArrayList<Number> list = new ArrayList<>();
        for (int number : numbers) {
            list.add(new Number(numberOfInputVariables, number));
        }
        return list;
    }

    /**
     * Merge two implicants resulting into new merged implicant. Two entry implicants are flagged as merged in process.
     */
    public static Number merge(Number firstImplicant, Number secondImplicant) {
        final ArrayList<Integer> number = new ArrayList<>();
        for (int i = 0; i < firstImplicant.size(); i++) {
            if (!firstImplicant.mNumber.get(i).equals(secondImplicant.mNumber.get(i))) {
                number.add(COMBINED_VALUE);
            }
            else {
                number.add(firstImplicant.mNumber.get(i));
            }
        }
        firstImplicant.flagMerged();
        secondImplicant.flagMerged();
        return new Number(number);
    }
}
