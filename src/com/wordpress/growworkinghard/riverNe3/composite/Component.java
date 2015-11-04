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
package com.wordpress.growworkinghard.riverNe3.composite;

import java.util.List;

import org.geotools.graph.util.geom.Coordinate2D;

import com.google.common.collect.BinaryTreeTraverser;
import com.wordpress.growworkinghard.riverNe3.composite.key.Connections;
import com.wordpress.growworkinghard.riverNe3.composite.key.Key;

/**
 * @brief class Component
 *
 * @description This is the abstract class of the <strong>Composite
 *              Pattern</strong>
 *              @cite freeman2004:head,
 *               which has been used to realize the structure
 *              for the binary tree
 *
 * @author Francesco Serafin, francesco.serafin.3@gmail.com
 * @version 0.1
 * @date October 13, 2015
 * @copyright GNU Public License v3 AboutHydrology (Riccardo Rigon)
 */
public abstract class Component {

    /**
     * @brief <tt>notify</tt> method from <strong>Observer Pattern</strong>
     * @cite freeman2004:head
     *
     * @description This method is used by children to notify to the parent that
     *              the computation of their simulation is finished.
     *
     * @param[in] child The key of the child whose computation is finished
     */
    public void notify(final Key child) {
        throw new UnsupportedOperationException("Method not implemented for class LEAF");
    }

    /**
     * @brief It returns if the node is ready to start the simulation
     *
     * @retval TRUE if the node is ready to start the simulation
     * @retval FALSE otherwise
     */
    abstract public boolean isReadyForSimulation();

    /**
     * @brief This method contains the type of simulation to run
     *
     * @description This method is divided in 3 main parts:
     *              <ol>
     *              <li><strong>precondition</strong> to check if the argument
     *              in input is actually the parent;</li>
     *              <li>the simulation to run;</li>
     *              <li>the notification to the parent that the simulation is
     *              finished.</li>
     *              </ol>
     * @param[in] parent The parent node
     */
    abstract public void runSimulation(final Component parent);

    /**
     * @brief Set the new <tt>key</tt> of the node
     *
     * @description Set the new <tt>key</tt> of the node, changing the parent
     *              key, left and right key as consequence. These states are
     *              related with an <strong>invariant</strong> because:
     *              <ul>
     *              <li>\f$parentKey = key / 2\f$;</li>
     *              <li>\f$leftChildKey = key * 2\f$;</li>
     *              <li>\f$rightChildKey = leftChildKey + 1\f$.</li>
     *              </ul>
     *
     * @param[in] key The new input key
     */
    abstract public void setNewKey(final Key key);

    /**
     * @brief Get the <tt>key</tt> of the node
     *
     * @return The <tt>key</tt> of the node
     */
    abstract public Key getKey();

    /**
     * @brief Get the <tt>key</tt> of the left child
     *
     * @return The <tt>key</tt> of the left child
     */
    abstract public Key getLeftChildKey();

    /**
     * @brief Get the <tt>key</tt> of the right child
     *
     * @return The <tt>key</tt> of the right child
     */
    abstract public Key getRightChildKey();

    /**
     * @brief Get the <tt>key</tt> of the parent node
     *
     * @return The <tt>key</tt> of the parent node
     */
    abstract public Key getParentKey();

    /**
     * @brief Set the <tt>layer</tt> of the node
     *
     * @param[in] layer The <tt>layer</tt> of the node in the tree
     */
    abstract public void setLayer(final int layer);

    /**
     * @brief Get the <tt>layer</tt> of the node
     *
     * @return The <tt>layer</tt> of the node in the tree
     */
    abstract public Integer getLayer();

    /**
     * @brief Get the coordinate of the starting point
     *
     * @description Each node of the tree can be identified with a starting
     *              point and an ending point. In the case of a sub-basin they
     *              are equal to the starting point and ending point of the main
     *              stream; in the case of a ghost node or a local point they
     *              coincide. This is necessary in order to have a unique API,
     *              furthermore that allows to identify ghost node and local
     *              point in parsing the classes.
     *
     * @return The coordinate of the starting point
     */
    abstract public Coordinate2D getStartPoint();

    /**
     * @brief Get the coordinate of the ending point
     *
     * @see Component#getStartPoint()
     *
     * @return The coordinate of the ending point
     */
    abstract public Coordinate2D getEndPoint();

    /**
     * @brief Set the traverser of the tree
     *
     * @description In order to compose each node with a <tt>traverser</tt>, due
     *              to the fact that the structure implemented for the tree is
     *              an <code>HashMap</code> and not a nested structure, the only
     *              way is building the <tt>traverser</tt> outside the node and
     *              then set <code>this</code> as starting point of the sub-tree
     *              to traverse.
     *
     * @param[in] traverser The traverser
     * @todo Generalize the traverser in order to use this structure not only
     *       for a binary tree
     */
    abstract public void setTraverser(final BinaryTreeTraverser<Component> traverser);

