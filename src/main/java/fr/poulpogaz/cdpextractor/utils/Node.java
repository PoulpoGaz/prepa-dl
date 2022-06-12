package fr.poulpogaz.cdpextractor.utils;

import java.util.*;

/**
 * A basic tree class.
 * Don't expect good performance from this
 */
public class Node<V> implements Iterable<Node<V>> {

    private final List<Node<V>> children;
    private Node<V> parent;
    private V value;

    public Node() {
        children = new ArrayList<>();
    }

    public Node(V value) {
        this.value = value;
        children = new ArrayList<>();
    }

    public void addChildren(V child) {
        addChildren(new Node<>(child));
    }

    public void addChildren(Node<V> child) {
        child.removeFromParent();
        children.add(child);
        child.parent = this;
    }

    public void removeFromParent() {
        if (parent != null) {
            parent.removeChildren(this);
        }
    }

    public void removeChildren(Node<V> child) {
        if (children.remove(child)) {
            child.parent = null;
        }
    }

    public Node<V> getRoot() {
        Node<V> node = this;

        while (node.parent != null) {
            node = node.parent;
        }

        return node;
    }

    public Node<V> getParent() {
        return parent;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    public List<Node<V>> getChildren() {
        return Collections.unmodifiableList(children);
    }

    @Override
    public Iterator<Node<V>> iterator() {
        return depthFirstIterator();
    }

    public Iterator<Node<V>> depthFirstIterator() {
        return new DepthFirstIterator();
    }

    private class DepthFirstIterator implements Iterator<Node<V>> {

        private final Stack<Node<V>> stack = new Stack<>();

        public DepthFirstIterator() {
            stack.push(Node.this);
        }

        @Override
        public boolean hasNext() {
            return !stack.isEmpty();
        }

        @Override
        public Node<V> next() {
            Node<V> next = stack.pop();

            for (Node<V> child : next.children) {
                stack.push(child);
            }

            return next;
        }
    }
}