package sk.uniza.fri.janmokry.karnaughmap.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import sk.uniza.fri.janmokry.karnaughmap.R;
import sk.uniza.fri.janmokry.karnaughmap.util.GraphicsUtil;

import static sk.uniza.fri.janmokry.karnaughmap.algorithm.quinemccluskey.Number.COMBINED_VALUE;

/**
 * Created by Janci on 17.4.2018.
 */

@SuppressLint("ViewConstructor")
public class LogicExpressionPickerItemView extends FrameLayout {

    private static final int SIZE_IN_DP = 36;

    @BindView(R.id.text)
    protected TextView mTextView;

    private boolean mIsSelected = false;
    private @ColorInt int mSelectTextColor;
    private @ColorInt int mUnselectTextColor;
    private ColorDrawable mSelectBackgroundColor;
    private ColorDrawable mUnselectBackgroundColor;

    private int mXthBitIndex;
    private int mBitValue; // 1 for x or 0 for x'

    public LogicExpressionPickerItemView(Context context, int xthBitIndex, int bitValue,
                                         LogicExpressionPicker.OnItemClickedListener listener) {
        super(context);

        init(context, xthBitIndex, bitValue, listener);
    }

    private void init(Context context, int xthBitIndex, int bitValue, LogicExpressionPicker.OnItemClickedListener listener) {
        LayoutInflater.from(context).inflate(R.layout.item_expression_picker_text_view, this, true);
        ButterKnife.bind(this);

        mXthBitIndex = xthBitIndex;
        mBitValue = bitValue;
        setOnClickListener(view -> {
            if (!mIsSelected) {
                select();
            } else {
                unselect();
            }
            listener.onPickerItemClicked(mXthBitIndex, mIsSelected ? mBitValue : COMBINED_VALUE, bitValue);
        });

        mSelectTextColor = mTextView.getTextColors().getDefaultColor();
        mUnselectTextColor = getResources().getColor(R.color.black_text_disabled);
        mSelectBackgroundColor = new ColorDrawable(getResources().getColor(R.color.selected_background));
        mUnselectBackgroundColor = new ColorDrawable(getResources().getColor(R.color.white));

        final int size = GraphicsUtil.dpToPx(getResources(), SIZE_IN_DP);
        setLayoutParams(new ViewGroup.LayoutParams(size, size));
    }

    public void setText(CharSequence text) {
        mTextView.setText(text);
    }

    public void select() {
        mIsSelected = true;
        mTextView.setTextColor(mSelectTextColor);
        mTextView.setBackgroundDrawable(mSelectBackgroundColor);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mTextView.setZ(0f);
        }
    }

    public void unselect() {
        mIsSelected = false;
        mTextView.setTextColor(mUnselectTextColor);
        mTextView.setBackgroundDrawable(mUnselectBackgroundColor);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mTextView.setZ(2f);
        }
    }

    public boolean isSelected() {
        return mIsSelected;
    }

    public int getXthBitIndex() {
        return mXthBitIndex;
    }
}
