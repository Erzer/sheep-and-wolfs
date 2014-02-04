package com.polonium.sheepandwolfes.entity.field;

import java.util.ArrayList;

public class GameField {
    private ArrayList<Node> field = new ArrayList<Node>();

    public GameField() {
        for (int i = 0; i <= 31; i++) {
            getNodes().add(new Node(i));
        }
        for (int i = 0; i < 28; i++) {
            if (i > 0 && i % 4 == 0) i += 4;
            if (i - 5 > 0 && i / 4 == (i - 5) / 4 + 1) getNodes().get(i).twoSideConnectTo(getNodes().get(i - 5));
            if (i - 4 > 0 && i / 4 == (i - 4) / 4 + 1) getNodes().get(i).twoSideConnectTo(getNodes().get(i - 4));
            if (i / 4 == (i + 3) / 4 - 1) getNodes().get(i).twoSideConnectTo(getNodes().get(i + 3));
            if (i / 4 == (i + 4) / 4 - 1) getNodes().get(i).twoSideConnectTo(getNodes().get(i + 4));
        }
    }

    public ArrayList<Node> getNodes() {
        return field;
    }

    public void fillBy(int value){
        for (Node node: field){
            node.setValue(value);
        }
    }
}
