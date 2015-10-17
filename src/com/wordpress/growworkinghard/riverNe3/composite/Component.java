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
 * @brief class Component
 *
 * @description This is the abstract class of the <strong>Composite
 *              Pattern</strong>, which has been used to realize the structure
 *              for the binary tree
 *
 * @author Francesco Serafin, francesco.serafin.3@gmail.com
 * @version 1.0
 * @date October 13, 2015
 * @copyright GNU Public License v3
 */
public abstract class Component {

    protected Integer parentKey; //!< the key of the HashMap of the parent
    protected Integer layer; //!< the layer in the tree in which this node is located

    abstract public void put();

    abstract public void delete();

    abstract public Integer getLeftChildKey();

    abstract public Integer getRightChildKey();

    public void setLeftChildKey(final int leftChildKey) {
        new UnsupportedOperationException();    
    }

    public void setRightChildKey(final int rightChildKey) {
        new UnsupportedOperationException();    
    }

    /**
     * @brief Setter method to set the key of the parent node
     *
     * @param parentKey The <tt>HashMap</tt> key of the parent node
     */
    public void setParentKey(final int parentKey) {
        this.parentKey = parentKey;
    }

    /**
     * @brief Getter method to get the key of the parent node
     *
     * @return The <tt>HashMap</tt> key of the parent node
     */
    public Integer getParentKey() {
        return new Integer(parentKey); 
    }

    /**
     * @brief Setter method to set the layer of the node
     *
     * @param layer The layer of the node in the tree
     */
    public void setLayer(final int layer) {
        this.layer = layer;
    }

    /**
     * @brief Getter method to get the layer of the node
     *
     * @return The layer of the node in the tree
     */
    public Integer getLayer() {
        return new Integer(layer); 
    }

}
