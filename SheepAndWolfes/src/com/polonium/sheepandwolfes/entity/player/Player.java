package com.polonium.sheepandwolfes.entity.player;

import com.polonium.sheepandwolfes.entity.game.GameState;
import com.polonium.sheepandwolfes.views.GameFieldView.OnFieldTouch;

public interface Player {
    public Player makeMove(GameState gameState, OnMakeMoveListener listener);
    public OnFieldTouch getFieldTouchListener();
    public void gameOver(boolean gameOver);
}
