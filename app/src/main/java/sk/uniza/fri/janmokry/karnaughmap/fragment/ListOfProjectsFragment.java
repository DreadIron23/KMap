package sk.uniza.fri.janmokry.karnaughmap.fragment;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import sk.uniza.fri.janmokry.karnaughmap.R;
import sk.uniza.fri.janmokry.karnaughmap.activity.AdjustProjectActivity;
import sk.uniza.fri.janmokry.karnaughmap.activity.ProjectActivity;
import sk.uniza.fri.janmokry.karnaughmap.adapter.ListOfProjectsRecyclerViewAdapter;
import sk.uniza.fri.janmokry.karnaughmap.data.ProjectInfo;
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
            public void onProjectClicked(ProjectInfo projectInfo) {
                final Intent intent = ProjectActivity.newIntent(getContext(), projectInfo);
                startActivity(intent);
            }

            @Override
            public void onEditClicked(ProjectInfo projectInfo) {
                final Intent intent = AdjustProjectActivity.newIntentForEditing(getContext(), projectInfo);
                startActivity(intent);
            }

            @Override
            public void onBinClicked(final ProjectInfo projectInfo) {
                new AlertDialog.Builder(getContext(), R.style.DialogStyle)
                        .setTitle(R.string.list_of_projects_screen_delete_dialog_title)
                        .setMessage(R.string.list_of_projects_screen_delete_dialog_message)
                        .setPositiveButton(R.string.list_of_projects_screen_delete_dialog_delete, (dialog, whichButton) ->
                                getViewModel().deleteProject(projectInfo)
                        )
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
    public void setData(List<ProjectInfo> projectInfoList) {
        mAdapter.clear();
        mAdapter.addAll(projectInfoList);
    }

    @Override
    public void deleteProject(ProjectInfo projectInfo) {
        mAdapter.delete(projectInfo);
    }
}
