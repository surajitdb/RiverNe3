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
import com.google.common.collect.FluentIterable;
import com.wordpress.growworkinghard.riverNe3.composite.key.Key;
import com.wordpress.growworkinghard.riverNe3.geometry.Point;

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
    @GuardedBy("this") private Coordinate2D point;
    @GuardedBy("this") private BinaryTreeTraverser<Component> traverser;

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
    public LocalNode(final Point root, final Key leftChildKey, final Key rightChildKey) {
        getInstance(root, leftChildKey, rightChildKey);
    }

    public synchronized void setNewKey(final Key key) {

        validateKey(key);
        this.key = new Key(key);
        this.parentKey = new Key(computeParentKey(key));
        if (leftChildKey != null) this.leftChildKey = new Key(key.getDouble() * 2);
        if (rightChildKey != null) this.rightChildKey = new Key(key.getDouble() * 2 + 1);

    }

    public synchronized Key getKey() {
        validateKey(key);
        return key;
    }

    /**
     * @brief Getter method to get the key of the left child
     *
     * @return The <tt>HashMap</tt> key of the left child
     */
    public synchronized Key getLeftChildKey() {
        validateKey(leftChildKey);
        return leftChildKey;
    }

    /**
     * @brief Getter method to get the key of the right child
     *
     * @return The <tt>HashMap</tt> key of the right child
     */
    public synchronized Key getRightChildKey() {
        // validateKey(rightChildKey); this key might be null
        return rightChildKey;
    }

    /**
     * @brief Getter method to get the key of the parent node
     *
     * @return The <tt>HashMap</tt> key of the parent node
     */
    public synchronized Key getParentKey() {
        validateKey(parentKey);
        return parentKey;
    }

    public synchronized Coordinate2D getStartPoint() {
        validateCoordinate(point);
        return new Coordinate2D(point.x, point.y);
    }

    public synchronized Coordinate2D getEndPoint() {
        validateCoordinate(point);
        return new Coordinate2D(point.x, point.y);
    }

    /**
     * @brief Setter method to set the layer of the node
     *
     * @param[in] layer The layer of the node in the tree
     */
    public synchronized void setLayer(final int layer) {
        validateLayer(layer);
        this.layer = layer;
    }

    /**
     * @brief Getter method to get the layer of the node
     *
     * @return The layer of the node in the tree
     */
    public synchronized Integer getLayer() {
        validateLayer(layer);
        return new Integer(layer);
    }

    public synchronized Coordinate2D getPoint() {
        validateCoordinate(point);
        return new Coordinate2D(point.x, point.y);
    }

    public synchronized void setTraverser(final BinaryTreeTraverser<Component> traverser) {

        this.traverser = traverser;

    }

    public synchronized List<Component> preOrderTraversal() {

        FluentIterable<Component> iterator = traverser.preOrderTraversal(this);
        return iterator.toList();

    }

    public synchronized List<Component> postOrderTraversal() {

        FluentIterable<Component> iterator = traverser.postOrderTraversal(this);
        return iterator.toList();

    }

    public synchronized List<Component> inOrderTraversal() {

        FluentIterable<Component> iterator = traverser.inOrderTraversal(this);
        return iterator.toList();

    }

    /**
     * @brief Simply overriding of the <code>toString</code> method
     *
     * @return The state variables of the object
     */
    @Override
    public String toString() {
  
        String tmp = "LocalNode";
        tmp += " - Key = " + key.getString();
        tmp += " Parent Key = " + parentKey.getString();

        if (leftChildKey != null)
            tmp += " Left Child = " + leftChildKey.getString();
        if (rightChildKey != null)
            tmp += " Right Child = " + rightChildKey.getString();

        tmp += " Layer = " + layer;

        return tmp;

    }

    private void getInstance(final Point root, final Key leftChildKey, final Key rightChildKey) {

        if (statesAreNull()) {
            synchronized(this) {
                if (statesAreNull()) {
                    this.key = new Key(root.getKey());
                    this.leftChildKey = leftChildKey;
                    this.rightChildKey = rightChildKey;
                    this.layer = new Integer(root.getLayer());
                    this.parentKey = new Key(root.getParentKey());
                    this.point = new Coordinate2D(root.getPoint().x, root.getPoint().y);

                    validateState();
                }

            }

        }

    }

    protected boolean statesAreNull() {

        if (this.key == null &&
            this.layer == null &&
            this.leftChildKey == null &&
            this.rightChildKey == null &&
            this.parentKey == null &&
            this.point == null) return true;

        return false;

    }

    protected void validateState() {

        validateKey(key);
        validateKey(parentKey);
        validateLayer(layer);
        validateKey(leftChildKey);
        // validateKey(rightChildKey); this key might be null
        validateCoordinate(point);

    }

    protected Key computeParentKey(final Key key) {
        return new Key(Math.floor(key.getDouble() / 2));
    }

}
