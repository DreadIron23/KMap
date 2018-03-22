package sk.uniza.fri.janmokry.karnaughmap.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import sk.uniza.fri.janmokry.karnaughmap.R;
import sk.uniza.fri.janmokry.karnaughmap.viewmodel.EmptyViewModel;
import sk.uniza.fri.janmokry.karnaughmap.viewmodel.view.IEmptyView;

/**
 * View logic
 */
public class TruthTableFragment extends ProjectBaseFragment<IEmptyView, EmptyViewModel> implements IEmptyView {

    public static TruthTableFragment newInstance() {
        return new TruthTableFragment();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_truth_table;
    }

    @Override
    protected void init() {
    }
}
