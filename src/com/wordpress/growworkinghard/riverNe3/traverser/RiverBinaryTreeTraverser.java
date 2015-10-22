package com.wordpress.growworkinghard.riverNe3.traverser;

import java.util.concurrent.ConcurrentHashMap;

import com.google.common.base.Optional;
import com.google.common.collect.BinaryTreeTraverser;
import com.wordpress.growworkinghard.riverNe3.composite.Component;
import com.wordpress.growworkinghard.riverNe3.composite.key.Key;

public class RiverBinaryTreeTraverser extends BinaryTreeTraverser<Component> {

    private volatile ConcurrentHashMap<Key, Component> binaryTree;

    public RiverBinaryTreeTraverser(final ConcurrentHashMap<Key, Component> binaryTree) {

        getInstance(binaryTree);

    }

    private void getInstance(final ConcurrentHashMap<Key, Component> binaryTree) {

        if (this.binaryTree == null) {
            synchronized(this) {
                if (this.binaryTree == null) {
                    this.binaryTree = new ConcurrentHashMap<Key, Component>(binaryTree);
                }
            }
        }
    }

    @Override
    public Optional<Component> leftChild(Component root) {
        Key index = root.getLeftChildKey();

        Component node = null;
        if (index.getString() != null)
            node = binaryTree.get(index);

        return Optional.fromNullable(node);

    }

    @Override
    public Optional<Component> rightChild(Component root) {
        Key index = root.getRightChildKey();

        Component node = null;
        if (index.getString() != null)
            node = binaryTree.get(index);

        return Optional.fromNullable(node);

    }

}
