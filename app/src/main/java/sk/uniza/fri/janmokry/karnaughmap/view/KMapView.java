package sk.uniza.fri.janmokry.karnaughmap.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;

import java.util.List;

import sk.uniza.fri.janmokry.karnaughmap.R;
import sk.uniza.fri.janmokry.karnaughmap.fragment.KarnaughMapsFragment;
import sk.uniza.fri.janmokry.karnaughmap.kmap.ConfigurationShapes;
import sk.uniza.fri.janmokry.karnaughmap.kmap.KMapCell;
import sk.uniza.fri.janmokry.karnaughmap.kmap.KMapCollection;
import sk.uniza.fri.janmokry.karnaughmap.util.BitOperationUtil;
import sk.uniza.fri.janmokry.karnaughmap.util.GraphicsUtil;

/**
 * View representing Karnaugh Map.
 */
@SuppressLint("ViewConstructor")
public class KMapView extends View {

    // Graphically represents variables shown above and left of Karnaugh map.
    public static final int[] POSITIONS_OF_4_VARIABLES = new int[] { 0x6666, 0x3C3C, 0x0FF0, 0x00FF }; // leftmost is lowest bit
    public static final int[] POSITIONS_OF_3_VARIABLES = new int[] { 0x66, 0x3C, 0x0F };
    public static final int[] POSITIONS_OF_2_VARIABLES = new int[] { 0x6, 0x3 };
    public static final int[] POSITIONS_OF_1_VARIABLE = new int[] { 0x1 };
    public static final int[] POSITIONS_OF_0_VARIABLES = new int[] {};

    private final static String TAG = KMapView.class.getSimpleName();

    private static final int CELL_SIZE_IN_DP = 32;

    private int mCellSizeInPx;
    private KMapCollection mKMapCollection;
    private KarnaughMapsFragment.OnKMapConfigurationComputationTriggerListener mOnKMapConfigurationTriggerListener;

    private Context mContext;
    private Paint mLinePaint = new Paint();
    private Paint mCellValuePaint = new Paint();
    private Paint mVariableTextPaint = new Paint();

    @Nullable
    private KMapCell mTappedCell = null;
    private int mActionDownRawPosX = 0;
    private int mActionDownRawPosY = 0;
    private final int mActionDownPositionTolerance = 10;
    private final KMapCollection.OnTapListener mOnTapListener = cell -> {
        invalidate();
        fireUpConfigurationComputing();
    };

    private final int STROKE_WIDTH = 4;

    public KMapView(Context context, @NonNull KarnaughMapsFragment.OnKMapConfigurationComputationTriggerListener listener) {
        super(context);

        init(context, null, listener);
    }

    /**
     * @param providedKMapCollection used for loading for now
     */
    private KMapView(Context context, @Nullable KMapCollection providedKMapCollection,
                     @NonNull KarnaughMapsFragment.OnKMapConfigurationComputationTriggerListener listener) {
        super(context);

        init(context, providedKMapCollection, listener);
    }

    private void init(Context context, @Nullable KMapCollection providedKMapCollection,
                      @NonNull KarnaughMapsFragment.OnKMapConfigurationComputationTriggerListener listener) {
        mContext = context;
        mOnKMapConfigurationTriggerListener = listener;
        final Resources resources = context.getResources();

        mKMapCollection = providedKMapCollection == null ?
                new KMapCollection(4) : providedKMapCollection;
        mKMapCollection.registerOnTapListener(mOnTapListener);

        mCellSizeInPx = GraphicsUtil.dpToPx(resources, CELL_SIZE_IN_DP);

        mLinePaint.setColor(GraphicsUtil.fetchColor(mContext, R.attr.colorPrimary));
        mLinePaint.setStrokeWidth(STROKE_WIDTH);
        mLinePaint.setStrokeJoin(Paint.Join.ROUND);
        mLinePaint.setStrokeCap(Paint.Cap.ROUND);
        mLinePaint.setAntiAlias(true);

        mCellValuePaint.setTextSize(mCellSizeInPx / 1.3f);
        mCellValuePaint.setTextAlign(Paint.Align.CENTER);
        mCellValuePaint.setAntiAlias(true);
        mVariableTextPaint.setTextSize(mCellSizeInPx / 3f);
        mVariableTextPaint.setTextAlign(Paint.Align.CENTER);
        mVariableTextPaint.setAntiAlias(true);
    }

    public KMapCollection onSave() {
        return mKMapCollection;
    }

    public static KMapView onLoad(Context context, @NonNull KMapCollection kMapCollection,
                                  KarnaughMapsFragment.OnKMapConfigurationComputationTriggerListener listener) {
        return new KMapView(context, kMapCollection, listener);
    }

    public void addVariable() {
        mKMapCollection.incrementVariable();
        fireUpConfigurationComputing();
        requestLayout();
    }

