package com.wordpress.growworkinghard.riverNe3.traverser;

import java.util.concurrent.ConcurrentHashMap;

import com.google.common.base.Optional;
import com.google.common.collect.BinaryTreeTraverser;
import com.wordpress.growworkinghard.riverNe3.composite.Component;

public class RiverBinaryTreeTraverser extends BinaryTreeTraverser<Component> {

    private volatile ConcurrentHashMap<Integer, Component> binaryTree;

    public RiverBinaryTreeTraverser(final ConcurrentHashMap<Integer, Component> binaryTree) {

        getInstance(binaryTree);

    }

    private void getInstance(final ConcurrentHashMap<Integer, Component> binaryTree) {

        if (this.binaryTree == null) {
            synchronized(this) {
                if (this.binaryTree == null) {
                    this.binaryTree = new ConcurrentHashMap<Integer, Component>(binaryTree); 
                }
            }
        }
    }

    @Override
    public Optional<Component> leftChild(Component root) {
        Integer index = root.getLeftChildKey();

        Component node = null;
        if (index != null) 
            node = binaryTree.get(index);

        return Optional.fromNullable(node);

    }

    @Override
    public Optional<Component> rightChild(Component root) {
        Integer index = root.getRightChildKey();

        Component node = null;
        if (index != null)
            node = binaryTree.get(index);

        return Optional.fromNullable(node);

    }

}
