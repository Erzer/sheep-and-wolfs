package com.polonium.sheepandwolfes.views;

import java.util.TreeSet;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;

import com.polonium.sheepandwolfes.entity.field.GameField;
import com.polonium.sheepandwolfes.entity.field.Node;
import com.polonium.sheepandwolfes.entity.game.GameState;

public class GameFieldView extends View {

    Paint fieldPaint = new Paint();
    Paint sheepPaint = new Paint();
    Paint wolfPaint = new Paint();
    Paint strokePaint = new Paint();
    Paint possibleMovePaint = new Paint();

    GameField gameField = new GameField();
    private GameState gameState = new GameState(1, 28, 29, 30, 31);

    private float mRadius;

    private OnFieldTouch mFieldTouchListener;
    private TreeSet<Integer> mPossibleMoves;
    private int mSelectedCell = -1;

    public interface OnFieldTouch {
        TreeSet<Integer> onCellTouch(int cell);
    }

    public GameFieldView(Context context) {
        super(context);
        init();
    }

    public GameFieldView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GameFieldView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        fieldPaint.setColor(0xff212021);
        fieldPaint.setAntiAlias(true);
        fieldPaint.setStrokeWidth(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                                                            2,
                                                            getResources().getDisplayMetrics()));
        sheepPaint.setColor(0xffffbb33);
        sheepPaint.setAntiAlias(true);

        wolfPaint.setColor(0xff33b5e5);
        wolfPaint.setAntiAlias(true);

        strokePaint.setColor(0xffeeeeee);
        strokePaint.setAntiAlias(true);
        strokePaint.setStrokeWidth(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                                                             1f,
                                                             getResources().getDisplayMetrics()));
        strokePaint.setStyle(Paint.Style.STROKE);

        possibleMovePaint.setColor(0x66ffffff);
        possibleMovePaint.setAntiAlias(true);
        possibleMovePaint.setColorFilter(new PorterDuffColorFilter(0x33fffff0, Mode.LIGHTEN));
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public void setFieldTouchListener(OnFieldTouch mFieldTouchListener) {
        this.mFieldTouchListener = mFieldTouchListener;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.i("canvas", "width = " + canvas.getWidth() + " height = " + canvas.getHeight());
        Log.i("measured", "width = " + getMeasuredWidth() + " height = " + getMeasuredHeight());
        drawField(canvas, getMeasuredWidth(), getMeasuredHeight());
        drawSheep(canvas);
        drawWofes(canvas);
        drawSelectedCell(canvas);
    }

    private void drawSelectedCell(Canvas canvas) {
        if (mSelectedCell >= 0) {
            canvas.drawCircle(getCenterX(mSelectedCell),
                              getCenterY(mSelectedCell),
                              mRadius - TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                                                                  4,
                                                                  getResources().getDisplayMetrics()),
                              possibleMovePaint);
        }

        if (mPossibleMoves != null) {
            for (Integer cell : mPossibleMoves) {
                canvas.drawCircle(getCenterX(cell),
                                  getCenterY(cell),
                                  mRadius- TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                                                                     4,
                                                                     getResources().getDisplayMetrics()),
                                  possibleMovePaint);
            }
        }
    }

    private VelocityTracker mVelocityTracker = null;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // int index = event.getActionIndex();
        int action = event.getActionMasked();
        // int pointerId = event.getPointerId(index);

        int touchedCell = calculateTouchedCell(event);
        switch (action) {
        case MotionEvent.ACTION_DOWN:
            if (mVelocityTracker == null) {
                // Retrieve a new VelocityTracker object to watch the velocity of a motion.
                mVelocityTracker = VelocityTracker.obtain();
            } else {
                // Reset the velocity tracker back to its initial state.
                mVelocityTracker.clear();
            }
            // Add a user's movement to the tracker.
            mVelocityTracker.addMovement(event);
            Log.d("", "Cell = " + touchedCell);

            if (mFieldTouchListener != null) {
                TreeSet<Integer> moves = mFieldTouchListener.onCellTouch(touchedCell);
                if (moves != null) {
                    mPossibleMoves = moves;
                    mSelectedCell = touchedCell;
                }
                postInvalidate();
            }

            break;
        case MotionEvent.ACTION_MOVE:
            mVelocityTracker.addMovement(event);
            // When you want to determine the velocity, call
            // computeCurrentVelocity(). Then call getXVelocity()
            // and getYVelocity() to retrieve the velocity for each pointer ID.
            mVelocityTracker.computeCurrentVelocity(1000);
            // Log velocity of pixels per second
            // Best practice to use VelocityTrackerCompat where possible.
            // Log.d("", "X velocity: " +
            // VelocityTrackerCompat.getXVelocity(mVelocityTracker,
            // pointerId));
            // Log.d("", "Y velocity: " +
            // VelocityTrackerCompat.getYVelocity(mVelocityTracker,
            // pointerId));
            break;
        case MotionEvent.ACTION_UP:
        case MotionEvent.ACTION_CANCEL:
            // Return a VelocityTracker object back to be re-used by others.
            mVelocityTracker.recycle();
            break;
        }
        return true;
    }

    private int calculateTouchedCell(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        for (Node node : gameField.getNodes()) {
            float nodeX = getCenterX(node.getId());
            float nodeY = getCenterY(node.getId());
            if (Math.sqrt((x - nodeX) * (x - nodeX) + (y - nodeY) * (y - nodeY)) <= mRadius) {
                return node.getId();
            }
        }
        return -1;
    }

    private void drawWofes(Canvas canvas) {
        if (gameState != null) {
            for (int wolfPos : gameState.wolfPositions) {
                canvas.drawCircle(getCenterX(wolfPos),
                                  getCenterY(wolfPos),
                                  mRadius - TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                                                                      4,
                                                                      getResources().getDisplayMetrics()),
                                  wolfPaint);
            }
        }
    }

    private void drawSheep(Canvas canvas) {
        if (gameState != null) {
            canvas.drawCircle(getCenterX(gameState.sheepPos),
                              getCenterY(gameState.sheepPos),
                              mRadius - TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                                                                  4,
                                                                  getResources().getDisplayMetrics()),
                              sheepPaint);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int min = Math.min(getMeasuredHeight(), getMeasuredWidth());
        setMeasuredDimension(min, min);
    }

    public void drawField(Canvas canvas, int width, int height) {
        int w = Math.min(width, height);
        mRadius = (float) w / 16;
        for (Node node : gameField.getNodes()) {
            float x = getNodeCenterX(node);
            float y = getNodeCenterY(node);
            canvas.drawCircle(x, y, mRadius,
                              /*mPossibleMoves != null ? (mPossibleMoves.contains(node.getId()) ? possibleMovePaint : fieldPaint) :*/ fieldPaint);
            for (Node near : node.getNear()) {
                canvas.drawLine(getNodeCenterX(node),
                                getNodeCenterY(node),
                                getNodeCenterX(near),
                                getNodeCenterY(near),
                                fieldPaint);
            }
        }
    }

    private float getNodeCenterY(Node node) {
        return ((node.getId() / 4) * mRadius + mRadius / 2) * 2;
    }

    private float getNodeCenterX(Node node) {
        return ((node.getId() % 4) * mRadius + mRadius / 2 + ((node.getId() / 4) % 2) * mRadius / 2) * 4 - mRadius;
    }

    private float getCenterY(int id) {
        return ((id / 4) * mRadius + mRadius / 2) * 2;
    }

    private float getCenterX(int id) {
        return ((id % 4) * mRadius + mRadius / 2 + ((id / 4) % 2) * mRadius / 2) * 4 - mRadius;
    }

    public void setState(GameState state) {
        this.gameState = state;
        mPossibleMoves = null;
        mSelectedCell = -1;
        postInvalidate();
    }
}
