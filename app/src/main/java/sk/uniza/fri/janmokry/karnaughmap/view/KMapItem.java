package sk.uniza.fri.janmokry.karnaughmap.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.supercharge.shimmerlayout.ShimmerLayout;
import sk.uniza.fri.janmokry.karnaughmap.R;
import sk.uniza.fri.janmokry.karnaughmap.algorithm.quinemccluskey.Number;
import sk.uniza.fri.janmokry.karnaughmap.algorithm.quinemccluskey.Solution;
import sk.uniza.fri.janmokry.karnaughmap.data.ColorPaletteService;
import sk.uniza.fri.janmokry.karnaughmap.kmap.KMapCell;
import sk.uniza.fri.janmokry.karnaughmap.text.LowerCaseOverlineSpan;
import sk.uniza.fri.janmokry.karnaughmap.util.SL;

/**
 * Layout holding all KMap related views and handling interaction between them.
 *
 * Created by Janci on 26.3.2018.
 */

public class KMapItem extends FrameLayout {

    public interface OnBinClickedListener {

        void onBinClicked(KMapItem clickedView);
    }

    public interface OnVariableCountChangeListener {

        void onVariableCountChange();
    }

    public interface OnLogicExpressionClickedListener {

        void onLogicExpressionClicked(Solution solution, int numberOfVariables);
    }

    @BindView(R.id.title)
    protected TextView mTitleView;

    @BindView(R.id.minus)
    protected ImageView mMinus;

    @BindView(R.id.plus)
    protected ImageView mPlus;

    @BindView(R.id.bin)
    protected ImageView mBin;

    @BindView(R.id.shimmer)
    protected ShimmerLayout mShimmer;
    private boolean mIsComputationRunning = false;

    @BindView(R.id.kmap_item_container)
    protected FrameLayout mKMapItemContainer;

    @BindView(R.id.title_expression)
    protected TextView mTitleExpression;

    @BindView(R.id.logic_expression)
    protected TextView mLogicExpression;

    private KMapView mAttachedKMap;

    @Nullable
    private OnBinClickedListener mOnBinClickedListener;

    @Nullable
    private OnVariableCountChangeListener mOnVariableCountChangeListener;

    @Nullable
    private OnLogicExpressionClickedListener mOnLogicExpressionClickedListener;

    private final Handler mHandler = new Handler(Looper.getMainLooper());

    public KMapItem(@NonNull Context context) {
        super(context);

        init();
    }

