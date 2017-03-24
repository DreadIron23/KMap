package sk.uniza.fri.janmokry.karnaughmap.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import sk.uniza.fri.janmokry.karnaughmap.R;
import sk.uniza.fri.janmokry.karnaughmap.activity.AdjustProjectActivity;
import sk.uniza.fri.janmokry.karnaughmap.adapter.ListOfProjectsRecyclerViewAdapter;
import sk.uniza.fri.janmokry.karnaughmap.layoutmanager.LinearLayoutManagerWithSmoothScroller;
import sk.uniza.fri.janmokry.karnaughmap.viewmodel.ListOfProjectsViewModel;
import sk.uniza.fri.janmokry.karnaughmap.viewmodel.view.IListOfProjectsView;

/**
 * View logic
 */
public class ListOfProjectsFragment extends ProjectBaseFragment<IListOfProjectsView, ListOfProjectsViewModel> implements IListOfProjectsView {

    public static ListOfProjectsFragment newInstance() {
        return new ListOfProjectsFragment();
    }

    @BindView(R.id.recycler_view)
    protected RecyclerView mRecyclerView;

    private ListOfProjectsRecyclerViewAdapter mAdapter;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_list_of_projects;
    }

    @Override
    protected void init() {
        final RecyclerView.LayoutManager layoutManager = new LinearLayoutManagerWithSmoothScroller(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);

        mAdapter = new ListOfProjectsRecyclerViewAdapter(getActivity(), new ListOfProjectsRecyclerViewAdapter.OnClickListener() {

            @Override
            public void onProjectClicked(String projectName) {
                mActivity.shortToast("Clicked on " + projectName); // TODO Open Project
            }

            @Override
            public void onEditClicked(String projectName) {
                final Intent intent = AdjustProjectActivity.newIntentForEditing(getContext(), projectName);
                startActivity(intent);
            }

            @Override
            public void onBinClicked(final String projectName) {
                new AlertDialog.Builder(getContext(), R.style.DialogStyle)
                        .setTitle(R.string.list_of_projects_screen_delete_dialog_title)
                        .setMessage(R.string.list_of_projects_screen_delete_dialog_message)
                        .setPositiveButton(R.string.list_of_projects_screen_delete_dialog_logout, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int whichButton) {
                                getViewModel().deleteProject(projectName);
                            }

                        })
                        .setNegativeButton(R.string.list_of_projects_screen_delete_dialog_discard, null)
                        .show();
            }

        });
        mRecyclerView.setAdapter(mAdapter);
    }

    @OnClick(R.id.add_project)
    public void onAddProjectClicked() {
        final Intent intent = AdjustProjectActivity.newIntent(getContext());
        startActivity(intent);
    }

    @Override
    public void setData(List<String> projectNames) {
        mAdapter.clear();
        mAdapter.addAll(projectNames);
    }

    @Override
    public void deleteProject(String projectName) {
        mAdapter.delete(projectName);
    }
}
