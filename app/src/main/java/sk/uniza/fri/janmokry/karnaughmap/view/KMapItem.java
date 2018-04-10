package sk.uniza.fri.janmokry.karnaughmap.view;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import sk.uniza.fri.janmokry.karnaughmap.R;

/**
 * Created by Janci on 26.3.2018.
 */

public class KMapItem extends FrameLayout {

    public interface OnBinClickedListener {

        void onBinClicked(KMapItem clickedView);
    }

    public interface OnVariableCountChangeListener {

        void onVariableCountChange();
    }

    @BindView(R.id.title)
    protected TextView mTitleView;

    @BindView(R.id.minus)
    protected ImageView mMinus;

    @BindView(R.id.plus)
    protected ImageView mPlus;

    @BindView(R.id.bin)
    protected ImageView mBin;

    @BindView(R.id.kmap_item_container)
    protected FrameLayout mKMapItemContainer;

    private KMapView mAttachedKMap;

    @Nullable
    private OnBinClickedListener mOnBinClickedListener;

    @Nullable
    private OnVariableCountChangeListener mOnVariableCountChangeListener;

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
        mTitleView.setText(kMapView.getKMapCollection().getTitle());
    }

    public void setTitle(String title) {
        mAttachedKMap.setTitle(title);
        setTitleViewText();
    }

    public KMapView getKMap() {
        return mAttachedKMap;
    }

    public void setOnBinClickedListener(@Nullable OnBinClickedListener listener) {
        this.mOnBinClickedListener = listener;
    }

    public void setOnVariableCountChangeListener(@Nullable OnVariableCountChangeListener listener) {
        this.mOnVariableCountChangeListener = listener;
    }

    public void onRemoval() {
        mAttachedKMap.onRemoval();
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
