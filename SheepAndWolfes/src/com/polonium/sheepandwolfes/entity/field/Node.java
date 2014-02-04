package com.polonium.sheepandwolfes.entity.field;

import java.util.TreeSet;

public class Node implements Comparable<Node>{
    private int id;
    private TreeSet<Node> near = new TreeSet<Node>();
    private int value;
    
    public Node(int id) {
        this.setId(id);
    }

    public boolean connectTo(Node node) {
        if (!getNear().contains(node)) {
            getNear().add(node);
            return true;
        }
        return false;
    }

    public void twoSideConnectTo(Node node) {
        connectTo(node);
        node.connectTo(this);
    }

    @Override
    public int hashCode() {
        return getId();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Node other = (Node) obj;
        if (getId() != other.getId()) return false;

        return true;
    }

    @Override
    public int compareTo(Node another) {
        if (getId() < another.getId()) return -1;
        if (getId() > another.getId()) return 1;
        return 0;
    }

    public TreeSet<Node> getNear() {
        return near;
    }

    public void setNear(TreeSet<Node> near) {
        this.near = near;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

}
