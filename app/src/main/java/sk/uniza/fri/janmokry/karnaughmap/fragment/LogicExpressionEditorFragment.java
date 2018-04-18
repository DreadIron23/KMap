package sk.uniza.fri.janmokry.karnaughmap.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.text.SpannableStringBuilder;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.TextView;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import sk.uniza.fri.janmokry.karnaughmap.R;
import sk.uniza.fri.janmokry.karnaughmap.algorithm.quinemccluskey.Number;
import sk.uniza.fri.janmokry.karnaughmap.algorithm.quinemccluskey.Solution;
import sk.uniza.fri.janmokry.karnaughmap.util.GraphicsUtil;
import sk.uniza.fri.janmokry.karnaughmap.util.SpanUtil;
import sk.uniza.fri.janmokry.karnaughmap.view.ImplicantView;
import sk.uniza.fri.janmokry.karnaughmap.view.LogicExpressionPicker;
import sk.uniza.fri.janmokry.karnaughmap.viewmodel.LogicExpressionEditorViewModel;
import sk.uniza.fri.janmokry.karnaughmap.viewmodel.view.ILogicExpressionEditorView;

import static sk.uniza.fri.janmokry.karnaughmap.activity.LogicExpressionEditorActivity.ARG_NUMBER_OF_VARIABLES;
import static sk.uniza.fri.janmokry.karnaughmap.activity.LogicExpressionEditorActivity.ARG_SOLUTION;

/**
 * View logic for Logic Expression Editor screen.
 */
