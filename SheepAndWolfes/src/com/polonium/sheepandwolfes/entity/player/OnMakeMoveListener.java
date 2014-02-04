package com.polonium.sheepandwolfes.entity.player;

import com.polonium.sheepandwolfes.entity.game.GameState;

public interface OnMakeMoveListener {
    void onMoveComlete(Player player, GameState state);
}
