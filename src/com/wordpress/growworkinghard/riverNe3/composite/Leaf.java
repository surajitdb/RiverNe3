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
 * @brief class Leaf
 *
 * @description The main purpose of this class is the representation of
 *              subbasins that don't have children. Instead this class has only
 *              two <tt>state variables</tt>
 *              <ul>
 *                  <li><tt>parent key</tt></li>
 *                  <li><tt>layer</tt></li>
 *              </ul>
 *
 * @todo make this class <em>ThreadSafe</em>
 *
 * @author Francesco Serafin, francesco.serafin.3@gmail.com
 * @version 0.1
 * @date October 13, 2015
 * @copyright GNU Public License v3 AboutHydrology (Riccardo Rigon)
 */
public class Leaf extends Component {

    @GuardedBy("this") private Key key;
    @GuardedBy("this") private Key parentKey; //!< the key of the HashMap of the parent
    @GuardedBy("this") private Integer layer; //!< the layer in the tree in which this node is located

    /**
     * @brief Alternative constructor which require all the states
     *
     * @param[in] parentKey
     *            The <tt>HashMap</tt> key of the parent
     * @param[in] layer
     *            The layer in the tree in which this node is located
     */
    public Leaf(final Key key, final int layer) {

        this.key = new Key(key);
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

    }

    @Override
    public synchronized Key getKey() {
        validateKey(key);
        return key;
    }

    /**
     * @brief Getter method to get the key of the left child
     *
     * @description This getter is useful because returning <code>null</code>
     *              value easily allows to identify which node is a leaf
     *
     * @return <code>null</code> value because this object is a leaf
     */
    @Override
    public synchronized Key getLeftChildKey() {
        return null; 
    }

    /**
     * @brief Getter method to get the key of the right child
     *
     * @description This getter is useful beacuse returning <code>null</code>
     *              vaule easily allows to identify which node is a leaf
     *
     * @return <code>null</code> value because this object is a leaf
     */
    @Override
    public synchronized Key getRightChildKey() {
        return null; 
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
  
        String tmp = "Leaf - Parent Key = " + parentKey.getString() + " Layer = " + layer;
        return tmp;

    }

    @Override
    protected void validateState() {

        validateKey(key);
        validateLayer(layer);

    }

    @Override
    protected Key computeParentKey(final Key key) {
        return new Key(Math.floor(key.getDouble() / 2));
    }

}