public class LogicExpressionEditorFragment
        extends ProjectBaseFragment<ILogicExpressionEditorView, LogicExpressionEditorViewModel>
        implements ILogicExpressionEditorView {

    public static final long IMPLICANT_ANIMATION_DURATION = 500L;
    public static final long SHOWCASE_IMPLICANT_ANIMATION_DURATION = 300L;
    public static final long SHOWCASE_IMPLICANT_ANIMATION_DELAY = 100L;

    public static LogicExpressionEditorFragment newInstance(Solution solution, int numberOfVariables) {
        final LogicExpressionEditorFragment fragment = new LogicExpressionEditorFragment();
        final Bundle args = new Bundle();
        args.putSerializable(ARG_SOLUTION, solution);
        args.putInt(ARG_NUMBER_OF_VARIABLES, numberOfVariables);
        fragment.setArguments(args);
        return fragment;
    }

    public interface OnImplicantClickedListener {

        void onImplicantClicked(ImplicantView implicant);
    }

    @BindView(R.id.title_expression)
    protected TextView mTitleExpression;

    @BindView(R.id.logic_expression)
    protected FlowLayout mLogicExpressionLayout;

    @BindView(R.id.picker)
    protected LogicExpressionPicker mPicker;

    private List<ImplicantView> mImplicants = new ArrayList<>();
    private AppCompatImageView mPlusView;

    @Nullable
    private ImplicantView mEditingImplicant;

    private Solution mSolution;
    private int mNumberOfVariables;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    private OnImplicantClickedListener mOnImplicantClickedListener = implicant -> {
        checkForInvalidImplicantRemoval(implicant);

        if (!implicant.isSelected()) {
            mEditingImplicant = implicant;
            mPicker.linkAndShow(implicant);
        } else {
            mPicker.hide();
        }
    };

    private void checkForInvalidImplicantRemoval(ImplicantView implicant) {
        final ImplicantView invalidImplicant = getInvalidImplicant(implicant);
        if (invalidImplicant != null) { // remove ImplicantView if it's blank after animation
            mHandler.postDelayed(() -> {
                final int index = mImplicants.indexOf(invalidImplicant);
                mImplicants.remove(invalidImplicant);
                final int indexOnLayout = index * 2;
                mLogicExpressionLayout.removeViewAt(indexOnLayout);
                if (mLogicExpressionLayout.getChildAt(indexOnLayout) instanceof TextView) {
                    mLogicExpressionLayout.removeViewAt(indexOnLayout);
                } else if(indexOnLayout - 1 >= 0) {
                    mLogicExpressionLayout.removeViewAt(indexOnLayout - 1);
                }
                if (invalidImplicant == mEditingImplicant) {
                    mEditingImplicant = null;
                }
            }, IMPLICANT_ANIMATION_DURATION);
        }
    }

    private @Nullable ImplicantView getInvalidImplicant(@Nullable ImplicantView implicant) {
        if (implicant != null && implicant.getImplicantEditor().isBlank()) {
            return implicant;
        }
        if (mEditingImplicant != null && mEditingImplicant.getImplicantEditor().isBlank()) {
            return mEditingImplicant;
        }
        if (implicant != null && isDuplicate(implicant)) {
            showToast(getString(R.string.logic_expression_editor_screen_duplicate_toast));
            return implicant;
        }
        if (mEditingImplicant != null && isDuplicate(mEditingImplicant)) {
            showToast(getString(R.string.logic_expression_editor_screen_duplicate_toast));
            return mEditingImplicant;
        }
        return null;
    }

    private boolean isDuplicate(@NonNull ImplicantView implicant) {
        for (ImplicantView view : mImplicants) {
            if (view == implicant) continue; // when it's itself
            final boolean isDuplicate = view.isDuplicate(implicant);
            if (isDuplicate) return true;
        }
        return false;
    }


    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_logic_expression_editor;
    }

    @Override
    protected void init() { // setting from model
        setHasOptionsMenu(true);
    }

    @Override
    public void init(Solution solution, int numberOfVariables) {
        mSolution = solution;
        mNumberOfVariables = numberOfVariables;
        final CharSequence kMapName = SpanUtil.spanVariableName(mSolution.getTitle());
        mTitleExpression.setText(new SpannableStringBuilder(kMapName).append(" = "));

        initExpressions();
        mPicker.setup(mNumberOfVariables);

        runShowcaseAnimation();
    }

    private void runShowcaseAnimation() {
        long baseDelay = SHOWCASE_IMPLICANT_ANIMATION_DELAY;
        long delay = baseDelay * 5;
        for (ImplicantView view : mImplicants) {
            view.animateShowcase(delay);
            delay += baseDelay;
        }

        mHandler.postDelayed(() -> {
            mPlusView.animate().rotation(360f)
                    .setDuration(SHOWCASE_IMPLICANT_ANIMATION_DURATION * 2)
                    .start();
        }, delay);
    }

    @Override
    public void onStop() {
        super.onStop();

        // saving state to model
        getViewModel().onSave(createEditingSolution());
    }

    private @NonNull Solution createEditingSolution() {
        final ArrayList<Number> editingImplicants = new ArrayList<>();
        for (ImplicantView view : mImplicants) {
            if (view.getImplicantEditor().isSetToZero()) continue;
            editingImplicants.add(new Number(view.getImplicantEditor().values()));
        }
        return new Solution(editingImplicants, mSolution.getTitle());
    }

    private void initExpressions() {
        final List<Number> solution = mSolution.getSolution();
        int counter = 0;
        for (Number implicant : solution) {
            final ImplicantView implicantView = new ImplicantView(getContext(), implicant, mOnImplicantClickedListener);
            if (implicant.isCoveringWholeMap()) {
                implicantView.setToOne();
            }

            mImplicants.add(implicantView);
            mLogicExpressionLayout.addView(implicantView);
            if (counter++ < solution.size() - 1) {
                final TextView plusView = getPlusTextView();
                mLogicExpressionLayout.addView(plusView);
            }
        }

        mPlusView = new AppCompatImageView(getContext());
        mPlusView.setBackgroundResource(R.drawable.ic_add_circle);
        final int size = GraphicsUtil.dpToPx(getResources(), 28);
        final FlowLayout.LayoutParams params = new FlowLayout.LayoutParams(size, size);
        params.setGravity(Gravity.CENTER);
        mPlusView.setLayoutParams(params);

        mPlusView.setOnClickListener(view -> {
            checkForInvalidImplicantRemoval(mEditingImplicant);

            if (!mImplicants.isEmpty()) { // add plus sign if needed
                final TextView plusTextView = getPlusTextView();
                mLogicExpressionLayout.addView(plusTextView, mLogicExpressionLayout.getChildCount() - 1);
            }

            // create and add new blank ImplicantView
            final ImplicantView implicantView = new ImplicantView(getContext(),
                    Number.getBlankNumber(mNumberOfVariables), mOnImplicantClickedListener);
            mImplicants.add(implicantView);
            mLogicExpressionLayout.addView(implicantView, mLogicExpressionLayout.getChildCount() - 1);

            // gain focus and link with picker
            implicantView.select();
            mEditingImplicant = implicantView;
            mPicker.linkAndShow(implicantView);
        });

        mLogicExpressionLayout.addView(mPlusView);
    }

    private @NonNull TextView getPlusTextView() {
        final TextView plusView = new TextView(getContext());
        plusView.setText("+");
        plusView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        final FlowLayout.LayoutParams layoutParams = new FlowLayout.LayoutParams(
                FlowLayout.LayoutParams.WRAP_CONTENT, FlowLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setGravity(Gravity.CENTER_VERTICAL);
        plusView.setLayoutParams(layoutParams);
        return plusView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_done:
                getViewModel().onActionDone(createEditingSolution());
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finishView() {
        getActivity().finish();
    }

    public boolean hasExpressionChanged() {
        final Solution editingSolution = createEditingSolution();
        return !mSolution.equalsValues(editingSolution);
    }
}