    public void removeVariable() {
        mKMapCollection.decrementVariable();
        fireUpConfigurationComputing();
        requestLayout();
    }

    public KMapCollection getKMapCollection() {
        return mKMapCollection;
    }

    public void setTitle(String title) {
        mKMapCollection.setTitle(title);
    }

    public String getTitle() {
        return mKMapCollection.getTitle();
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

    private void fireUpConfigurationComputing() {
        mOnKMapConfigurationTriggerListener.onKMapConfigurationComputationTrigger(mKMapCollection);
    }

    private boolean isItTheSameSpot(MotionEvent event) {
        return (int) event.getRawX() < mActionDownRawPosX + mActionDownPositionTolerance
                && (int) event.getRawX() > mActionDownRawPosX - mActionDownPositionTolerance
                && (int) event.getRawY() < mActionDownRawPosY + mActionDownPositionTolerance
                && (int) event.getRawY() > mActionDownRawPosY - mActionDownPositionTolerance;
    }

    private @Nullable KMapCell getCellAtPoint(int x, int y) {
        final int variableWidth = mCellSizeInPx / 3;

        final int leftVariablesWidth = variableWidth * (mKMapCollection.getNumberOfRowVariables() + 1);
        final int topVariablesWidth = variableWidth * (mKMapCollection.getNumberOfColumnVariables() + 1);

        final int leftGridPosition = getPaddingLeft() + leftVariablesWidth;
        final int topGridPosition = getPaddingTop() + topVariablesWidth;

        int lx = x - leftGridPosition;
        int ly = y - topGridPosition;

        if (lx < 0 || ly < 0) { // if lx/ly is between -1 and negative mCellSizeInPx it would pass test
            return null;
        }

        int row = ly / mCellSizeInPx;
        int column = lx / mCellSizeInPx;

        if(mKMapCollection.isValidPosition(column, row)) {
            return mKMapCollection.get(column, row);
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
        final int variableWidth = mCellSizeInPx / 3;
        final int topVariablesWidth = variableWidth * (mKMapCollection.getNumberOfColumnVariables() + 1);
        final int topGridPosition = getPaddingTop() + topVariablesWidth;
        final int rowSize = mKMapCollection.getRowSize();
        final int textWidth = (int) mVariableTextPaint.measureText("  X8");
        return mCellSizeInPx * rowSize + topGridPosition + getPaddingBottom() + textWidth;
    }

    private int getDesiredWidth() {
        final int variableWidth = mCellSizeInPx / 3;
        final int leftVariablesWidth = variableWidth * (mKMapCollection.getNumberOfRowVariables() + 1);
        final int leftGridPosition = getPaddingLeft() + leftVariablesWidth;
        final int columnSize = mKMapCollection.getColumnSize();
        final int textWidth = (int) mVariableTextPaint.measureText("  X8");
        return mCellSizeInPx * columnSize + leftGridPosition + getPaddingRight() + textWidth;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        final int paddingLeft = getPaddingLeft();
        final int paddingTop = getPaddingTop();

        final int variableWidth = mCellSizeInPx / 3;
        final int leftVariablesWidth = variableWidth * (mKMapCollection.getNumberOfRowVariables() + 1);
        final int topVariablesWidth = variableWidth * (mKMapCollection.getNumberOfColumnVariables() + 1);

        final int leftGridPosition = paddingLeft + leftVariablesWidth;
        final int topGridPosition = paddingTop + topVariablesWidth;

        final int columnSize = mKMapCollection.getColumnSize();
        final int rowSize = mKMapCollection.getRowSize();

        // ---x
        // |
        // y
        // draw lines
        for (int columnIndex = 0; columnIndex <= columnSize; columnIndex++) {
            final int x = columnIndex * mCellSizeInPx + leftGridPosition;
            canvas.drawLine(x, topGridPosition, x, mCellSizeInPx * rowSize + topGridPosition, mLinePaint);
        }
        for (int rowIndex = 0; rowIndex <= rowSize; rowIndex++) {
            final int y = rowIndex * mCellSizeInPx + topGridPosition;
            canvas.drawLine(leftGridPosition, y, mCellSizeInPx * columnSize + leftGridPosition, y, mLinePaint);
        }

        // draw values
        int x = 0;
        for (int xCoord = leftGridPosition; xCoord < columnSize * mCellSizeInPx + leftGridPosition; xCoord += mCellSizeInPx) {
            int y = 0;
            for (int yCoord = topGridPosition; yCoord < rowSize * mCellSizeInPx + topGridPosition; yCoord += mCellSizeInPx) {
                final KMapCell kMapCell = mKMapCollection.get(x, y++);
                canvas.drawText(
                        kMapCell.toString(),
                        xCoord + mCellSizeInPx / 2,
                        yCoord + mCellSizeInPx / 2 - ((mCellValuePaint.descent() + mCellValuePaint.ascent()) / 2),
                        mCellValuePaint);
                final List<KMapCell.ConfigurationShape> shapes = kMapCell.getShapes();
                for (KMapCell.ConfigurationShape shape : shapes) {
                    final ConfigurationShapes shape1 = shape.shape;
                    final Drawable drawable = shape1.getDrawable(getContext());
                    if (drawable != null) {
                        drawable.setColorFilter(shape.color, PorterDuff.Mode.SRC_ATOP);
                        drawable.setBounds(xCoord, yCoord, xCoord + mCellSizeInPx, yCoord + mCellSizeInPx);
                        drawable.draw(canvas);
                    }
                }

            }
            x++;
        }

        // draw variables
        final int[] topVariablePositions = getNumberOfVariablesPositions(mKMapCollection.getNumberOfColumnVariables());
        int currentVariablePositionIndex = 0;
        for (int yCoord = paddingTop + variableWidth; currentVariablePositionIndex < topVariablePositions.length; yCoord += variableWidth) {
            final int currentVariablePosition = topVariablePositions[currentVariablePositionIndex++];
            int currentVariableDash = mKMapCollection.getColumnSize() - 1;
            for (int xCoord = leftGridPosition; xCoord < columnSize * mCellSizeInPx + leftGridPosition; xCoord += mCellSizeInPx) {
                if (BitOperationUtil.isNthBitSet(currentVariablePosition, currentVariableDash--)) {
                    canvas.drawLine(xCoord, yCoord, xCoord + mCellSizeInPx, yCoord, mLinePaint);
                }
            }
        }
        final int[] leftVariablePositions = getNumberOfVariablesPositions(mKMapCollection.getNumberOfRowVariables());
        currentVariablePositionIndex = 0;
        for (int xCoord = paddingLeft + variableWidth; currentVariablePositionIndex < leftVariablePositions.length; xCoord += variableWidth) {
            final int currentVariablePosition = leftVariablePositions[currentVariablePositionIndex++];
            int currentVariableDash = mKMapCollection.getRowSize() - 1;
            for (int yCoord = topGridPosition; yCoord < rowSize * mCellSizeInPx + topGridPosition; yCoord += mCellSizeInPx) {
                if (BitOperationUtil.isNthBitSet(currentVariablePosition, currentVariableDash--)) {
                    canvas.drawLine(xCoord, yCoord, xCoord, yCoord + mCellSizeInPx, mLinePaint);
                }
            }
        }

        // draw variable labels
        int variableCounter = 0;
        int yCoord = paddingTop + variableWidth;
        for (int variable : topVariablePositions) {
            final int lowestOneBit = BitOperationUtil.findRightmostOnePosition(variable);
            final int xCoord = (mKMapCollection.getColumnSize() - lowestOneBit) * mCellSizeInPx + leftGridPosition;
            canvas.drawText(
                    "X" + variableCounter,
                    xCoord + variableWidth,
                    yCoord - ((mVariableTextPaint.descent() + mVariableTextPaint.ascent()) / 2),
                    mVariableTextPaint);
            variableCounter += 2;
            yCoord += variableWidth;
        }

        variableCounter = 1;
        int xCoord = paddingLeft + variableWidth;
        for (int variable : leftVariablePositions) {
            final int lowestOneBit = BitOperationUtil.findRightmostOnePosition(variable);
            yCoord = (mKMapCollection.getRowSize() - lowestOneBit) * mCellSizeInPx + topGridPosition;
            canvas.drawText(
                    "X" + variableCounter,
                    xCoord,
                    yCoord + variableWidth - ((mVariableTextPaint.descent() + mVariableTextPaint.ascent()) / 2),
                    mVariableTextPaint);
            variableCounter += 2;
            xCoord += variableWidth;
        }

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        onRemoval();
    }

    public void onRemoval() {
        mKMapCollection.unregisterOnTapListener(mOnTapListener);
    }

    private int[] getNumberOfVariablesPositions(int numberOfVariables) {
        final int [] topVariablePositions;
        switch (numberOfVariables) {
            case 4:
                topVariablePositions = POSITIONS_OF_4_VARIABLES;
                break;
            case 3:
                topVariablePositions = POSITIONS_OF_3_VARIABLES;
                break;
            case 2:
                topVariablePositions = POSITIONS_OF_2_VARIABLES;
                break;
            case 1:
                topVariablePositions = POSITIONS_OF_1_VARIABLE;
                break;
            case 0:
                topVariablePositions = POSITIONS_OF_0_VARIABLES;
                break;
            default:
                throw new IllegalArgumentException("Unsupported number of variables: " + mKMapCollection.getNumberOfColumnVariables());
        }
        return topVariablePositions;
    }
}
