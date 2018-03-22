package sk.uniza.fri.janmokry.karnaughmap.viewmodel;

import android.os.Bundle;
import android.support.annotation.Nullable;

import sk.uniza.fri.janmokry.karnaughmap.viewmodel.view.IProjectView;

/**
 * Business logic for project fragment
 */
public class ProjectViewModel extends ProjectBaseViewModel<IProjectView> {

    @Override
    public void onCreate(Bundle arguments, @Nullable Bundle savedInstanceState) {
        super.onCreate(arguments, savedInstanceState);
    }
}
