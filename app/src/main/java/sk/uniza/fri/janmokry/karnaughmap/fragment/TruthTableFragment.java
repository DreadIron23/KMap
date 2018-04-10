package sk.uniza.fri.janmokry.karnaughmap.fragment;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import sk.uniza.fri.janmokry.karnaughmap.R;
import sk.uniza.fri.janmokry.karnaughmap.kmap.KMapCollection;
import sk.uniza.fri.janmokry.karnaughmap.view.TruthTableView;
import sk.uniza.fri.janmokry.karnaughmap.viewmodel.ProjectViewModel;
import sk.uniza.fri.janmokry.karnaughmap.viewmodel.TruthTableViewModel;
import sk.uniza.fri.janmokry.karnaughmap.viewmodel.view.ITruthTableView;

/**
 * View logic
 */
public class TruthTableFragment extends ProjectBaseFragment<ITruthTableView, TruthTableViewModel> implements ITruthTableView {

    public static TruthTableFragment newInstance() {
        return new TruthTableFragment();
    }

    @BindView(R.id.truth_table_container)
    protected LinearLayout mTruthTableContainer;

    @BindView(R.id.no_data_message)
    protected TextView mNoDataMessage;

    private TruthTableView mTruthTableView;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_truth_table;
    }

    @Override
    protected void init() {
        getProjectViewModel().mTruthTableCollection.observe(this, truthTableCollection -> {

            if (truthTableCollection != null) {
                mTruthTableView = new TruthTableView(getContext(), truthTableCollection);
                if (!mTruthTableView.isEmpty()) {
                    showTable();
                }
            }
        });
    }

    public ProjectViewModel getProjectViewModel() {
        return ((ProjectFragment) getParentFragment()).getViewModel();
    }

    @Override
    public void onKMapAddition(KMapCollection collection) {
        if (mTruthTableView.isEmpty()) {
            showTable();
        }
        mTruthTableView.onKMapAddition(collection);
    }

    @Override
    public void onKMapRemoval(KMapCollection collection) {
        mTruthTableView.onKMapRemoval(collection);
        if (mTruthTableView.isEmpty()) {
            hideTable();
        }
    }

    @Override
    public void layoutTruthTable() {
        mTruthTableView.requestLayout();
    }

    private void showTable() {
        mTruthTableContainer.removeAllViews();
        mTruthTableContainer.addView(mTruthTableView);
        setNoDataMessageVisibility();
    }

    private void hideTable() {
        mTruthTableContainer.removeAllViews();
        setNoDataMessageVisibility();
    }

    private void setNoDataMessageVisibility() {
        if (mTruthTableContainer.getChildCount() == 0) {
            mNoDataMessage.setVisibility(View.VISIBLE);
        } else {
            mNoDataMessage.setVisibility(View.GONE);
        }
    }
}
