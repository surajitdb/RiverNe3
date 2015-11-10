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
package com.wordpress.growworkinghard.riverNe3.composite.key;

import java.util.List;

/**
 * @brief Abstract class for the connection of the nodes
 *
 * @description This type of object has been introduced in order to manage the
 *              invariants as unique instance, as specified in @cite
 *              goetz2006:java p69.
 *              <p>
 *              This abstract class should work as guide for
 *              who wants to implement the connections between nodes of a tree.
 *              The methods <code>getNumberNonNullChildren()</code> and
 *              <code>getChildren()</code> must be implemented because are
 *              necessary to get the number of children of each node, in order
 *              to know when a node has all the input data to run its own
 *              simulation.
 *              </p>
 *
 * @author Francesco Serafin, francesco.serafin.3@gmail.com
 * @version 0.1
 * @date November 08, 2015
 * @copyright GNU Public License v3 AboutHydrology (Riccardo Rigon)
 */
public abstract class Connections {

    /**
     * @brief Returns the <tt>ID</tt> key of the node
     *
     * @return The <tt>ID</tt> key of the node
     */
    abstract public Key getID();

    /**
     * @brief Returns the <tt>PARENT</tt> key of the node
     *
     * @return The <tt>PARENT</tt> key of the node
     */
    abstract public Key getPARENT();

    /**
     * @brief Returns the number of non null children of the node
     *
     * @description Considering the chance of having more than two children per
     *              node, this method is necessary if you want to know the
     *              effective number of children of a node.
     *
     * @return The number of the non null children of the node
     */
    abstract public int getNumberNonNullChildren();

    /**
     * @brief Return the children of the node
     *
     * @description In order to know the corresponding children of the node,
     *              this method return the <code>List</code> of the <tt>Key</tt>
     *              of each child.
     *
     * @return The <code>List</code> of the <tt>Key</tt> of each child of the
     *         node
     */
    abstract public List<Key> getChildren();

    /**
     * @brief Return the right child of the node
     *
     * @description This method must be implemented only for connections for
     *              binary trees
     *
     * @return The <tt>Key</tt> of the right child of the node
     */
    public Key getRCHILD() {
        throw new UnsupportedOperationException("Method not implemented.");
    }

    /**
     * @brief Return the left child of the node
     *
     * @description This method must be implemented only for connections for
     *              binary trees
     *
     * @return The <tt>Key</tt> of the left child of the node
     */
    public Key getLCHILD() {
        throw new UnsupportedOperationException("Method not implemented.");
    }

    /**
     * @brief Validate the states of the connection object
     */
    abstract protected void validateStates();

    /**
     * @brief Validate the invariant of the states of the connection object
     */
    abstract protected void validateInvariant();

    /**
     * @brief Validate a <tt>Key</tt> object
     *
     * @param[in] key The input key
     */
    abstract protected void validateKey(final Key key);

}
