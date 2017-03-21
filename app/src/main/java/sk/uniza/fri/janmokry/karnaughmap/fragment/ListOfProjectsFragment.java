package sk.uniza.fri.janmokry.karnaughmap.fragment;

import android.support.v7.widget.RecyclerView;
import android.widget.ProgressBar;

import java.util.List;

import butterknife.BindView;
import sk.uniza.fri.janmokry.karnaughmap.R;
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

    @BindView(R.id.progress_bar)
    protected ProgressBar mProgressBar;

    private ListOfProjectsRecyclerViewAdapter mAdapter;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_list_of_projects;
    }

    @Override
    protected void init() {
        final RecyclerView.LayoutManager layoutManager = new LinearLayoutManagerWithSmoothScroller(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);

        mAdapter = new ListOfProjectsRecyclerViewAdapter(getActivity(), new ListOfProjectsRecyclerViewAdapter.OnProjectClickedListener() {

            @Override
            public void onProjectClicked(String projectName) {
                mActivity.shortToast("Clicked on " + projectName);
            }
        });
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void setData(List<String> projectNames) {
        mAdapter.addAll(projectNames);
    }
}
