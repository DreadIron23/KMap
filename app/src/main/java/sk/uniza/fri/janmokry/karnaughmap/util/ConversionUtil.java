package sk.uniza.fri.janmokry.karnaughmap.util;

import android.support.annotation.NonNull;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import sk.uniza.fri.janmokry.karnaughmap.algorithm.quinemccluskey.Number;
import sk.uniza.fri.janmokry.karnaughmap.algorithm.quinemccluskey.Solution;
import sk.uniza.fri.janmokry.karnaughmap.data.GsonService;
import sk.uniza.fri.janmokry.karnaughmap.data.ProjectInfo;
import sk.uniza.fri.janmokry.karnaughmap.data.ProjectKMap;
import sk.uniza.fri.janmokry.karnaughmap.kmap.KMapCell;
import sk.uniza.fri.janmokry.karnaughmap.kmap.KMapCollection;
import sk.uniza.fri.janmokry.karnaughmap.kmap.TruthTableCollection;

import static sk.uniza.fri.janmokry.karnaughmap.kmap.KMapCell.VALUE_0_STRING;
import static sk.uniza.fri.janmokry.karnaughmap.kmap.KMapCell.VALUE_1_STRING;

/**
 * Util for data conversions.
 *
 * Created by Janci on 2.4.2018.
 */

public class ConversionUtil {

    private static final String VARIABLE_BASE_NAME = "x";
    private static final String DIVIDER = ",";
    private static final String SIGN_NEGATE = "~";
    private static final String SIGN_PLUS = " + ";
    private static final String SIGN_EQUALS = " = ";

    public static List<ProjectKMap> transformToProjectKMaps(ProjectInfo projectInfo, ArrayList<KMapCollection> kMapCollections) {
        final Gson gson = SL.get(GsonService.class).provide();

        List<ProjectKMap> listToReturn = new ArrayList<>();
        for (KMapCollection kMapCollection : kMapCollections) {
            listToReturn.add(new ProjectKMap(projectInfo.id, kMapCollection.getTitle(), gson.toJson(kMapCollection)));
        }
        return listToReturn;
    }

    public static List<KMapCollection> transformToKMapCollections(List<ProjectKMap> projectKMaps) {
        final Gson gson = SL.get(GsonService.class).provide();

        final ArrayList<KMapCollection> kMapCollections = new ArrayList<>();
        for (ProjectKMap projectKMap : projectKMaps) {
            final KMapCollection kMapCollection = gson.fromJson(projectKMap.gsonData, KMapCollection.class);
            kMapCollection.afterGsonDeserialization();
            kMapCollections.add(kMapCollection);
        }
        return kMapCollections;
    }

    @NonNull
    public static String buildStringForShare(@NonNull TruthTableCollection truthTableCollection) {
        final StringBuilder builder = new StringBuilder();
        final List<KMapCollection> kMapCollections = truthTableCollection.getKMapCollections();

        final int maximumKMapSize = truthTableCollection.getMaximumKMapSize();
        final int maximumVariables = truthTableCollection.getMaximumVariables();
        final int numberOfMaps = truthTableCollection.getNumberOfMaps();

        for (int index = maximumVariables - 1; index >= 0; index--) { // value labels
            builder.append(VARIABLE_BASE_NAME).append(index)
                    .append(DIVIDER);
        }
        builder.append(DIVIDER);
        for (int index = 0; index < numberOfMaps; index++) { // map labels
            builder.append(kMapCollections.get(index).getTitle())
                    .append(DIVIDER);
        }
        builder.delete(builder.length() - 1, builder.length());
        builder.append("\n");

        for (int yIndex = 0; yIndex < maximumKMapSize; yIndex++) {
            int bitCounter = maximumVariables - 1;
            for (int xIndex = maximumVariables - 1; xIndex >= 0; xIndex--) { // value bits
                builder.append(BitOperationUtil.isNthBitSet(yIndex, bitCounter--) ? VALUE_1_STRING : VALUE_0_STRING)
                        .append(DIVIDER);
            }
            builder.append(DIVIDER);
            for (KMapCollection collection : kMapCollections) { // map bits
                builder.append(yIndex < collection.getSize() ? collection.getKMapCellList().get(yIndex).toString() : "-")
                        .append(DIVIDER);
            }
            builder.delete(builder.length() - 1, builder.length());
            builder.append("\n");
        }
        builder.append("\n");


        for (KMapCollection collection : kMapCollections) { // logic expressions
            builder.append(collection.getTitle())
                    .append(SIGN_EQUALS);
            final List<Number> configurations = collection.getSolution().getSolution();
            int configurationCounter = 0;
            for (Number configuration : configurations) {
                final ArrayList<Integer> numbers = configuration.mNumber;
                int bitCounter = 0;
                for (int bitIndex = numbers.size() - 1; bitIndex >= 0; bitIndex--) {
                    final Integer bit = numbers.get(bitIndex);
                    if (bit == KMapCell.VALUE_1 || bit == KMapCell.VALUE_0) {
                        if (bit == KMapCell.VALUE_0) {
                            builder.append(SIGN_NEGATE);
                        }
                        builder.append(VARIABLE_BASE_NAME).append(String.valueOf(bitCounter));
                    }
                    bitCounter++;
                }
                if (++configurationCounter != configurations.size()) { // if it's not last add plus sign
                    builder.append(SIGN_PLUS);
                }
            }
            if (configurations.size() == 1 && configurations.get(0).isCoveringWholeMap()) {
                builder.append(String.valueOf(Solution.RESULT_1));
            }
            if (collection.getSolution().isEmpty()) {
                builder.append(String.valueOf(Solution.RESULT_0));
            }
            builder.append("\n");
        }
        return builder.toString();
    }
}
