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
package com.wordpress.growworkinghard.riverNe3.treeBuilding.decorator;

import java.util.HashMap;

import com.wordpress.growworkinghard.riverNe3.composite.Component;
import com.wordpress.growworkinghard.riverNe3.composite.key.Key;
import com.wordpress.growworkinghard.riverNe3.treeBuilding.BinaryTree;

public abstract class BinaryTreeDecorator extends BinaryTree {

    public abstract HashMap<Key, Component> computeNodes();

    public abstract void buildTree();

    protected abstract void validateState();

    protected void validateBinaryTree(final BinaryTree binaryTree) {
        if (binaryTree == null)
            throw new NullPointerException("Object BinaryTree cannot be null");
    }

    protected synchronized HashMap<Key, Component> deepCopy(final HashMap<Key, Component> tree) {

        HashMap<Key, Component> result = new HashMap<Key, Component>();
        result.putAll(tree);

        return result;

    }

}