    public KMapItem(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public KMapItem(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public KMapItem(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init();
    }

    private void init() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        inflater.inflate(R.layout.item_kmap, this, true);
        ButterKnife.bind(this);
    }

    public void addKMap(@NonNull KMapView kMapView) {
        mKMapItemContainer.removeAllViews();
        mAttachedKMap = kMapView;
        mKMapItemContainer.addView(kMapView);
        final CharSequence title = kMapView.getKMapCollection().getSpannedTitle();
        mTitleView.setText(title);
        mTitleExpression.setText(new SpannableStringBuilder(title).append(" = "));
        mHandler.post(() -> { // run after mTitleExpression has been invalidated
            mLogicExpression.setMaxWidth(getResources().getDisplayMetrics().widthPixels - mTitleExpression.getWidth());
        });
        initOnLogicExpressionClickedListener();
        showSolution(kMapView.getKMapCollection().getSolution());
    }

    private void initOnLogicExpressionClickedListener() {
        final OnClickListener listener = view -> {
            if (mOnLogicExpressionClickedListener != null) {
                final Solution solution = mAttachedKMap.getKMapCollection().getSolution();
                mOnLogicExpressionClickedListener.onLogicExpressionClicked(solution,
                        mAttachedKMap.getKMapCollection().getNumberOfVariables());
            }
        };
        mLogicExpression.setOnClickListener(listener);
        mTitleExpression.setOnClickListener(listener);
    }

    public void setTitle(String title) {
        mAttachedKMap.setTitle(title);
        setTitleViewText();
    }

    public KMapView getKMap() {
        return mAttachedKMap;
    }

    public void setOnBinClickedListener(@Nullable OnBinClickedListener listener) {
        mOnBinClickedListener = listener;
    }

    public void setOnVariableCountChangeListener(@Nullable OnVariableCountChangeListener listener) {
        mOnVariableCountChangeListener = listener;
    }

    public void setOnLogicExpressionClickedListener(@Nullable OnLogicExpressionClickedListener listener) {
        mOnLogicExpressionClickedListener = listener;
    }

    public void onRemoval() {
        mAttachedKMap.onRemoval();
    }

    public void onComputationKickOf() {
        mIsComputationRunning = true;
        // show shimmer after 1 second
        mHandler.postDelayed(() -> {
            if (mIsComputationRunning) {
                mShimmer.setVisibility(VISIBLE);
            }
        }, 1000L);
    }

    public void onComputationDone(@NonNull Solution solution) {
        mIsComputationRunning = false;
        mShimmer.setVisibility(GONE);

        showSolution(solution);
    }

    /** @param solution is null when map is created and not yet changed -> thus result is 0 */
    private void showSolution(@NonNull Solution solution) {
        final List<Number> configurations = solution.getSolution();
        final int[] colorPalette = SL.get(ColorPaletteService.class).providePaletteFor(configurations.size());
        final SpannableStringBuilder builder = new SpannableStringBuilder();
        int paletteIndex = 0;
        int configurationCounter = 0;
        for (Number configuration : configurations) {
            if (paletteIndex == 0) { // little hack for proper Paint setup for LowerCaseOverlineSpan
                builder.append(".");
                builder.setSpan(new ForegroundColorSpan(Color.TRANSPARENT), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            final int color = colorPalette[paletteIndex++ % (colorPalette.length - 1)];
            final ArrayList<Integer> numbers = configuration.mNumber;
            int bitCounter = 0;
            for (int bitIndex = numbers.size() - 1; bitIndex >= 0; bitIndex--) {
                final Integer bit = numbers.get(bitIndex);
                final int baseLength = builder.length();
                if (bit == KMapCell.VALUE_1 || bit == KMapCell.VALUE_0) {
                    builder.append("x").append(String.valueOf(bitCounter));
                    builder.setSpan(new ForegroundColorSpan(color), baseLength, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    builder.setSpan(new RelativeSizeSpan(0.5f), baseLength + 1, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    builder.setSpan(Typeface.BOLD, baseLength + 1, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    if (bit == KMapCell.VALUE_0) {
                        builder.setSpan(new LowerCaseOverlineSpan(), baseLength, baseLength + "x".length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }
                bitCounter++;
            }
            if (++configurationCounter != configurations.size()) { // if it's not last add plus sign
                builder.append(" + ");
            }
        }
        if (configurations.size() == 1 && configurations.get(0).isCoveringWholeMap()) {
            builder.append(String.valueOf(Solution.RESULT_1));
        }
        if (solution.isEmpty()) {
            builder.append(String.valueOf(Solution.RESULT_0));
        }
        mLogicExpression.setText(builder);
    }

    @OnClick(R.id.plus)
    protected void onPlusClicked() {
        mAttachedKMap.addVariable();
        if (mOnVariableCountChangeListener != null) {
            mOnVariableCountChangeListener.onVariableCountChange();
        }
    }

    @OnClick(R.id.minus)
    protected void onMinusClicked() {
        mAttachedKMap.removeVariable();
        if (mOnVariableCountChangeListener != null) {
            mOnVariableCountChangeListener.onVariableCountChange();
        }
    }

    @OnClick(R.id.bin)
    protected void onBinClicked() {
        if (mOnBinClickedListener != null) {
            mOnBinClickedListener.onBinClicked(this);
        }
    }

    /** Sets title from attached KMap */
    private void setTitleViewText() {
        mTitleView.setText(mAttachedKMap.getKMapCollection().getTitle());
    }
}
