package com.polonium.sheepandwolfes.views;

import java.util.TreeSet;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.animation.AnimationUtils;

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

    private float mSelectedX;
    private float mSelectedY;
    private boolean isDraging;
    private OnAnimationListener onAnimationListener;
    private MoveAnimation currentAnimation;

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

    public OnAnimationListener getOnAnimationListener() {
        return onAnimationListener;
    }

    public void setOnAnimationListener(OnAnimationListener onAnimationListener) {
        this.onAnimationListener = onAnimationListener;
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
        // Log.i("canvas", "width = " + canvas.getWidth() + " height = " + canvas.getHeight());
        // Log.i("measured", "width = " + getMeasuredWidth() + " height = " + getMeasuredHeight());
        drawField(canvas, getMeasuredWidth(), getMeasuredHeight());
        drawSheep(canvas);
        drawWofes(canvas);
        drawSelectedCell(canvas);
    }

    private void drawSelectedCell(Canvas canvas) {
        if (mPossibleMoves != null) {
            for (Integer cell : mPossibleMoves) {
                canvas.drawCircle(getCenterX(cell),
                                  getCenterY(cell),
                                  mRadius - TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                                                                      4,
                                                                      getResources().getDisplayMetrics()),
                                  possibleMovePaint);
            }
        }

        if (mSelectedCell >= 0) {
            canvas.drawCircle(mSelectedX,
                              mSelectedY,
                              mRadius - TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                                                                  4,
                                                                  getResources().getDisplayMetrics()),
                              (gameState.lastMove == GameState.WOLFS) ? sheepPaint : wolfPaint);
            canvas.drawCircle(mSelectedX,
                              mSelectedY,
                              mRadius - TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                                                                  4,
                                                                  getResources().getDisplayMetrics()),
                              possibleMovePaint);
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();

        int touchedCell = calculateTouchedCell(event);
        switch (action) {
        case MotionEvent.ACTION_DOWN:
            if (mFieldTouchListener != null) {
                TreeSet<Integer> moves = mFieldTouchListener.onCellTouch(touchedCell);
                if (moves != null) {
                    mPossibleMoves = moves;
                    mSelectedCell = touchedCell;
                    calcCursorPos(event);
                    isDraging = true;
                }
            }
            break;
        case MotionEvent.ACTION_MOVE:
            if (isDraging) calcCursorPos(event);
            break;
        case MotionEvent.ACTION_UP:
        case MotionEvent.ACTION_CANCEL:
            if (isDraging) {
                mFieldTouchListener.onCellTouch(getNearestCell(event));
            }
            isDraging = false;
            break;
        }
        postInvalidate();
        return true;
    }

    private void calcCursorPos(MotionEvent event) {
        if (mPossibleMoves != null) {
            int nearestCell = getNearestCell(event);

            float centerX = getCenterX(mSelectedCell);
            float centerY = getCenterY(mSelectedCell);

            float nearestCellX = getCenterX(nearestCell);
            float nearestCellY = getCenterY(nearestCell);
            float maxR = (float) Math.sqrt((centerX - nearestCellX) * (centerX - nearestCellX)
                                           + (centerY - nearestCellY)
                                           * (centerY - nearestCellY));
            float dx = event.getX() - centerX;
            float dy = event.getY() - centerY;
            if (Math.abs(dx) <= Math.abs(dy)) {
                mSelectedX = centerX + Math.abs(dx) * Math.signum(nearestCellX - centerX);
                mSelectedY = centerY + Math.abs(dx) * Math.signum(nearestCellY - centerY);
            } else {
                mSelectedX = centerX + Math.abs(dy) * Math.signum(nearestCellX - centerX);
                mSelectedY = centerY + Math.abs(dy) * Math.signum(nearestCellY - centerY);
            }
            if (dy * (nearestCellY - centerY) < 0 || dx * (nearestCellX - centerX) < 0) {
                mSelectedX = centerX;
                mSelectedY = centerY;
            }
            if (maxR < (float) Math.sqrt((centerX - mSelectedX) * (centerX - mSelectedX)
                                         + (centerY - mSelectedY)
                                         * (centerY - mSelectedY))) {
                mSelectedX = nearestCellX;
                mSelectedY = nearestCellY;
            }
        }
    }

    private int getNearestCell(MotionEvent event) {
        int nearestCell = mSelectedCell;
        float centerX = getCenterX(mSelectedCell);
        float centerY = getCenterY(mSelectedCell);
        float touchX = event.getX();
        float touchY = event.getY();
        float minR = (float) Math.sqrt((touchX - centerX) * (touchX - centerX)
                                       + (touchY - centerY)
                                       * (touchY - centerY));

        for (Integer cell : mPossibleMoves) {
            float cellX = getCenterX(cell);
            float cellY = getCenterY(cell);
            float r = (float) Math.sqrt((touchX - cellX) * (touchX - cellX) + (touchY - cellY) * (touchY - cellY));
            if (minR > r) {
                minR = r;
                nearestCell = cell;
            }
        }
        return nearestCell;
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
                if (wolfPos != mSelectedCell) {
                    canvas.drawCircle(getCenterX(wolfPos),
                                      getCenterY(wolfPos),
                                      mRadius - TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                                                                          4,
                                                                          getResources().getDisplayMetrics()),
                                      wolfPaint);
                }
            }
        }
    }

    private void drawSheep(Canvas canvas) {
        if (gameState != null && gameState.sheepPos != mSelectedCell) {
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
            canvas.drawCircle(x, y, mRadius, fieldPaint);
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
        gameState = state;
        mPossibleMoves = null;
        mSelectedCell = -1;
        if (currentAnimation != null) currentAnimation.cancel();
        postInvalidate();
    }

    private int getSelectedCell(GameState oldState, GameState newState) {
        if (oldState.sheepPos != newState.sheepPos) return oldState.sheepPos;
        for (Integer oldWolf : oldState.wolfPositions) {
            if (!newState.wolfPositions.contains(oldWolf)) {
                return oldWolf;
            }
        }
        return -1;
    }

    private int getSelectedCellDestination(GameState oldState, GameState newState) {
        if (oldState.sheepPos != newState.sheepPos) return newState.sheepPos;
        for (Integer newWolf : newState.wolfPositions) {
            if (!oldState.wolfPositions.contains(newWolf)) {
                return newWolf;
            }
        }
        return -1;
    }
    
    class MoveAnimation implements Runnable {
        private GameState oldState, newState;
        private int time;
        private long startTime;
        private boolean isCanceled = false;
        private long lastTime;
        private float speedX;
        private float speedY;

        public MoveAnimation(GameState oldState, GameState newState) {
            this.oldState = oldState;
            this.newState = newState;
            lastTime = startTime = System.currentTimeMillis();
            mSelectedCell = getSelectedCell(oldState, newState);
            int selectedCellDestination = getSelectedCellDestination(oldState, newState);
            mSelectedX = getCenterX(mSelectedCell);
            mSelectedY = getCenterY(mSelectedCell);

            float destX = getCenterX(selectedCellDestination);
            float destY = getCenterY(selectedCellDestination);

            time = 100;
            speedX = (destX - mSelectedX) / time;
            speedY = (destY - mSelectedY) / time;
        }

        public void cancel() {
            isCanceled = true;
        }

        @Override
        public void run() {
           
            long deltaTime = System.currentTimeMillis() - lastTime;
            lastTime = System.currentTimeMillis();
            mSelectedX += speedX * deltaTime;
            mSelectedY += speedY * deltaTime;

            if (System.currentTimeMillis() - startTime < time && !isCancelled()) {
                postDelayed(this, 0);
            } else {
                if (!isCancelled()) {
                    setState(newState);
                    if (getOnAnimationListener() != null) getOnAnimationListener().onAnimationComplete();
                }
            }
            invalidate();
        }

        private boolean isCancelled() {
            return isCanceled;
        }
    }

    public void morphToState(GameState state) {
        removeCallbacks(currentAnimation);
        currentAnimation = new MoveAnimation(this.gameState, state);
        post(currentAnimation);
    }
}
