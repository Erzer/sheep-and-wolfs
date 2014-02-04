package com.polonium.sheepandwolfes.entity.game;

import java.util.ArrayList;
import java.util.TreeSet;

import com.polonium.sheepandwolfes.entity.field.GameField;
import com.polonium.sheepandwolfes.entity.field.Node;

public class GameState {
    public final static int WOLFS = 1;
    public final static int SHEEP = 2;
    
    public int sheepPos;
    public TreeSet<Integer> wolfPositions;
    public int rate = 0;
    public int lastMove = 0;

    @SuppressWarnings("serial")
    private final static TreeSet<Integer> goals = new TreeSet<Integer>() {
        {
            add(28);
            add(29);
            add(30);
            add(31);
        }
    };
    
    public GameState(GameState gameState) {
        this.sheepPos = gameState.sheepPos;
        this.wolfPositions = new TreeSet<Integer>(gameState.wolfPositions);
        this.rate = gameState.rate;
        this.lastMove = gameState.lastMove;
    }

    public GameState(int sheepPos, int... wolfPos) {
        super();
        this.sheepPos = sheepPos;
        this.wolfPositions = new TreeSet<Integer>();
        for (int pos : wolfPos) {
            this.wolfPositions.add(pos);
        }
        lastMove = WOLFS;
    }

    public int heuristicRate(GameField field) {
        field.fillBy(-1);
        field.getNodes().get(sheepPos).setValue(0);
        for (int pos : wolfPositions) {
            field.getNodes().get(pos).setValue(0);
        }

        ArrayList<Node> buffer = new ArrayList<Node>();
        buffer.add(field.getNodes().get(sheepPos));

        int stepToGoal = 0;
        do {
            Node node = buffer.remove(0);
            for (Node near : node.getNear()) {
                if (near.getValue() < 0) {
                    int step = node.getValue() + 1;
                    if (goals.contains(near.getId())) {
                        return step;
                    }
                    near.setValue(step);
                    buffer.add(near);
                    stepToGoal = (stepToGoal < step) ? step : stepToGoal;
                }
            }
        } while (!buffer.isEmpty());
        return 255;
    }
    
    public GameState getStateRated(GameField field){
        this.rate = heuristicRate(field);
        return this;
    }
    
    public boolean wolfsWin(GameField field) {
        field.fillBy(-1);
        field.getNodes().get(sheepPos).setValue(0);
        for (int pos : wolfPositions) {
            field.getNodes().get(pos).setValue(0);
        }
        Node node = field.getNodes().get(sheepPos);
        for (Node near : node.getNear()) {
            if (near.getValue() < 0) {
                return true;
            }
        }
        return false;
    }

    public boolean sheepWin() {
        int sheepY = sheepPos / 4;
        for (Integer wp : wolfPositions) {
            int wolfY = wp / 4;
            if (wolfY > sheepY) return false;
        }
        return true;
    }
}
