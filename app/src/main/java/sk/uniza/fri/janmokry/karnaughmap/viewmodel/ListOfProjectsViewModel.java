package sk.uniza.fri.janmokry.karnaughmap.viewmodel;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import sk.uniza.fri.janmokry.karnaughmap.data.ListOfProjectsPreferences;
import sk.uniza.fri.janmokry.karnaughmap.util.SL;
import sk.uniza.fri.janmokry.karnaughmap.viewmodel.view.IListOfProjectsView;

/**
 * Business logic for list of project screen
 */
public class ListOfProjectsViewModel extends ProjectBaseViewModel<IListOfProjectsView> {

    @Nullable
    private List<String> mProjectNames;

    @Override
    public void onCreate(Bundle arguments, @Nullable Bundle savedInstanceState) {
        super.onCreate(arguments, savedInstanceState);

        initProjectNames();
    }

    @Override
    public void onBindView(@NonNull IListOfProjectsView view) {
        super.onBindView(view);

        if(mProjectNames != null) {
            setDataToView();
        }
    }

    private void setDataToView() {
        final IListOfProjectsView view = getView();
        if (view != null) {
            view.setData(mProjectNames);
        }
    }

    private void initProjectNames() {
        mProjectNames = new ArrayList<>(SL.get(ListOfProjectsPreferences.class).getProjectNames());
        mProjectNames.add("Prevodník BCD + 3"); // TODO test data
        mProjectNames.add("Sedem segmentovka"); // TODO test data
    }
}