    /**
     * @brief Compute the <strong>Preorder</strong> <code>List</code> of nodes
     *        in the subtree with <tt>this</tt> as main vertex
     *
     * @return The <strong>Preorder</strong> <code>List</code> of nodes
     */
    abstract public List<Component> preOrderTraversal();

    /**
     * @brief Compute the <strong>Postorder</strong> <code>List</code> of nodes
     *        in the subtree with <tt>this</tt> as main vertex
     *
     * @return The <strong>Postorder</strong> <code>List</code> of nodes
     */
    abstract public List<Component> postOrderTraversal();

    /**
     * @brief Compute the <strong>Inorder</strong> <code>List</code> of nodes in
     *        the subtree with <tt>this</tt> as main vertex
     *
     * @return The <strong>Inorder</strong> <code>List</code> of nodes
     */
    abstract public List<Component> inOrderTraversal();

    /**
     * @brief Method to implement in order to check if states are null
     *
     * @retval TRUE if each and every state is null
     * @retval FALSE otherwise
     */
    abstract protected boolean statesAreNull();

    /**
     * @brief Method to implement in order to validate the states of the class
     */
    abstract protected void validateState();

    /**
     * @brief Method to implement in order to allocate memory for the flags
     *        which identify if the simulation of a child is finished or not
     */
    abstract protected void allocateSimulationFlags();

    /**
     * @brief Method to compute the <tt>key</tt> of the parent node
     *
     * @param[in] key The <tt>key</tt> of the node
     * @return The <tt>key</tt> of the parent node
     */
    abstract protected Key computeParentKey(final Key key);

    /**
     * @brief Validate a <tt>Key</tt> object
     *
     * @param[in] key The <tt>key</tt> to validate
     * @exception NullPointerException
     *                if the <tt>key</tt> is null
     */
    protected synchronized void validateKey(final Key key) {

        if (key == null) {
            String message = "Only the key of the right child can be null";
            throw new NullPointerException(message);
        }

    }

    /**
     * @brief Validate the <tt>layer</tt> of the node in the tree
     *
     * @param[in] layer The layer of the node
     * @exception IllegalArgumentException
     *                if the input layer is equal 0 or negative
     */
    protected synchronized void validateLayer(final int layer) {

        if (layer <= 0) {
            String message = "Layer cannot be 0 or negative";
            throw new IllegalArgumentException(message);
        }

    }

    /**
     * @brief Validate the <tt>coordinate</tt> of a point
     *
     * @param[in] point The coordinates to validate
     * @exception NullPointerException
     *                if the input point is null
     * @exception IllegalArgumentException
     *                if the <tt>x</tt> or <tt>y</tt> coordinates are negative
     */
    protected synchronized void validateCoordinate(final Coordinate2D point) {

        if (point == null) {
            String message = "The coordinates of a point cannot be null";
            throw new NullPointerException(message);
        }

        if (point.x < 0 || point.y < 0) {
            String message = "Negative coordinates are not allowed";
            throw new IllegalArgumentException(message);
        }

    }

    /**
     * @brief Validate the <strong>invariant</strong> of the node
     *
     * @param[in] key The <tt>key</tt> of the node
     * @param[in] parentKey The <tt>key</tt> of the parent node
     * @param[in] leftChildKey The <tt>key</tt> of the left child
     * @param[in] rightChildKey The <tt>key</tt> of the right child
     * @exception IllegalArgumentException
     *                the exception is thrown in three cases
     *                <ul>
     *                <li>the parent key is not the half of the key node;</li>
     *                <li>the left child key is not the twice of the key node;
     *                </li>
     *                <li>the right child key is not the left child key + 1.
     *                </li>
     *                </ul>
     */
    protected synchronized void validateInvariant(final Key key, final Key parentKey, final Key leftChildKey, final Key rightChildKey) {

        if (parentKey.getDouble() != Math.floor(key.getDouble() / 2)) {
            String message = "Parent key " + parentKey.getString();
            message += " is not the half of the key " + key.getString();
            throw new IllegalArgumentException(message);
        }

        if (key.getDouble() * 2 != leftChildKey.getDouble()) {
            String message = "Left child key " + leftChildKey.getString();
            message += " is not the twice of the key " + key.getString();
            throw new IllegalArgumentException(message);
        }

        if (rightChildKey != null &&
            (leftChildKey.getDouble() + 1) != rightChildKey.getDouble()) {
            String message = "Righ child key " + rightChildKey.getString();
            message += " is not the the left child key " + leftChildKey.getString();
            message += " + 1";
            throw new IllegalArgumentException(message);
        }

    }

    protected void validateConnections(final Connections connKeys) {
        if (connKeys == null)
            throw new NullPointerException("Input Connections object cannot be null.");
    }

}
