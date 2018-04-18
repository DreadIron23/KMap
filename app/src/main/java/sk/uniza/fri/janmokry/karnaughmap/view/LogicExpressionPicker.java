package sk.uniza.fri.janmokry.karnaughmap.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import sk.uniza.fri.janmokry.karnaughmap.R;
import sk.uniza.fri.janmokry.karnaughmap.helper.ImplicantEditor;
import sk.uniza.fri.janmokry.karnaughmap.util.SpanUtil;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by Janci on 16.4.2018.
 */

public class LogicExpressionPicker extends FrameLayout {

    private static final int BIT_VALUE_0 = 0;
    private static final int BIT_VALUE_1 = 1;
    private static final int BIT_VALUE_10_ZERO = 10; // this means it's button with value zero
    private static final int BIT_VALUE_11_ONE = 11;  // this means it's button with value one

    public interface OnItemClickedListener {

        void onPickerItemClicked(int xthIndex, Integer value, int defaultBitValue);
    }

    @BindView(R.id.main_layout)
    protected LinearLayout mMainLayout;

    @BindView(R.id.first_row)
    protected LinearLayout mFirstRowLayout;

    @BindView(R.id.second_row)
    protected LinearLayout mSecondRowLayout;

    private List<LogicExpressionPickerItemView> mFirstRowList;
    private List<LogicExpressionPickerItemView> mSecondRowList;

    private boolean mIsShown = false;
    private ImplicantView mEditingImplicant;

    private OnItemClickedListener mOnItemClickedListener = (xthIndex, value, defaultBitValue) -> {
        switch (defaultBitValue) {
            case BIT_VALUE_1: {
                mEditingImplicant.setXthBit(xthIndex, value);
                unselectFromSecondRow(xthIndex);
                unselectOne();
                unselectZero();
                break;
            }
            case BIT_VALUE_0: {
                mEditingImplicant.setXthBit(xthIndex, value);
                unselectFromFirstRow(xthIndex);
                unselectOne();
                unselectZero();
                break;
            }
            case BIT_VALUE_10_ZERO: {
                mEditingImplicant.setToZero();
                for (int index = 0; index < mFirstRowList.size() - 1; index++) {
                    unselectFromFirstRow(index);
                }
                for (int index = 0; index < mSecondRowList.size(); index++) {
                    unselectFromSecondRow(index);
                }
                break;
            }
            case BIT_VALUE_11_ONE: {
                mEditingImplicant.setToOne();
                for (int index = 0; index < mFirstRowList.size(); index++) {
                    unselectFromFirstRow(index);
                }
                for (int index = 0; index < mSecondRowList.size() - 1; index++) {
                    unselectFromSecondRow(index);
                }
                break;
            }
        }
    };

    private void unselectZero() {
        unselectFromFirstRow(mFirstRowList.size() - 1);
    }

    private void unselectOne() {
        unselectFromSecondRow(mSecondRowList.size() - 1);
    }

    private void unselectFromSecondRow(int index) {
        final LogicExpressionPickerItemView item = mSecondRowList.get(index);
        if (item.isSelected()) {
            item.unselect();
        }
    }

    private void unselectFromFirstRow(int index) {
        final LogicExpressionPickerItemView item = mFirstRowList.get(index);
        if (item.isSelected()) {
            item.unselect();
        }
    }


    public LogicExpressionPicker(@NonNull Context context) {
        super(context);

        init(context);
    }

    public LogicExpressionPicker(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    public LogicExpressionPicker(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public LogicExpressionPicker(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init(context);
    }

    private void init(Context context) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.logic_expression_picker, this, true);
        ButterKnife.bind(this);
    }

