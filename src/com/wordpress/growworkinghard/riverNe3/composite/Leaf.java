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
 * @todo add <strong>pre-conditions</strong> and
 *       <strong>post-conditions</strong>
 *
 * @todo make this class <em>ThreadSafe</em>
 *
 * @author Francesco Serafin, francesco.serafin.3@gmail.com
 * @version 1.0
 * @date October 13, 2015
 * @copyright GNU Public License v3
 */
public class Leaf extends Component {

    /**
     * @brief Default constructor
     */
    public Leaf() {}

    /**
     * @brief Alternative constructor which require all the states
     *
     * @param parentKey The <tt>HashMap</tt> key of the parent
     * @param layer The layer in the tree in which this node is located
     */
    public Leaf(final int parentKey, final int layer) {

        this.parentKey = new Integer(parentKey);
        this.layer = new Integer(layer);
    }

    @Override
    public void put() {
        new UnsupportedOperationException("Method not implemented yet");    
    }

    @Override
    public void delete() {
        new UnsupportedOperationException("Method not implemented yet");
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
    public Integer getLeftChildKey() {
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
    public Integer getRightChildKey() {
        return null; 
    }

    /**
     * @brief Simply overriding of the <code>toString</code> method
     *
     * @return The state variables of the object
     */
    @Override
    public String toString() {
  
        String tmp = "Leaf - Parent Key = " + parentKey + " Layer = " + layer;
        return tmp;

    }

}
