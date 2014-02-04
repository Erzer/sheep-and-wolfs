package com.polonium.sheepandwolfes.entity.player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

import android.os.AsyncTask;

import com.polonium.sheepandwolfes.entity.field.GameField;
import com.polonium.sheepandwolfes.entity.field.Node;
import com.polonium.sheepandwolfes.entity.game.GameState;
import com.polonium.sheepandwolfes.views.GameFieldView.OnFieldTouch;

public class UniversalAIMinimaxRandomBest implements Player {

    private GameField gameField = new GameField();
    private GameState currentState;
    private OnMakeMoveListener makeMoveListener;
    private int deep = 1;

    public UniversalAIMinimaxRandomBest(int deep) {
        this.deep = deep;
    }

    @Override
    public UniversalAIMinimaxRandomBest makeMove(GameState gameState, OnMakeMoveListener listener) {
        makeMoveListener = listener;
        currentState = gameState;
        new Thinker().execute();
        return this;
    }

    @Override
    public OnFieldTouch getFieldTouchListener() {
        return null;
    }

    private boolean mGameOver = false;

    @Override
    public void gameOver(boolean gameOver) {
        mGameOver = gameOver;
    }

    class Thinker extends AsyncTask<Void, Void, GameState> {

        @Override
        protected GameState doInBackground(Void... params) {

            return minimax(currentState, (deep - 1) * 2 + 1);
        }

        private GameState minimax(GameState gs, int recursiveLevel) {
            if (recursiveLevel <= 0 || mGameOver) {
                GameState stateRated = gs.getStateRated(gameField);
                return stateRated;
            }
            int best = gs.lastMove == GameState.WOLFS ? 255 : 0;

            ArrayList<GameState> bestGameState = null;
            if (gs.lastMove == GameState.WOLFS) {
                ArrayList<GameState> sheepPosibleMoves = getSheepPosibleMoves(gs);
                for (GameState possible : sheepPosibleMoves) {
                    int minimax = minimax(possible, recursiveLevel - 1).rate;
                    if (minimax < best || bestGameState == null) {
                        bestGameState = new ArrayList<GameState>();
                        best = minimax;
                        possible.rate = minimax;
                        bestGameState.add(possible);
                    } else if (minimax == best) {
                        possible.rate = minimax;
                        bestGameState.add(possible);
                    }
                }
            } else {
                ArrayList<GameState> wolfsPossibleMoves = getWolfsPossibleMoves(gs);
                for (GameState possible : wolfsPossibleMoves) {
                    int minimax = minimax(possible, recursiveLevel - 1).rate;
                    if (minimax > best || bestGameState == null) {
                        bestGameState = new ArrayList<GameState>();
                        best = minimax;
                        possible.rate = minimax;
                        bestGameState.add(possible);
                    } else if (minimax == best) {
                        possible.rate = minimax;
                        bestGameState.add(possible);
                    }
                }
            }
            if (bestGameState == null) {
                return gs.getStateRated(gameField);
            }
            return bestGameState.get((int) (Math.random() * bestGameState.size()));
        }

        private ArrayList<GameState> getWolfsPossibleMoves(GameState gs) {
            ArrayList<GameState> res = new ArrayList<GameState>();
            for (int wolfPos : gs.wolfPositions) {
                for (Node node : gameField.getNodes().get(wolfPos).getNear()) {
                    if (!gs.wolfPositions.contains(node.getId()) && node.getId() != gs.sheepPos
                        && wolfPos > node.getId()) {
                        GameState posibleState = new GameState(gs);
                        posibleState.wolfPositions.remove(wolfPos);
                        posibleState.wolfPositions.add(node.getId());
                        posibleState.lastMove = GameState.WOLFS;
                        res.add(posibleState);
                    }
                }
            }
            return res;
        }

        private ArrayList<GameState> getSheepPosibleMoves(GameState gs) {
            TreeSet<Node> near = gameField.getNodes().get(gs.sheepPos).getNear();
            ArrayList<GameState> possibleStates = new ArrayList<GameState>();
            Iterator<Node> iterator = near.iterator();
            while (iterator.hasNext()) {
                Node node = (Node) iterator.next();
                if (!gs.wolfPositions.contains(node.getId())) {
                    GameState possibleGameState = new GameState(gs);
                    possibleGameState.sheepPos = node.getId();
                    possibleGameState.lastMove = GameState.SHEEP;
                    possibleStates.add(possibleGameState);
                }
            }
            return possibleStates;
        }

        @Override
        protected void onPostExecute(GameState result) {
            if (!mGameOver) makeMoveListener.onMoveComlete(UniversalAIMinimaxRandomBest.this, result);
        }

    }
}
