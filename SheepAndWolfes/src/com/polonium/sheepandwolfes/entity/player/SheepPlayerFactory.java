package com.polonium.sheepandwolfes.entity.player;

public class SheepPlayerFactory extends PlayerFactory {

	@Override
	public Player createPlayer(int level) {
		if (level == 0) {
            return new SheepHuman();
        } else if (level < 4) {
            return new UniversalAIMinimax(level);
        } else {
            return new UniversalAIMinimaxAlphaBeta(level - 2);
        }
	}

}
