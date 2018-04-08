package sk.uniza.fri.janmokry.karnaughmap.fragment;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import sk.uniza.fri.janmokry.karnaughmap.R;
import sk.uniza.fri.janmokry.karnaughmap.view.TruthTableView;
import sk.uniza.fri.janmokry.karnaughmap.viewmodel.EmptyViewModel;
import sk.uniza.fri.janmokry.karnaughmap.viewmodel.ProjectViewModel;
import sk.uniza.fri.janmokry.karnaughmap.viewmodel.view.IEmptyView;

/**
 * View logic
 */
public class TruthTableFragment extends ProjectBaseFragment<IEmptyView, EmptyViewModel> implements IEmptyView {

    public static TruthTableFragment newInstance() {
        return new TruthTableFragment();
    }

    @BindView(R.id.truth_table_container)
    protected LinearLayout mTruthTableContainer;

    @BindView(R.id.no_data_message)
    protected TextView mNoDataMessage;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_truth_table;
    }

    @Override
    protected void init() {
        getProjectViewModel().mTruthTableCollection.observe(this, truthTableCollection -> {

            if (truthTableCollection != null) {
                mTruthTableContainer.removeAllViews();
                final TruthTableView truthTableView = new TruthTableView(getContext(), truthTableCollection);
                mTruthTableContainer.addView(truthTableView);

                setNoDataMessageVisibility();
            }
        });
    }

    public ProjectViewModel getProjectViewModel() {
        return ((ProjectFragment) getParentFragment()).getViewModel();
    }

    private void setNoDataMessageVisibility() {
        if (mTruthTableContainer.getChildCount() == 0) {
            mNoDataMessage.setVisibility(View.VISIBLE);
        } else {
            mNoDataMessage.setVisibility(View.GONE);
        }
    }
}
