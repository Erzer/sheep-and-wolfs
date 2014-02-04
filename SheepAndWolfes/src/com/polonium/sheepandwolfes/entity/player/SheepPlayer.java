package com.polonium.sheepandwolfes.entity.player;

import java.util.TreeSet;

import com.polonium.sheepandwolfes.entity.field.GameField;
import com.polonium.sheepandwolfes.entity.field.Node;
import com.polonium.sheepandwolfes.entity.game.GameState;
import com.polonium.sheepandwolfes.views.GameFieldView.OnFieldTouch;

public class SheepPlayer implements Player{
    
    private GameField gameField = new GameField();
    private GameState currentState;
    private int currentSelectedCell = -1;
    private TreeSet<Integer> possibleMoves;
    private OnMakeMoveListener makeMoveListener;

    public SheepPlayer() {
    }

    @Override
    public SheepPlayer makeMove(GameState gameState, OnMakeMoveListener listener) {
        makeMoveListener = listener;
        currentState = gameState;
        return this;
    }

    @Override
    public OnFieldTouch getFieldTouchListener() {
        return listener;
    }
 
    public GameField getGameField() {
        return gameField;
    }

    public void setGameField(GameField gameField) {
        this.gameField = gameField;
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
                new Exception("Game state not set!");
                return null;
            }
            if (currentState.sheepPos == cell){
                currentSelectedCell = cell;
                TreeSet<Integer> res = new TreeSet<Integer>();
                for (Node node: gameField.getNodes().get(cell).getNear()){
                    if (!currentState.wolfPositions.contains(node.getId()) && node.getId() != currentState.sheepPos){
                        res.add(node.getId());
                    }
                }
                possibleMoves = res;
                return res;
            }
            if (currentSelectedCell >=0 && possibleMoves != null){
                if (possibleMoves.contains(cell)){
                    currentState.sheepPos = cell;
                    currentState.lastMove = GameState.SHEEP;
                    if (makeMoveListener != null && !mGameOver) makeMoveListener.onMoveComlete(SheepPlayer.this, currentState);
                }
            }
            return null;
        }
    };
    
}
