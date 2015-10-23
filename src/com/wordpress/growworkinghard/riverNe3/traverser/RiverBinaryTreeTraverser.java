/*
 * GNU GPL v3 License
 *
 * Copyright 2015 AboutHydrology (Riccardo Rigon)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
