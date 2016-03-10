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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.wordpress.growworkinghard.riverNe3.composite.Component;
import com.wordpress.growworkinghard.riverNe3.composite.key.Key;
import com.wordpress.growworkinghard.riverNe3.geometry.Geometry;
import com.wordpress.growworkinghard.riverNe3.treeBuilding.Tree;

/**
 * @brief Template class for new decorators
 *
 * To completely implement a decorator class, you have just to start the
 * algorithm you are designing to add features to the input tree from the
 * Template#buildTree() method.
 *
 * @author sidereus, francesco.serafin.3@gmail.com
 * @version 0.1
 * @date November 08, 2015
 * @copyright GNU Public License v3 AboutHydrology (Riccardo Rigon)
 */
public class Template extends BinaryTreeDecorator {

    private List<Geometry> data;
    private HashMap<Key, Component> tree;
    private Tree binaryTree;

    /**
     * @brief Constructor for the template class
     *
     * The minimum variables required to construct a decorator are:
     * <ul>
     * <li>the binary tree to decorate;</li>
     * <li>the input data with decorators info.</li>
     * </ul>
     * Then you can add all the variables you need.
     *
     * @param binaryTree The data structure to decorate
     * @param data The input data
     */
    public Template(final Tree binaryTree, final List<Geometry> data) {
        this.data = new ArrayList<Geometry>(data);
        this.tree = new HashMap<Key, Component>();
        this.binaryTree = binaryTree;

        validateState();

        tree.putAll(binaryTree.computeNodes());
    }

    /**
     * {@inheritDoc}
     *
     * @see Tree#computeNodes()
     */
    public HashMap<Key, Component> computeNodes() {
        return deepCopy(tree);
    }

    /**
     * {@inheritDoc}
     *
     * @see Tree#buildTree()
     */
    public void buildTree() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     *
     * @see BinaryTreeDecorator#validateState()
     */
    protected void validateState() {
        throw new UnsupportedOperationException();
    }
}
