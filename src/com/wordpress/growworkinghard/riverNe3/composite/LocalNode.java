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
 * @brief class Localized Node
 *
 * @description The main purpose of this class is the representation of
 *              localized nodes, e.g. dams or monitoring points, inside the
 *              binary tree designed with the <strong>Composite Pattern</strong>
 *
 * @todo make this class <em>ThreadSafe</em>
 *
 * @author Francesco Serafin, francesco.serafin.3@gmail.com
 * @version 0.1
 * @date October 13, 2015
 * @copyright GNU Public License v3 AboutHydrology (Riccardo Rigon)
 */
public class LocalNode extends Component {

    @GuardedBy("this") private Key key;
    @GuardedBy("this") private Key parentKey; //!< the key of the HashMap of the parent
    @GuardedBy("this") private Integer layer; //!< the layer in the tree in which this node is located
    @GuardedBy("this") private Key leftChildKey; //!< the key of the HashMap of the left child
    @GuardedBy("this") private Key rightChildKey; //!< the key of the HashMap of the right child

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
    public LocalNode(final Key key, final Key leftChildKey, final Key rightChildKey, final int layer) {

        this.key = new Key(key);
        this.leftChildKey = new Key(leftChildKey);
        this.rightChildKey = new Key(rightChildKey);
        this.layer = new Integer(layer);

        validateState();

        this.parentKey = new Key(computeParentKey(key));

    }

    @Override
    public synchronized void put() {
        new UnsupportedOperationException("Method not implemented yet");    
    }

    @Override
    public synchronized void delete() {
        new UnsupportedOperationException("Method not implemented yet");
    }

    @Override
    public synchronized void setNewKey(final Key key) {

        validateKey(key);
        this.key = new Key(key);
        this.parentKey = new Key(computeParentKey(key));
        if (leftChildKey != null) this.leftChildKey = new Key(key.getDouble() * 2);
        if (rightChildKey != null) this.rightChildKey = new Key(key.getDouble() * 2 + 1);

    }

    @Override
    public synchronized Key getKey() {
        validateKey(key);
        return key;
    }

    /**
     * @brief Getter method to get the key of the left child
     *
     * @return The <tt>HashMap</tt> key of the left child
     */
    @Override
    public synchronized Key getLeftChildKey() {
        validateKey(leftChildKey);
        return leftChildKey;
    }

    /**
     * @brief Getter method to get the key of the right child
     *
     * @return The <tt>HashMap</tt> key of the right child
     */
    @Override
    public synchronized Key getRightChildKey() {
        validateKey(rightChildKey);
        return rightChildKey;
    }

    /**
     * @brief Getter method to get the key of the parent node
     *
     * @return The <tt>HashMap</tt> key of the parent node
     */
    @Override
    public synchronized Key getParentKey() {
        validateKey(parentKey);
        return parentKey;
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
  
        String tmp = "LocalNode - Parent Key = " + parentKey.getString();
        tmp += " Left Child = " + leftChildKey.getString() + " Right Child = " + rightChildKey.getString();
        tmp += " Layer = " + layer;
        return tmp;

    }

    @Override
    protected void validateState() {

        validateKey(parentKey);
        validateLayer(layer);
        validateKey(leftChildKey);
        validateKey(rightChildKey);

    }

    @Override
    protected Key computeParentKey(final Key key) {
        return new Key(Math.floor(key.getDouble() / 2));
    }

}
