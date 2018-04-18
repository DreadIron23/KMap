package sk.uniza.fri.janmokry.karnaughmap.viewmodel;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import sk.uniza.fri.janmokry.karnaughmap.activity.LogicExpressionEditorActivity;
import sk.uniza.fri.janmokry.karnaughmap.algorithm.quinemccluskey.Solution;
import sk.uniza.fri.janmokry.karnaughmap.data.EventBusService;
import sk.uniza.fri.janmokry.karnaughmap.data.event.LogicExpressionEditCompletionEvent;
import sk.uniza.fri.janmokry.karnaughmap.util.SL;
import sk.uniza.fri.janmokry.karnaughmap.viewmodel.view.ILogicExpressionEditorView;

/**
 * Business logic for editing logic expression for given map.
 */
public class LogicExpressionEditorViewModel extends ProjectBaseViewModel<ILogicExpressionEditorView> {

    private Solution mSolution;
    private int mNumberOfVariables;

    @Nullable
    private Solution mSavedEditingSolution = null;

    @Override
    public void onCreate(Bundle arguments, @Nullable Bundle savedInstanceState) {
        super.onCreate(arguments, savedInstanceState);

        mSolution = (Solution) arguments.getSerializable(LogicExpressionEditorActivity.ARG_SOLUTION);
        mNumberOfVariables = arguments.getInt(LogicExpressionEditorActivity.ARG_NUMBER_OF_VARIABLES, Integer.MIN_VALUE);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void onBindView(@NonNull ILogicExpressionEditorView view) {
        super.onBindView(view);

        if (getView() != null) {
            getView().init(mSavedEditingSolution == null ? mSolution : mSavedEditingSolution, mNumberOfVariables);
        }
    }

    private void finishView() {
        if (getView() != null) {
            getView().finishView();
        }
    }

    public void onSave(@NonNull Solution editingSolution) {
        mSavedEditingSolution = editingSolution;
    }

    public void onActionDone(@NonNull Solution editingSolution) {
        if (!mSolution.equalsValues(editingSolution)) { // if solution has changed
            SL.get(EventBusService.class).post(new LogicExpressionEditCompletionEvent(editingSolution));
        }
        finishView();
    }
}
