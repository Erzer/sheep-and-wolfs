package com.polonium.sheepandwolfes.entity.player;

import java.util.TreeSet;

import com.polonium.sheepandwolfes.entity.field.GameField;
import com.polonium.sheepandwolfes.entity.field.Node;
import com.polonium.sheepandwolfes.entity.game.GameState;
import com.polonium.sheepandwolfes.views.GameFieldView.OnFieldTouch;

public class WolfsPlayer implements Player {

    private GameField gameField = new GameField();
    private GameState currentState;
    private int currentSelectedCell = -1;
    private TreeSet<Integer> possibleMoves;

    private OnMakeMoveListener makeMoveListener;

    public WolfsPlayer() {
    }

    @Override
    public OnFieldTouch getFieldTouchListener() {
        return listener;
    }
    
    private boolean mGameOver = false;
    @Override
    public void gameOver(boolean gameOver) {
        mGameOver = gameOver;
    }
    
    private OnFieldTouch listener = new OnFieldTouch() {

        @Override
        public TreeSet<Integer> onCellTouch(int cell) {
            if (currentState == null) {
                return null;
            }
            if (currentState.wolfPositions.contains(cell)) {
                currentSelectedCell = cell;
                TreeSet<Integer> res = new TreeSet<Integer>();
                for (Node node : gameField.getNodes().get(cell).getNear()) {
                    if (!currentState.wolfPositions.contains(node.getId()) && node.getId() != currentState.sheepPos
                        && cell > node.getId()) {
                        res.add(node.getId());
                    }
                }
                possibleMoves = res;
                return res;
            }
            if (currentSelectedCell >= 0 && possibleMoves != null) {
                if (possibleMoves.contains(cell)) {
                    currentState.wolfPositions.remove(currentSelectedCell);
                    currentState.wolfPositions.add(cell);
                    currentState.lastMove = GameState.WOLFS;
                    if (makeMoveListener != null && !mGameOver) makeMoveListener.onMoveComlete(WolfsPlayer.this, currentState);
                }
            }
            return null;
        }
    };

    @Override
    public WolfsPlayer makeMove(GameState gameState, OnMakeMoveListener listener) {
        makeMoveListener = listener;
        currentState = gameState;
        return this;
    }
    
}
