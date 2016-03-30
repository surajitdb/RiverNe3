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
package com.wordpress.growworkinghard.riverNe3.tree.building.decorator;

import java.util.HashMap;

import com.wordpress.growworkinghard.riverNe3.composite.Component;
import com.wordpress.growworkinghard.riverNe3.composite.key.Key;
import com.wordpress.growworkinghard.riverNe3.tree.building.Tree;

import net.jcip.annotations.ThreadSafe;

/**
 * @brief Abstract class of the decorators of the <strong>Decorator
 *        Pattern</strong>
 *
 * @description This class is <em>ThreadSafe</em> because the implemented
 *              methods are synchronized and guarded by the intrinsic lock
 *
 * @author sidereus, francesco.serafin.3@gmail.com
 * @version 0.1
 * @date November 08, 2015
 * @copyright GNU Public License v3 AboutHydrology (Riccardo Rigon)
 */
@ThreadSafe
public abstract class BinaryTreeDecorator extends Tree {

    /**
     * {@inheritDoc}
     *
     * @see Tree#computeNodes()
     */
    @Override
    abstract public HashMap<Key, Component> computeNodes();

    @Override
    abstract protected void buildTree();
    /**
     * @brief Validation of the states of a decorator
     */
    abstract protected void validateState();

    /**
     * @brief Validation of the input binary tree
     *
     * @param[in] binaryTree The input binary tree
     */
    protected synchronized void validateBinaryTree(final Tree binaryTree) {
        if (binaryTree == null)
            throw new NullPointerException("Object BinaryTree cannot be null");
    }

    /**
     * @brief Return a copy of the computed tree
     *
     * @param[in] tree The computed tree with the new features
     * @return The copy of the just computed tree
     */
    protected synchronized HashMap<Key, Component> deepCopy(final HashMap<Key, Component> tree) {

        HashMap<Key, Component> result = new HashMap<Key, Component>();
        result.putAll(tree);

        return result;

    }

}
