package com.polonium.sheepandwolfes.entity.data;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class Tree<T> {
    private TreeNode<T> root;

    public Tree(T rootData) {
        this.root = new TreeNode<T>();
        getRoot().setObject(rootData);
        getRoot().setChildren(new LinkedList<TreeNode<T>>());
    }
   
    public TreeNode<T> getRoot() {
        return root;
    }

    public static class TreeNode<T> {
        private T data;
        private TreeNode<T> parent;
        private List<TreeNode<T>> children;

        public T getObject() {
            return data;
        }

        public void setObject(T data) {
            this.data = data;
        }

        public TreeNode<T> getParent() {
            return parent;
        }

        public void setParent(TreeNode<T> parent) {
            this.parent = parent;
        }

        public List<TreeNode<T>> getChildren() {
            return children;
        }

        public void setChildren(List<TreeNode<T>> children) {
            this.children = children;
        }

        public void addChild(T object) {
            TreeNode<T> node = new TreeNode<T>();
            node.parent = this;
            node.setObject(object);
            node.setChildren(new LinkedList<TreeNode<T>>());
            children.add(node);
        }
        
        public void addChildren(Collection<T> objects) {
            for (T node:objects){
                addChild(node);
            }
        }
    }
}