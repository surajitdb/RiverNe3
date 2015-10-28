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
package com.wordpress.growworkinghard.riverNe3.geometry;

import org.geotools.graph.util.geom.Coordinate2D;

import com.wordpress.growworkinghard.riverNe3.composite.key.Key;

import net.jcip.annotations.GuardedBy;

/**
 * @brief class Line
 *
 * @description This class extends the abstract class <tt>Geometry</tt> with the
 *              purpose of creating a <tt>Line</tt> object with a starting point
 *              <tt>startPoint</tt> and an ending point <tt>endPoint</tt>
 *
 * @todo make this class <em>ThreadSafe</em>
 *
 * @todo add <strong>pre-conditions</strong> and
 *       <strong>post-conditions</strong>
 *
 * @author Francesco Serafin, francesco.serafin.3@gmail.com
 * @version 0.1
 * @date October 13, 2015
 * @copyright GNU Public License v3 AboutHydrology (Riccardo Rigon)
 */
public class Line extends Geometry {

    @GuardedBy("this") private boolean root; //!< to identify if the element is the root of the subtree
    @GuardedBy("this") private Key key; //!< the key to use in the ConcurrentHashMap
    @GuardedBy("this") private Key parentKey; //!< the key in the ConcurrentHashMap of the parent node
    @GuardedBy("this") private Integer layer;
    @GuardedBy("this") private Coordinate2D startPoint; //!< starting point
    @GuardedBy("this") private Coordinate2D endPoint; //!< ending point

    public Line() {}

    public Line(final boolean root, final Key key, final Key parentKey, final int layer, final Coordinate2D startPoint, final Coordinate2D endPoint) {

        this.root = new Boolean(root);
        this.key = new Key(key);
        this.parentKey = new Key(parentKey);
        this.layer = new Integer(layer);
        this.startPoint = new Coordinate2D(startPoint.x, startPoint.y);
        this.endPoint = new Coordinate2D(endPoint.x, endPoint.y);

        validateState();

    }

    /**
     * @brief Identify if the feature is root of a subtree
     *
     * @return the boolean variable <tt>root</tt>
     */
    public synchronized boolean isRoot() {
        return root;
    }

    /**
     * @brief Setter method for the variable <tt>root</tt>
     *
     * @param[in] root
     *            boolean <code>true</code> if the feature is root a subtree
     */
    public synchronized void setRoot(final boolean root) {
        this.root = new Boolean(root);
    }

    /**
     * @brief Setter method for the variable <tt>key</tt>
     *
     * @param[in] key
     *            the key that is going to be used as index in the
     *            <code>ConcurrentHashMap</code>
     */
    public synchronized void setKey(final Key key) {
        this.key = new Key(key);
    }

    /**
     * @brief Getter method for the variable <tt>key</tt>
     *
     * @return the key that is going to be used as index in the
     *         <code>ConcurrentHashMap</code>
     */
    public synchronized Key getKey() {
        return key;
    }

    /**
     * @brief Setter method for the variable <tt>layer</tt>
     *
     * @param[in] layer
     *            the layer in the tree
     */
    public synchronized void setLayer(final int layer) {
        this.layer = new Integer(layer);
    }

    /**
     * @brief Getter method for the variable <tt>layer</tt>
     *
     * @return the layer in the tree
     */
    public synchronized int getLayer() {
        return new Integer(layer);
    }

    /**
     * @brief Setter method for the variable <tt>parentKey</tt>
     *
     * @param[in] parentKey
     *            the key which identify the parent node in the
     *            <code>ConcurrentHashMap</code>
     */
    public synchronized void setParentKey(final Key parentKey) {
        this.parentKey = new Key(parentKey);
    }

    /**
     * @brief Getter method for the variable <tt>parentKey</tt>
     *
     * @return the key of the parent node, used in the
     *         <code>ConcurrentHashMap</code>
     */
    public synchronized Key getParentKey() {
        return parentKey;
    }

    /**
     * @brief Setter method for the variable <tt>startPoint</tt>
     *
     * @param[in] x
     *            The x coordinate
     * @param[in] y
     *            The y coordinate
     */
    public synchronized void setStartPoint(final double x, final double y) {
        startPoint = new Coordinate2D(x, y);
    }

    /**
     * @brief Setter method for the variable <tt>startPoint</tt>
     *
     * @param[in] startPoint
     *            The coordinates of the starting point
     */
    public synchronized void setStartPoint(final Coordinate2D startPoint) {
        this.startPoint = new Coordinate2D(startPoint.x, startPoint.y);
    }

    /**
     * @brief Getter method for the variable <tt>startPoint</tt>
     *
     * @return The coordinates of the starting point
     */
    public synchronized Coordinate2D getStartPoint() {
        return new Coordinate2D(startPoint.x, startPoint.y);
    }

    /**
     * @brief Setter method for the variable <tt>endPoint</tt>
     *
     * @param[in] x
     *            The x coordinate
     * @param[in] y
     *            The y coordinate
     */
    public synchronized void setEndPoint(final double x, final double y) {
        endPoint = new Coordinate2D(x, y);
    }

    /**
     * @brief Setter method for the variable <tt>endPoint</tt>
     *
     * @param[in] endPoint
     *            The coordinates of the ending point
     */
    public synchronized void setEndPoint(final Coordinate2D endPoint) {
        this.endPoint = new Coordinate2D(endPoint.x, endPoint.y);
    }

    /**
     * @brief Getter method for the variable <tt>endPoint</tt>
     *
     * @return The coordinates of the ending point
     */
    public synchronized Coordinate2D getEndPoint() {
        return new Coordinate2D(endPoint.x, endPoint.y);
    }

    protected void validateState() {

        validateKey(key);
        validateKey(parentKey);
        validateLayer(layer);
        validatePoint(startPoint);
        validatePoint(endPoint);

    }

}
