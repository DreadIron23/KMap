package sk.uniza.fri.janmokry.karnaughmap.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import sk.uniza.fri.janmokry.karnaughmap.R;
import sk.uniza.fri.janmokry.karnaughmap.fragment.ListOfProjectsFragment;
import sk.uniza.fri.janmokry.karnaughmap.viewmodel.EmptyViewModel;
import sk.uniza.fri.janmokry.karnaughmap.viewmodel.view.IEmptyView;

public class ListOfProjectsActivity extends ProjectBaseActivity<IEmptyView, EmptyViewModel> {

    public static Intent newIntent(Context context) {
        return new Intent(context, ListOfProjectsActivity.class);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_list_of_projects;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_activity_list_od_projects);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.contact_container, ListOfProjectsFragment.newInstance())
                    .commit();
        }
    }
}
