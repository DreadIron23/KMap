package sk.uniza.fri.janmokry.karnaughmap.util;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import sk.uniza.fri.janmokry.karnaughmap.data.GsonService;
import sk.uniza.fri.janmokry.karnaughmap.data.ProjectInfo;
import sk.uniza.fri.janmokry.karnaughmap.data.ProjectKMap;
import sk.uniza.fri.janmokry.karnaughmap.kmap.KMapCollection;

/**
 * Util for data conversions.
 *
 * Created by Janci on 2.4.2018.
 */

public class ConversionUtil {

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
}
