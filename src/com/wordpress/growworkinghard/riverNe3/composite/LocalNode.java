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
 * @brief class Localized Node
 *
 * @description The main purpose of this class is the representation of
 *              localized nodes, e.g. dams or monitoring points, inside the
 *              binary tree designed with the <strong>Composite Pattern</strong>
 *
 * @todo add <strong>pre-conditions</strong> and
 *       <strong>post-conditions</strong>
 *
 * @todo make this class <em>ThreadSafe</em>
 *
 * @author Francesco Serafin, francesco.serafin.3@gmail.com
 * @version 0.1
 * @date October 13, 2015
 * @copyright GNU Public License v3 AboutHydrology (Riccardo Rigon)
 */
public class LocalNode extends Component {

    private Integer leftChildKey; //!< the key of the HashMap of the left child
    private Integer rightChildKey; //!< the key of the HashMap of the right child
    private Integer parentKey; //!< the key of the HashMap of the parent
    private Integer layer; //!< the layer in the tree in which this node is located

    /**
     * @brief Default constructor
     */
    public LocalNode() {}

    /**
     * @brief Alternative constructor which requires all the states
     *
     * @param parentKey
     *            The <tt>HashMap</tt> key of the parent
     * @param leftChildKey
     *            The <tt>HashMap</tt> key of the left child
     * @param rightChildKey
     *            The <tt>HashMap</tt> key of the right child
     * @param layer
     *            The layer in the tree in which this node is located
     */
    public LocalNode(final int parentKey, final int leftChildKey, final int rightChildKey, final int layer) {

        this.parentKey = new Integer(parentKey);
        this.leftChildKey = new Integer(leftChildKey);
        this.rightChildKey = new Integer(rightChildKey);
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
     * @brief Setter method to set the key of the left child
     *
     * @param leftChildKey The <tt>HashMap</tt> key of the left child
     */
    @Override
    public void setLeftChildKey(final int leftChildKey) {
        this.leftChildKey = new Integer(leftChildKey);
    }

    /**
     * @brief Getter method to get the key of the left child
     *
     * @return The <tt>HashMap</tt> key of the left child
     */
    @Override
    public Integer getLeftChildKey() {
        return new Integer(leftChildKey); 
    }

    /**
     * @brief Setter method to set the key of the right child
     *
     * @param rightChildKey The <tt>HashMap</tt> key of the right child
     */
    @Override
    public void setRightChildKey(final int rightChildKey) {
        this.rightChildKey = new Integer(rightChildKey);
    }

    /**
     * @brief Getter method to get the key of the right child
     *
     * @return The <tt>HashMap</tt> key of the right child
     */
    @Override
    public Integer getRightChildKey() {
        return new Integer(rightChildKey); 
    }

    /**
     * @brief Setter method to set the key of the parent node
     *
     * @param parentKey The <tt>HashMap</tt> key of the parent node
     */
    @Override
    public void setParentKey(final int parentKey) {
        this.parentKey = new Integer(parentKey);
    }

    /**
     * @brief Getter method to get the key of the parent node
     *
     * @return The <tt>HashMap</tt> key of the parent node
     */
    @Override
    public Integer getParentKey() {
        return new Integer(parentKey); 
    }

    /**
     * @brief Setter method to set the layer of the node
     *
     * @param layer The layer of the node in the tree
     */
    @Override
    public void setLayer(final int layer) {
        this.layer = new Integer(layer);
    }

    /**
     * @brief Getter method to get the layer of the node
     *
     * @return The layer of the node in the tree
     */
    @Override
    public Integer getLayer() {
        return new Integer(layer); 
    }

    /**
     * @brief Simply overriding of the <code>toString</code> method
     *
     * @return The state variables of the object
     */
    @Override
    public String toString() {
  
        String tmp = "LocalNode - Parent Key = " + parentKey;
        tmp += " Left Child = " + leftChildKey + " Right Child = " + rightChildKey;
        tmp += " Layer = " + layer;
        return tmp;

    }

}

