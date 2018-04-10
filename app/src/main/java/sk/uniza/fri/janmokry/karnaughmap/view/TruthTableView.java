package sk.uniza.fri.janmokry.karnaughmap.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.DynamicLayout;
import android.text.Layout;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.RelativeSizeSpan;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import sk.uniza.fri.janmokry.karnaughmap.R;
import sk.uniza.fri.janmokry.karnaughmap.kmap.KMapCell;
import sk.uniza.fri.janmokry.karnaughmap.kmap.KMapCollection;
import sk.uniza.fri.janmokry.karnaughmap.kmap.TruthTableCollection;
import sk.uniza.fri.janmokry.karnaughmap.util.BitOperationUtil;
import sk.uniza.fri.janmokry.karnaughmap.util.GraphicsUtil;

import static sk.uniza.fri.janmokry.karnaughmap.kmap.KMapCell.VALUE_0_STRING;
import static sk.uniza.fri.janmokry.karnaughmap.kmap.KMapCell.VALUE_1_STRING;

/**
 * Created by Janci on 30.3.2018.
 */

@SuppressLint("ViewConstructor")
public class TruthTableView extends View {

    private final static String TAG = TruthTableView.class.getSimpleName();

    private static final int CELL_SIZE_IN_DP = 32;
    private static final int VALUE_CELL_SIZE_IN_DP = CELL_SIZE_IN_DP * 2;
    private static final int BORDER_OFFSET_IN_DP = 16; // effectively padding
    private static final int STROKE_WIDTH = 4;
    private static final String INVALID_POSITION = "\u2014"; // long dash

    private int mCellSizeInPx;
    private int mValueCellSizeInPx;
    private int mBorderOffsetInPx;
    private TruthTableCollection mTruthTableCollection;

    @Nullable
    private KMapCell mTappedCell = null;
    private int mActionDownRawPosX = 0;
    private int mActionDownRawPosY = 0;
    private final int mActionDownPositionTolerance = 10;
    private final KMapCollection.OnTapListener mOnTapListener = cell -> {
        invalidate();
    };

    private Context mContext;
    private Paint mLinePaint = new Paint();
    private Paint mThickLinePaint = new Paint();
    private Paint mCellValuePaint = new Paint();

    private SpannableString         mSpannedHashSign;
    private SpannableStringBuilder  mVariableLabelBuilder;
    private DynamicLayout           mVariableLabelLayout;
    private SpannableStringBuilder  mMapLabelBuilder;
    private DynamicLayout           mMapLabelLayout;

    public TruthTableView(Context context, TruthTableCollection truthTableCollection) {
        super(context);

        init(context, truthTableCollection);
    }

