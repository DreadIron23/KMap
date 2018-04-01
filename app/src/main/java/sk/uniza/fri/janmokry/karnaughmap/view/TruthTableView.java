package sk.uniza.fri.janmokry.karnaughmap.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;

import sk.uniza.fri.janmokry.karnaughmap.R;
import sk.uniza.fri.janmokry.karnaughmap.util.GraphicsUtil;

/**
 * Created by Janci on 30.3.2018.
 */

public class TruthTableView extends View {

    private final static String TAG = KMapView.class.getSimpleName();

    private static final int CELL_SIZE_IN_DP = 32; // TODO make settable from XML

    private int mCellSizeInPx;

    private Context mContext;
    private Paint mLinePaint = new Paint();
    private Paint mCellValuePaint = new Paint();

    private final int STROKE_WIDTH = 4; // TODO make settable from XML

    public TruthTableView(Context context) {
        super(context);

        init(context);
    }

    public TruthTableView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    public TruthTableView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public TruthTableView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init(context);
    }

    private void init(Context context) {
        mContext = context;
        final Resources resources = context.getResources();

        mCellSizeInPx = GraphicsUtil.dpToPx(resources, CELL_SIZE_IN_DP);

        mLinePaint.setColor(GraphicsUtil.fetchColor(mContext, R.attr.colorPrimary));
        mLinePaint.setStrokeWidth(STROKE_WIDTH); // TODO make settable from XML
        mLinePaint.setStrokeJoin(Paint.Join.ROUND);
        mLinePaint.setStrokeCap(Paint.Cap.ROUND);
        mLinePaint.setAntiAlias(true);

        mCellValuePaint.setTextSize(mCellSizeInPx / 1.3f);
        mCellValuePaint.setTextAlign(Paint.Align.CENTER);
        mCellValuePaint.setAntiAlias(true);
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) { // TODO
//        int x = (int) event.getX();
//        int y = (int) event.getY();
//
//        switch (event.getAction()) {
//            case (MotionEvent.ACTION_DOWN):
//                mTappedCell = getCellAtPoint(x, y);
//                mActionDownRawPosX = (int) event.getRawX();
//                mActionDownRawPosY = (int) event.getRawY();
//                return true;
//            case (MotionEvent.ACTION_MOVE):
//                return false;
//            case MotionEvent.ACTION_UP:
//                final KMapCell actionUpCell = getCellAtPoint(x, y);
//                if (actionUpCell == mTappedCell && mTappedCell != null && isItTheSameSpot(event)) { // only flipping when tapped
//                    mTappedCell.tapped();
//                    invalidate();
//                    return true;
//                }
//        }
//
//        return super.onTouchEvent(event);
//    }

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        final int desiredWidth = getDesiredWidth();
//        final int desiredHeight = getDesiredHeight();
//
//        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
//        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
//        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
//        final int heightSize = MeasureSpec.getSize(heightMeasureSpec);
//
//        int width;
//        int height;
//
//        //Measure Width
//        if (widthMode == MeasureSpec.EXACTLY) {
//            //Must be this size
//            width = widthSize;
//        } else if (widthMode == MeasureSpec.AT_MOST) {
//            //Can't be bigger than...
//            width = Math.min(desiredWidth, widthSize);
//        } else {
//            //Be whatever you want
//            width = desiredWidth;
//        }
//
//        //Measure Height
//        if (heightMode == MeasureSpec.EXACTLY) {
//            //Must be this size
//            height = heightSize;
//        } else if (heightMode == MeasureSpec.AT_MOST) {
//            //Can't be bigger than...
//            height = Math.min(desiredHeight, heightSize);
//        } else {
//            //Be whatever you want
//            height = desiredHeight;
//        }
//
//        setMeasuredDimension(width, height);
//    }
//
//    private int getDesiredHeight() {
//        final int variableWidth = mCellSizeInPx / 3;
//        final int topVariablesWidth = variableWidth * (mKMapCollection.getNumberOfColumnVariables() + 1);
//        final int topGridPosition = getPaddingTop() + topVariablesWidth;
//        final int rowSize = mKMapCollection.getRowSize();
//        final int textWidth = (int) mVariableTextPaint.measureText("  X8");
//        return mCellSizeInPx * rowSize + topGridPosition + getPaddingBottom() + textWidth;
//    }
//
//    private int getDesiredWidth() {
//        final int variableWidth = mCellSizeInPx / 3;
//        final int leftVariablesWidth = variableWidth * (mKMapCollection.getNumberOfRowVariables() + 1);
//        final int leftGridPosition = getPaddingLeft() + leftVariablesWidth;
//        final int columnSize = mKMapCollection.getColumnSize();
//        final int textWidth = (int) mVariableTextPaint.measureText("  X8");
//        return mCellSizeInPx * columnSize + leftGridPosition + getPaddingRight() + textWidth;
//    }
//
//    @Override
//    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//
//        final int paddingLeft = getPaddingLeft();
//        final int paddingTop = getPaddingTop();
//
//        final int variableWidth = mCellSizeInPx / 3;
//        final int leftVariablesWidth = variableWidth * (mKMapCollection.getNumberOfRowVariables() + 1);
//        final int topVariablesWidth = variableWidth * (mKMapCollection.getNumberOfColumnVariables() + 1);
//
//        final int leftGridPosition = paddingLeft + leftVariablesWidth;
//        final int topGridPosition = paddingTop + topVariablesWidth;
//
//        final int columnSize = mKMapCollection.getColumnSize();
//        final int rowSize = mKMapCollection.getRowSize();
//
//        // ---x
//        // |
//        // y
//        // draw lines
//        for (int columnIndex = 0; columnIndex <= columnSize; columnIndex++) {
//            final int x = columnIndex * mCellSizeInPx + leftGridPosition;
//            canvas.drawLine(x, topGridPosition, x, mCellSizeInPx * rowSize + topGridPosition, mLinePaint);
//        }
//        for (int rowIndex = 0; rowIndex <= rowSize; rowIndex++) {
//            final int y = rowIndex * mCellSizeInPx + topGridPosition;
//            canvas.drawLine(leftGridPosition, y, mCellSizeInPx * columnSize + leftGridPosition, y, mLinePaint);
//        }
//
//        // draw values
//        int x = 0;
//        for (int xCoord = leftGridPosition; xCoord < columnSize * mCellSizeInPx + leftGridPosition; xCoord += mCellSizeInPx) {
//            int y = 0;
//            for (int yCoord = topGridPosition; yCoord < rowSize * mCellSizeInPx + topGridPosition; yCoord += mCellSizeInPx) {
//                canvas.drawText(
//                        mKMapCollection.get(x, y++).toString(),
//                        xCoord + mCellSizeInPx / 2,
//                        yCoord + mCellSizeInPx / 2 - ((mCellValuePaint.descent() + mCellValuePaint.ascent()) / 2),
//                        mCellValuePaint);
//            }
//            x++;
//        }
//
//        // draw variables
//        final int[] topVariablePositions = getNumberOfVariablesPositions(mKMapCollection.getNumberOfColumnVariables());
//        int currentVariablePositionIndex = 0;
//        for (int yCoord = paddingTop + variableWidth; currentVariablePositionIndex < topVariablePositions.length; yCoord += variableWidth) {
//            final int currentVariablePosition = topVariablePositions[currentVariablePositionIndex++];
//            int currentVariableDash = mKMapCollection.getColumnSize() - 1;
//            for (int xCoord = leftGridPosition; xCoord < columnSize * mCellSizeInPx + leftGridPosition; xCoord += mCellSizeInPx) {
//                if (BitOperationUtil.isNthBitSet(currentVariablePosition, currentVariableDash--)) {
//                    canvas.drawLine(xCoord, yCoord, xCoord + mCellSizeInPx, yCoord, mLinePaint);
//                }
//            }
//        }
//        final int[] leftVariablePositions = getNumberOfVariablesPositions(mKMapCollection.getNumberOfRowVariables());
//        currentVariablePositionIndex = 0;
//        for (int xCoord = paddingLeft + variableWidth; currentVariablePositionIndex < leftVariablePositions.length; xCoord += variableWidth) {
//            final int currentVariablePosition = leftVariablePositions[currentVariablePositionIndex++];
//            int currentVariableDash = mKMapCollection.getRowSize() - 1;
//            for (int yCoord = topGridPosition; yCoord < rowSize * mCellSizeInPx + topGridPosition; yCoord += mCellSizeInPx) {
//                if (BitOperationUtil.isNthBitSet(currentVariablePosition, currentVariableDash--)) {
//                    canvas.drawLine(xCoord, yCoord, xCoord, yCoord + mCellSizeInPx, mLinePaint);
//                }
//            }
//        }
//
//        // draw variable labels
//        int variableCounter = 0;
//        int yCoord = paddingTop + variableWidth;
//        for (int variable : topVariablePositions) {
//            final int lowestOneBit = BitOperationUtil.findRightmostOnePosition(variable);
//            final int xCoord = (mKMapCollection.getColumnSize() - lowestOneBit) * mCellSizeInPx + leftGridPosition;
//            canvas.drawText(
//                    "X" + variableCounter,
//                    xCoord + variableWidth,
//                    yCoord - ((mVariableTextPaint.descent() + mVariableTextPaint.ascent()) / 2),
//                    mVariableTextPaint);
//            variableCounter += 2;
//            yCoord += variableWidth;
//        }
//
//        variableCounter = 1;
//        int xCoord = paddingLeft + variableWidth;
//        for (int variable : leftVariablePositions) {
//            final int lowestOneBit = BitOperationUtil.findRightmostOnePosition(variable);
//            yCoord = (mKMapCollection.getRowSize() - lowestOneBit) * mCellSizeInPx + topGridPosition;
//            canvas.drawText(
//                    "X" + variableCounter,
//                    xCoord,
//                    yCoord + variableWidth - ((mVariableTextPaint.descent() + mVariableTextPaint.ascent()) / 2),
//                    mVariableTextPaint);
//            variableCounter += 2;
//            xCoord += variableWidth;
//        }
//
//    }

}
