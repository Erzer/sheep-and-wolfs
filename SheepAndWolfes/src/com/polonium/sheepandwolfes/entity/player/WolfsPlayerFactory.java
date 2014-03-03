package com.polonium.sheepandwolfes.entity.player;

public class WolfsPlayerFactory extends PlayerFactory {

	@Override
	public Player createPlayer(int level) {
		if (level == 0) {
            return new WolfsHuman();
        } else if (level < 4) {
            return new UniversalAIMinimax(level);
        } else {
            return new UniversalAIMinimaxAlphaBeta(level - 2);
        }
	}

}
