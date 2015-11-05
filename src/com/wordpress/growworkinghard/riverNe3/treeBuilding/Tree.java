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
package com.wordpress.growworkinghard.riverNe3.treeBuilding;

import java.util.HashMap;

import com.wordpress.growworkinghard.riverNe3.composite.*;
import com.wordpress.growworkinghard.riverNe3.composite.key.Key;

/**
 * @brief Building the binary tree of the input river net
 *
 * @description This class parses a <code>List</code> of <tt>Geometry</tt>
 *              objects, finds the <strong>root node</strong> of a sub-tree,
 *              adds it to the <code>ConcurrentHashMap</code> structure after
 *              having computed its <em>left child</em> and <em>right child</em>
 *              . Then, the latter two become the new root nodes of a sub-tree
 *              and when a thread, which is parsing the <code>List</code>
 *              structure recognizes that, it executes the complete procedure.
 *              <p>
 *              This algorithm is able to recognize 3 type of <tt>Component</tt>
 *              (from the <strong>Composite Pattern</strong> implemented in the
 *              <code>composite</code> package):
 *              <ul>
 *              <li><tt>Leaf</tt>: node without children;</li>
 *              <li><tt>Node</tt>: node with children;</li>
 *              <li><tt>Ghost Node</tt>: node created when a river intersection
 *              is composed by 3 or more rivers. In a <tt>binary tree</tt>
 *              structure a node cannot have three children, so that a ghost
 *              node is necessary to gather the flow of two rivers and then
 *              releases it with the flow of the remaining river into the root
 *              node of the sub-tree. At the end it does nothing special, it is
 *              just a virtual structure required to build a binary tree.</li>
 *              </ul>
 *              </p>
 *
 * @code{.java}
 * TreeBuilding treeBuilding = new TreeBuilding();
 * ConcurrentHashMap<Integer, Component> tree = treeBuilding.get(listGeometries);
 * @endcode
 *
 * @todo design a better implementation for the method <code>computeNewNode</code>
 *
 * @todo complete the documentation
 *
 * @author Francesco Serafin, francesco.serafin.3@gmail.com
 * @version 0.1
 * @date October 13, 2015
 * @copyright GNU Public License v3 AboutHydrology (Riccardo Rigon)
 */
public abstract class Tree {

    public abstract HashMap<Key, Component> computeNodes();

    public abstract void buildTree();

}