    private void init(Context context, TruthTableCollection truthTableCollection) {
        mContext = context;
        mTruthTableCollection = truthTableCollection;
        mTruthTableCollection.setOnTapListener(mOnTapListener);
        final Resources resources = context.getResources();

        mCellSizeInPx = GraphicsUtil.dpToPx(resources, CELL_SIZE_IN_DP);
        mValueCellSizeInPx = GraphicsUtil.dpToPx(resources, VALUE_CELL_SIZE_IN_DP);
        mBorderOffsetInPx = GraphicsUtil.dpToPx(resources, BORDER_OFFSET_IN_DP);

        mLinePaint.setColor(GraphicsUtil.fetchColor(mContext, R.attr.colorPrimary));
        mLinePaint.setStrokeWidth(STROKE_WIDTH);
        mLinePaint.setStrokeJoin(Paint.Join.ROUND);
        mLinePaint.setStrokeCap(Paint.Cap.ROUND);
        mLinePaint.setAntiAlias(true);

        mThickLinePaint = new Paint(mLinePaint);
        mThickLinePaint.setStrokeCap(Paint.Cap.BUTT);
        mThickLinePaint.setStrokeWidth(STROKE_WIDTH * 2);

        mCellValuePaint.setTextSize(mCellSizeInPx / 1.3f);
        mCellValuePaint.setTextAlign(Paint.Align.CENTER);
        mCellValuePaint.setAntiAlias(true);

        mSpannedHashSign = new SpannableString("#");
        mSpannedHashSign.setSpan(Typeface.ITALIC, 0, mSpannedHashSign.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

        mVariableLabelBuilder = new SpannableStringBuilder("x1");
        mVariableLabelBuilder.setSpan(new RelativeSizeSpan(0.5f), 1, mVariableLabelBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mVariableLabelBuilder.setSpan(Typeface.BOLD, 1, mVariableLabelBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        mMapLabelBuilder = new SpannableStringBuilder("y1");
        mMapLabelBuilder.setSpan(new RelativeSizeSpan(0.5f), 1, mVariableLabelBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mMapLabelBuilder.setSpan(Typeface.BOLD, 1, mVariableLabelBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        final TextPaint textPaint = new TextPaint();
        textPaint.setTextSize(mCellSizeInPx / 1.3f);
        textPaint.setAntiAlias(true);
        mVariableLabelLayout = new DynamicLayout(mVariableLabelBuilder, textPaint, mCellSizeInPx, Layout.Alignment.ALIGN_CENTER, 1, 0, false);
        mMapLabelLayout = new DynamicLayout(mMapLabelBuilder, textPaint, mCellSizeInPx, Layout.Alignment.ALIGN_CENTER, 1, 0, false);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (event.getAction()) {
            case (MotionEvent.ACTION_DOWN):
                mTappedCell = getCellAtPoint(x, y);
                mActionDownRawPosX = (int) event.getRawX();
                mActionDownRawPosY = (int) event.getRawY();
                return true;
            case (MotionEvent.ACTION_MOVE):
                return false;
            case MotionEvent.ACTION_UP:
                final KMapCell actionUpCell = getCellAtPoint(x, y);
                if (actionUpCell == mTappedCell && mTappedCell != null && isItTheSameSpot(event)) { // only flipping when tapped
                    mTappedCell.tapped();
                    invalidate();
                    return true;
                }
        }

        return super.onTouchEvent(event);
    }

    private boolean isItTheSameSpot(MotionEvent event) {
        return (int) event.getRawX() < mActionDownRawPosX + mActionDownPositionTolerance
                && (int) event.getRawX() > mActionDownRawPosX - mActionDownPositionTolerance
                && (int) event.getRawY() < mActionDownRawPosY + mActionDownPositionTolerance
                && (int) event.getRawY() > mActionDownRawPosY - mActionDownPositionTolerance;
    }

    private @Nullable KMapCell getCellAtPoint(int x, int y) {

        final int leftGridPosition = getPaddingLeft() + mBorderOffsetInPx + mValueCellSizeInPx +
                mTruthTableCollection.getMaximumVariables() * mCellSizeInPx;
        final int topGridPosition = getPaddingTop() + mBorderOffsetInPx + mCellSizeInPx;

        int lx = x - leftGridPosition;
        int ly = y - topGridPosition;

        if (lx < 0 || ly < 0) { // if lx/ly is between -1 and negative mCellSizeInPx it would pass test
            return null;
        }

        int row = lx / mCellSizeInPx;
        int column = ly / mCellSizeInPx;

        if(mTruthTableCollection.isValidPosition(column, row)) {
            return mTruthTableCollection.get(column, row);
        } else {
            return null;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int desiredWidth = getDesiredWidth();
        final int desiredHeight = getDesiredHeight();

        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        final int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        //Measure Width
        if (widthMode == MeasureSpec.EXACTLY) {
            //Must be this size
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            width = Math.min(desiredWidth, widthSize);
        } else {
            //Be whatever you want
            width = desiredWidth;
        }

        //Measure Height
        if (heightMode == MeasureSpec.EXACTLY) {
            //Must be this size
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            height = Math.min(desiredHeight, heightSize);
        } else {
            //Be whatever you want
            height = desiredHeight;
        }

        setMeasuredDimension(width, height);
    }

    private int getDesiredHeight() {
        final int maximumKMapSize = mTruthTableCollection.getMaximumKMapSize();
        return  mCellSizeInPx * (maximumKMapSize + 1/*header*/) + mBorderOffsetInPx * 2 + getPaddingBottom();
    }

    private int getDesiredWidth() {
        final int maximumVariables = mTruthTableCollection.getMaximumVariables();
        final int numberOfMaps = mTruthTableCollection.getNumberOfMaps();
        return mValueCellSizeInPx + mCellSizeInPx * (maximumVariables + numberOfMaps) + mBorderOffsetInPx * 2 + getPaddingRight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        final int paddingLeft = getPaddingLeft();
        final int paddingTop = getPaddingTop();

        final int maximumKMapSize = mTruthTableCollection.getMaximumKMapSize();
        final int maximumVariables = mTruthTableCollection.getMaximumVariables();
        final int numberOfMaps = mTruthTableCollection.getNumberOfMaps();

        final int leftGridPosition = paddingLeft + mBorderOffsetInPx;
        final int topGridPosition = paddingTop + mBorderOffsetInPx;

        final int columnSize = maximumVariables + numberOfMaps;
        final int rowSize = maximumKMapSize + 1/*header*/;

        // ---x
        // |
        // y
        // draw lines
        canvas.drawLine(leftGridPosition, topGridPosition, leftGridPosition, mCellSizeInPx * rowSize + topGridPosition, mLinePaint); // draw first column
        for (int columnIndex = 0; columnIndex <= columnSize; columnIndex++) { // draw rest of columns
            final int x = columnIndex * mCellSizeInPx + mValueCellSizeInPx + leftGridPosition;
            canvas.drawLine(x, topGridPosition, x, mCellSizeInPx * rowSize + topGridPosition,
                    columnIndex == 0 || columnIndex == maximumVariables ? mThickLinePaint : mLinePaint);
        }
        for (int rowIndex = 0; rowIndex <= rowSize; rowIndex++) { // draw rows
            final int y = rowIndex * mCellSizeInPx + topGridPosition;
            canvas.drawLine(leftGridPosition, y, mCellSizeInPx * columnSize + mValueCellSizeInPx + leftGridPosition, y,
                    rowIndex == 1 ? mThickLinePaint : mLinePaint);
        }

        // draw hash sign
        canvas.drawText(mSpannedHashSign, 0, mSpannedHashSign.length(),
                leftGridPosition + mValueCellSizeInPx / 2,
                topGridPosition + mCellSizeInPx / 2 - ((mCellValuePaint.descent() + mCellValuePaint.ascent()) / 2),
                mCellValuePaint);

        // prepare reference points for texts
        final float valueBaseX = leftGridPosition + mValueCellSizeInPx / 2;
        final float valueBaseY = topGridPosition + mCellSizeInPx + mCellSizeInPx / 2 - ((mCellValuePaint.descent() + mCellValuePaint.ascent()) / 2);
        final float variableLabelBaseX = leftGridPosition + mValueCellSizeInPx; @SuppressWarnings("redundant")
        final float variableLabelBaseY = topGridPosition;
        final float variableBitBaseX = variableLabelBaseX + mCellSizeInPx / 2;
        final float variableBitBaseY = variableLabelBaseY + mCellSizeInPx + mCellSizeInPx / 2 - ((mCellValuePaint.descent() + mCellValuePaint.ascent()) / 2);
        final float mapLabelBaseX = variableLabelBaseX + mCellSizeInPx * mTruthTableCollection.getMaximumVariables(); @SuppressWarnings("redundant")
        final float mapLabelBaseY = variableLabelBaseY;
        final float mapBitBaseX = mapLabelBaseX + mCellSizeInPx / 2;
        final float mapBitBaseY = mapLabelBaseY + mCellSizeInPx + mCellSizeInPx / 2 - ((mCellValuePaint.descent() + mCellValuePaint.ascent()) / 2);

        // draw values
        int valueCounter = 0;
        for (float yCoord = valueBaseY; yCoord < maximumKMapSize * mCellSizeInPx + valueBaseY; yCoord += mCellSizeInPx) {
            canvas.drawText(
                    String.valueOf(valueCounter++),
                    valueBaseX,
                    yCoord,
                    mCellValuePaint);
        }

        // draw variable labels
        canvas.save();
        canvas.translate(variableLabelBaseX, variableLabelBaseY);
        for (int index = maximumVariables - 1; index >= 0; index--) {
            mVariableLabelBuilder.replace(1, mVariableLabelBuilder.length(), String.valueOf(index));
            mVariableLabelLayout.draw(canvas);
            canvas.translate(mCellSizeInPx, 0);
        }
        canvas.restore();

        // draw map labels
        final List<KMapCollection> kMapCollections = mTruthTableCollection.getKMapCollections(); // ordered by title
        canvas.save();
        canvas.translate(mapLabelBaseX, mapLabelBaseY);
        for (int index = 0; index < numberOfMaps; index++) {
            mMapLabelBuilder.replace(1, mVariableLabelBuilder.length(), kMapCollections.get(index).getTitle().substring(1));
            mMapLabelLayout.draw(canvas);
            canvas.translate(mCellSizeInPx, 0);
        }
        canvas.restore();

        // draw variable bits
        valueCounter = 0;
        for (float yCoord = variableBitBaseY; yCoord < maximumKMapSize * mCellSizeInPx + variableBitBaseY; yCoord += mCellSizeInPx) {
            int bitCounter = maximumVariables - 1;
            for (float xCoord = variableBitBaseX; xCoord < maximumVariables * mCellSizeInPx + variableBitBaseX; xCoord += mCellSizeInPx) {
                canvas.drawText(
                        BitOperationUtil.isNthBitSet(valueCounter, bitCounter--) ? VALUE_1_STRING : VALUE_0_STRING,
                        xCoord,
                        yCoord,
                        mCellValuePaint);
            }
            valueCounter++;
        }

        // draw map bits
        int collectionCounter = 0;
        for (float xCoord = mapBitBaseX; xCoord < numberOfMaps * mCellSizeInPx + mapBitBaseX; xCoord += mCellSizeInPx) {

            final KMapCollection kMapCollection = kMapCollections.get(collectionCounter++);
            final int collectionSize = kMapCollection.getSize();
            final ArrayList<KMapCell> kMapCellList = kMapCollection.getKMapCellList();
            int cellCounter = 0;

            for (float yCoord = mapBitBaseY; yCoord < maximumKMapSize * mCellSizeInPx + mapBitBaseY; yCoord += mCellSizeInPx) {

                canvas.drawText(
                        cellCounter < collectionSize ? kMapCellList.get(cellCounter++).toString() : INVALID_POSITION,
                        xCoord,
                        yCoord,
                        mCellValuePaint);
            }
            valueCounter++;
        }
    }

    public void onKMapAddition(@NonNull KMapCollection collection) {
        mTruthTableCollection.onKMapAddition(collection);
        requestLayout();
    }

    public void onKMapRemoval(KMapCollection collection) {
        mTruthTableCollection.onKMapRemoval(collection);
        requestLayout();
    }

    public boolean isEmpty() {
        return mTruthTableCollection.isEmpty();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        mTruthTableCollection.removeOnTapListener();
    }
}
