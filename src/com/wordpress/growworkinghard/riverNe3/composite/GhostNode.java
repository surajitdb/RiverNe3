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

import com.wordpress.growworkinghard.riverNe3.composite.key.Key;

import net.jcip.annotations.GuardedBy;

/**
 * @brief class Ghost Node
 *
 * @description The main purpose of this class is the representation of
 *              <tt>ghost nodes</tt> inside the binary tree designed with the
 *              <strong>Composite Pattern</strong>. <tt>ghost node</tt> are
 *              necessary because a river network might have a intersection with
 *              more than two streams and using <strong>binary tree</strong> as
 *              schema doesn't allow that. In an intersection with 3 streams, a
 *              <tt>ghost node</tt> enables outline it with a stream and the
 *              <tt>ghost node</tt> where the other two streams flows
 *
 * @todo make this class <em>ThreadSafe</em>
 *
 * @author Francesco Serafin, francesco.serafin.3@gmail.com
 * @version 0.1
 * @date October 13, 2015
 * @copyright GNU Public License v3 AboutHydrology (Riccardo Rigon)
 */
public class GhostNode implements Component {

    @GuardedBy("this") private Key parentKey; //!< the key of the HashMap of the parent
    @GuardedBy("this") private Integer layer; //!< the layer in the tree in which this node is located
    @GuardedBy("this") private Key leftChildKey; //!< the key of the HashMap of the left child
    @GuardedBy("this") private Key rightChildKey; //!< the key of the HashMap of the right child

    /**
     * @brief Default constructor
     */
    public GhostNode() {}

    /**
     * @brief Alternative constructor which requires all the states
     *
     * @param[in] parentKey
     *            The <tt>HashMap</tt> key of the parent
     * @param[in] leftChildKey
     *            The <tt>HashMap</tt> key of the left child
     * @param[in] rightChildKey
     *            The <tt>HashMap</tt> key of the right child
     * @param[in] layer
     *            The layer in the tree in which this node is located
     */
    public GhostNode(final Key parentKey, final Key leftChildKey, final Key rightChildKey, final int layer) {

        getInstance(parentKey, leftChildKey, rightChildKey, layer);

    }

    private void getInstance(final Key parentKey, final Key leftChildKey, final Key rightChildKey, final int layer) {

        if (this.parentKey == null && this.layer == null && this.leftChildKey == null && this.rightChildKey == null) {
            synchronized(this) {
                if (this.parentKey == null && this.layer == null && this.leftChildKey == null && this.rightChildKey == null) {

                    this.parentKey = new Key(parentKey);
                    this.leftChildKey = new Key(leftChildKey);
                    this.rightChildKey = new Key(rightChildKey);
                    this.layer = new Integer(layer);

                    validateState();

                }

            }

        }

    }

    @Override
    public synchronized void put() {
        new UnsupportedOperationException("Method not implemented yet");    
    }

    @Override
    public synchronized void delete() {
        new UnsupportedOperationException("Method not implemented yet");
    }

    /**
     * @brief Setter method to set the key of the left child
     *
     * @param[in] leftChildKey
     *            The <tt>HashMap</tt> key of the left child
     */
    @Override
    public synchronized void setLeftChildKey(final Key leftChildKey) {
        validateKey(leftChildKey);
        this.leftChildKey = new Key(leftChildKey);
    }

    /**
     * @brief Getter method to get the key of the left child
     *
     * @return The <tt>HashMap</tt> key of the left child
     */
    @Override
    public synchronized Key getLeftChildKey() {
        validateKey(leftChildKey);
        return new Key(leftChildKey);
    }

    /**
     * @brief Setter method to set the key of the right child
     *
     * @param[in] rightChildKey
     *            The <tt>HashMap</tt> key of the right child
     */
    @Override
    public synchronized void setRightChildKey(final Key rightChildKey) {
        validateKey(rightChildKey);
        this.rightChildKey = new Key(rightChildKey);
    }

    /**
     * @brief Getter method to get the key of the right child
     *
     * @return The <tt>HashMap</tt> key of the right child
     */
    @Override
    public synchronized Key getRightChildKey() {
        validateKey(rightChildKey);
        return new Key(rightChildKey);
    }

    /**
     * @brief Setter method to set the key of the parent node
     *
     * @param[in] parentKey The <tt>HashMap</tt> key of the parent node
     */
    @Override
    public synchronized void setParentKey(final Key parentKey) {
        validateKey(parentKey);
        this.parentKey = parentKey;
    }

    /**
     * @brief Getter method to get the key of the parent node
     *
     * @return The <tt>HashMap</tt> key of the parent node
     */
    @Override
    public synchronized Key getParentKey() {
        validateKey(parentKey);
        return new Key(parentKey);
    }

    /**
     * @brief Setter method to set the layer of the node
     *
     * @param[in] layer The layer of the node in the tree
     */
    @Override
    public synchronized void setLayer(final int layer) {
        validateLayer(layer);
        this.layer = layer;
    }

    /**
     * @brief Getter method to get the layer of the node
     *
     * @return The layer of the node in the tree
     */
    @Override
    public synchronized Integer getLayer() {
        validateLayer(layer);
        return new Integer(layer);
    }

    /**
     * @brief Simply overriding of the <code>toString</code> method
     *
     * @return The state variables of the object
     */
    @Override
    public String toString() {
  
        String tmp = "Ghost - Parent Key = " + parentKey.getString() + " Left Child = " + leftChildKey.getString() + " Right Child = " + rightChildKey.getString() + " Layer = " + layer;
        return tmp;

    }

    private void validateState() {

        validateKey(parentKey);
        validateLayer(layer);
        validateKey(leftChildKey);
        validateKey(rightChildKey);

    }

    private void validateKey(final Key key) {

        if (key == null || key.getString() == null)
            throw new NullPointerException("Component keys cannot be null");
    }

    private void validateLayer(final int layer) {

        if (layer < 0)
            throw new NullPointerException("Layer cannot be null or less then zero");

    }

}
