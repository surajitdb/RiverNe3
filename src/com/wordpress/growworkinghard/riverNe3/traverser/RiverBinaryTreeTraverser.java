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

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.base.Optional;
import com.google.common.collect.BinaryTreeTraverser;
import com.wordpress.growworkinghard.riverNe3.composite.Component;
import com.wordpress.growworkinghard.riverNe3.composite.key.Key;

import net.jcip.annotations.Immutable;

/**
 * @brief How retrieving right and left children from the binary tree
 *
 * @description This class implements how retrieving right and left children
 *              from the <tt>HashMap</tt> containing the binary tree. The
 *              implementation of the traverser are in the BinaryTreeTraverser
 *              class, from the GUAVA package
 *              <p>
 *              This class is <em>ThreadSafe</em> because it is
 *              <em>Immutable</em>.
 *
 * @author sidereus, francesco.serafin.3@gmail.com
 * @version 0.1
 * @date November 08, 2015
 * @copyright GNU Public License v3 AboutHydrology (Riccardo Rigon)
 */
@Immutable
public class RiverBinaryTreeTraverser extends BinaryTreeTraverser<Component> {

    private final ConcurrentHashMap<Key, Component> binaryTree
        = new ConcurrentHashMap<Key, Component>(); //!< copy of the binary tree

    /**
     * @brief Constructor
     *
     * @param[in] binaryTree The structure of the binary tree
     */
    public RiverBinaryTreeTraverser(final HashMap<Key, Component> binaryTree) {
        this.binaryTree.putAll(binaryTree);
    }

    /**
     * @brief Compute the left child of the input root
     *
     * @param[in] root The root of the subtree
     * @return The left child of the root
     */
    @Override
    public synchronized Optional<Component> leftChild(Component root) {
        Key index = root.getConnections().getLCHILD(); // get the key

        Component node = null;
        if (index != null)
            node = binaryTree.get(index); // get the LCHILD

        return Optional.fromNullable(node);
    }

    /**
     * @brief Compute the right child of the input root
     *
     * @param[in] root The root of the subtree
     * @return The right child of the root
     */
    @Override
    public synchronized Optional<Component> rightChild(Component root) {
        Key index = root.getConnections().getRCHILD(); // get the key

        Component node = null;
        if (index != null)
            node = binaryTree.get(index); //get the RCHILD

        return Optional.fromNullable(node);
    }

}
