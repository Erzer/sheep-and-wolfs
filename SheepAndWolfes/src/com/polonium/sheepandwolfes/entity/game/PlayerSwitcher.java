package com.polonium.sheepandwolfes.entity.game;

import com.polonium.sheepandwolfes.entity.player.Player;

public class PlayerSwitcher {

    PlayerHolder first;
    PlayerHolder current;

    public void addPlayer(Player player) {
        if (first == null) {
            first = new PlayerHolder();
            first.player = player;
            first.next = first;
            current = first;
        } else {
            PlayerHolder last = new PlayerHolder();
            last.player = player;
            last.next = first;
            current.next = last;
            current = last;
        }
    }

    public Player getNext() {
        current = current.next;
        return current.player;
    }

    public Player getCurrent() {
        return current.player;
    }

    class PlayerHolder {
        PlayerHolder next;
        Player player;
    }
}
