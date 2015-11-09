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
import net.jcip.annotations.ThreadSafe;

/**
 * @brief class Line
 *
 * @description This class extends the abstract class <tt>Geometry</tt> with the
 *              purpose of creating a <tt>Line</tt> object with a starting point
 *              <tt>startPoint</tt> and an ending point <tt>endPoint</tt>
 *              <p>
 *              This class is <em>ThreadSafe</em> because every method is
 *              synchronized and state variables are guarded by the intrinsick
 *              lock.
 *              </p>
 *
 * @author Francesco Serafin, francesco.serafin.3@gmail.com
 * @version 0.1
 * @date October 13, 2015
 * @copyright GNU Public License v3 AboutHydrology (Riccardo Rigon)
 */
@ThreadSafe
public class Line extends Geometry {

    @GuardedBy("this") private boolean root; //!< if the element is the root of the subtree
    @GuardedBy("this") private Key key; //!< key to use in the ConcurrentHashMap
    @GuardedBy("this") private Key parentKey; //!< key in the ConcurrentHashMap for parent node
    @GuardedBy("this") private Integer layer; //!< layer of the node in the tree
    @GuardedBy("this") private Coordinate2D startPoint; //!< starting point of the stream in the subbasin
    @GuardedBy("this") private Coordinate2D endPoint; //!< ending point of the stream in the subbasin

    /**
     * @brief Constructor
     *
     * @param[in] root If the element is the root of the subtree
     * @param[in] key The key of the element
     * @param[in] parentKey The key of the parent node
     * @param[in] layer The layer of the node in the tree
     * @param[in] startPoint The starting point of the stream in the subbasin
     * @param[in] endPoint The ending point of the stream in the subbasin
     */
    public Line(final boolean root, final Key key, final Key parentKey, final int layer, final Coordinate2D startPoint, final Coordinate2D endPoint) {

        this.root = new Boolean(root);
        this.key = key;
        this.parentKey = parentKey;
        this.layer = new Integer(layer);
        this.startPoint = new Coordinate2D(startPoint.x, startPoint.y);
        this.endPoint = new Coordinate2D(endPoint.x, endPoint.y);

        validateState(); // precondition

    }

    /**
     * {@inheritDoc}
     *
     * @see Geometry#isRoot()
     */
    public synchronized boolean isRoot() {
        return root;
    }

    /**
     * {@inheritDoc}
     *
     * @see Geometry#setRoot(final boolean)
     */
    public synchronized void setRoot(final boolean root) {
        this.root = new Boolean(root);
    }

    /**
     * {@inheritDoc}
     *
     * @see Geometry#setKey(final Key)
     */
    public synchronized void setKey(final Key key) {
        this.key = key;
    }

    /**
     * {@inheritDoc}
     *
     * @see Geometry#getKey()
     */
    public synchronized Key getKey() {
        return key;
    }

    /**
     * {@inheritDoc}
     *
     * @see Geometry#setLayer(final int)
     */
    public synchronized void setLayer(final int layer) {
        this.layer = new Integer(layer);
    }

    /**
     * {@inheritDoc}
     *
     * @see Geometry#getLayer()
     */
    public synchronized int getLayer() {
        return new Integer(layer);
    }

    /**
     * {@inheritDoc}
     *
     * @see Geometry#setParentKey(final Key)
     */
    public synchronized void setParentKey(final Key parentKey) {
        this.parentKey = parentKey;
    }

    /**
     * {@inheritDoc}
     *
     * @see Geometry#getParentKey()
     */
    public synchronized Key getParentKey() {
        return parentKey;
    }

    /**
     * @brief Setter method for the variable <tt>startPoint</tt>
     *
     * @param[in] x The x coordinate
     * @param[in] y The y coordinate
     */
    @Override
    public synchronized void setStartPoint(final double x, final double y) {
        startPoint = new Coordinate2D(x, y);
    }

    /**
     * @brief Setter method for the variable <tt>startPoint</tt>
     *
     * @param[in] startPoint The starting point
     */
    @Override
    public synchronized void setStartPoint(final Coordinate2D startPoint) {
        this.startPoint = new Coordinate2D(startPoint.x, startPoint.y);
    }

    /**
     * @brief Getter method for the variable <tt>startPoint</tt>
     *
     * @return The starting point
     */
    @Override
    public synchronized Coordinate2D getStartPoint() {
        return new Coordinate2D(startPoint.x, startPoint.y);
    }

    /**
     * @brief Setter method for the variable <tt>endPoint</tt>
     *
     * @param[in] x The x coordinate
     * @param[in] y The y coordinate
     */
    @Override
    public synchronized void setEndPoint(final double x, final double y) {
        endPoint = new Coordinate2D(x, y);
    }

    /**
     * @brief Setter method for the variable <tt>endPoint</tt>
     *
     * @param[in] endPoint The ending point
     */
    @Override
    public synchronized void setEndPoint(final Coordinate2D endPoint) {
        this.endPoint = new Coordinate2D(endPoint.x, endPoint.y);
    }

    /**
     * @brief Getter method for the variable <tt>endPoint</tt>
     *
     * @return The ending point
     */
    @Override
    public synchronized Coordinate2D getEndPoint() {
        return new Coordinate2D(endPoint.x, endPoint.y);
    }

    /**
     * {@inheritDoc}
     *
     * @see Geometry#validateState()
     */
    protected void validateState() {
        validateKey(key);
        validateKey(parentKey);
        validateLayer(layer);
        validatePoint(startPoint);
        validatePoint(endPoint);
    }

}