    public void setup(int numberOfVariables) {
        mFirstRowLayout.removeAllViews();
        mSecondRowLayout.removeAllViews();
        mFirstRowList = new ArrayList<>(numberOfVariables);
        mSecondRowList = new ArrayList<>(numberOfVariables);
        for (int index = 0; index < numberOfVariables; index++) {
            final LogicExpressionPickerItemView firstTextView = new LogicExpressionPickerItemView(getContext(), index, BIT_VALUE_1, mOnItemClickedListener);
            final LogicExpressionPickerItemView secondTextView = new LogicExpressionPickerItemView(getContext(), index, BIT_VALUE_0, mOnItemClickedListener);
            firstTextView.setText(SpanUtil.spanVariableName("x" + index));
            secondTextView.setText(SpanUtil.spanVariableNameNegated("x" + index));

            mFirstRowLayout.addView(firstTextView);
            mSecondRowLayout.addView(secondTextView);
            mFirstRowList.add(firstTextView);
            mSecondRowList.add(secondTextView);
        }

        // add 1 and 0 buttons to the end
        final LogicExpressionPickerItemView firstTextView = new LogicExpressionPickerItemView(getContext(), numberOfVariables, BIT_VALUE_10_ZERO, mOnItemClickedListener);
        final LogicExpressionPickerItemView secondTextView = new LogicExpressionPickerItemView(getContext(), numberOfVariables, BIT_VALUE_11_ONE, mOnItemClickedListener);
        firstTextView.setText(SpanUtil.spanIntegerToLowerCase(0));
        secondTextView.setText(SpanUtil.spanIntegerToLowerCase(1));

        mFirstRowLayout.addView(firstTextView);
        mSecondRowLayout.addView(secondTextView);
        mFirstRowList.add(firstTextView);
        mSecondRowList.add(secondTextView);
    }

    public void linkAndShow(ImplicantView implicant) {
        if (mEditingImplicant != null) {
            mEditingImplicant.unselect();
        }

        mEditingImplicant = implicant;
        initItemStates(implicant);

        if (!mIsShown) {
            toggleBottomPanel(true);
        }
    }

    private void initItemStates(ImplicantView implicantView) {
        final ImplicantEditor editor = implicantView.getImplicantEditor();
        for (int index = 0; index < editor.size(); index++) {
            final int xthBit = editor.getXthBit(index);
            switch (xthBit) {
                case 1:
                    mFirstRowList.get(index).select();
                    mSecondRowList.get(index).unselect();
                    break;
                case 0:
                    mFirstRowList.get(index).unselect();
                    mSecondRowList.get(index).select();
                    break;
                default:
                    mFirstRowList.get(index).unselect();
                    mSecondRowList.get(index).unselect();
                    break;
            }
        }
        mFirstRowList.get(editor.size()).unselect();
        mSecondRowList.get(editor.size()).unselect();
        if (editor.isSetToOne()) {
            final LogicExpressionPickerItemView item = mSecondRowList.get(mSecondRowList.size() - 1);
            if (!item.isSelected()) {
                item.select();
            }
        }
        if (editor.isSetToZero()) {
            final LogicExpressionPickerItemView item = mFirstRowList.get(mFirstRowList.size() - 1);
            if (!item.isSelected()) {
                item.select();
            }
        }
    }

    public void hide() {
        if (mIsShown) {
            toggleBottomPanel(false);
        }
    }

    private void toggleBottomPanel(boolean show) {
        if (show) {
            setVisibility(VISIBLE);
        }
        measure(getLayoutParams().width, WRAP_CONTENT);
        int legendHeightPx = getMeasuredHeight();
        int start = show ? 0 : legendHeightPx;
        int end = show ? legendHeightPx : 0;
        ValueAnimator anim = ValueAnimator.ofInt(start, end);
        anim.addUpdateListener(valueAnimator -> {
            int currentValue = (int) valueAnimator.getAnimatedValue();
            ViewGroup.LayoutParams layoutParams = getLayoutParams();
            layoutParams.height = currentValue;
            setLayoutParams(layoutParams);
        });
        anim.setDuration(300L);
        this.mIsShown = show;
        anim.start();
    }
}
