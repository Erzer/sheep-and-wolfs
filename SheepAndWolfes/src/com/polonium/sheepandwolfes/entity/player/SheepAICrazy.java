package com.polonium.sheepandwolfes.entity.player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

import android.os.AsyncTask;

import com.polonium.sheepandwolfes.entity.field.GameField;
import com.polonium.sheepandwolfes.entity.field.Node;
import com.polonium.sheepandwolfes.entity.game.GameState;
import com.polonium.sheepandwolfes.views.GameFieldView.OnFieldTouch;

public class SheepAICrazy implements Player {

    private GameField gameField = new GameField();
    private GameState currentState;
    private OnMakeMoveListener makeMoveListener;
    
    public SheepAICrazy() {
    }

    @Override
    public SheepAICrazy makeMove(GameState gameState, OnMakeMoveListener listener) {
        makeMoveListener = listener;
        currentState = gameState;
        new Thinker().execute();
        return this;
    }

    @Override
    public OnFieldTouch getFieldTouchListener() {
        return null;
    }
  
    class Thinker extends AsyncTask<Void, Void, GameState> {

        @Override
        protected GameState doInBackground(Void... params) {
            TreeSet<Node> near = gameField.getNodes().get(currentState.sheepPos).getNear();
            ArrayList<Integer> possibleMoves = new ArrayList<Integer>();
            Iterator<Node> iterator = near.iterator();
            while (iterator.hasNext()) {
                Node node = (Node) iterator.next();
                if (!currentState.wolfPositions.contains(node.getId())){
                    possibleMoves.add(node.getId());
                }
            }
            int movePos = (int) (Math.random() * (double)possibleMoves.size());
            int counter = 0;
            for (Integer pos : possibleMoves) {
                if (counter == movePos){
                    currentState.sheepPos = pos;
                    currentState.lastMove = GameState.SHEEP;
                    return currentState;
                }
                counter++;
            }
            return currentState;
        }

        @Override
        protected void onPostExecute(GameState result) {
           if (!mGameOver) makeMoveListener.onMoveComlete(SheepAICrazy.this, result);
        }

    }
    
    private boolean mGameOver = false;
    @Override
    public void gameOver(boolean gameOver) {
        mGameOver = gameOver;
    }
}
