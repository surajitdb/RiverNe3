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

/**
 * @brief Abstract class Geometry
 *
 * @description This class collect the behaviours of different type of geometry elements. It
 * is implemented following the idea of the Strategy Pattern, then you can
 * easily add new features.
 *
 * @author Francesco Serafin, francesco.serafin.3@gmail.com
 * @version 0.1
 * @date October 13, 2015
 * @copyright GNU Public License v3 AboutHydrology (Riccardo Rigon)
 */
public abstract class Geometry {

    private boolean root; //!< to identify if the element is the root of the subtree
    // private int layer;
    private int key; //!< the key to use in the ConcurrentHashMap
    private int layer;
    private int parentKey; //!< the key in the ConcurrentHashMap of the parent node

    /**
     * @brief Identify if the feature is root of a subtree
     *
     * @return the boolean variable <tt>root</tt>
     */
    public boolean isRoot() {
        return root;
    }

    /**
     * @brief Setter method for the variable <tt>root</tt> 
     *
     * @param root
     *            boolean <code>true</code> if the feature is root a subtree
     */
    public void setRoot(final boolean root) {
        this.root = root;
    }

    /**
     * @brief Setter method for the variable <tt>key</tt>
     *
     * @param key
     *            the key that is going to be used as index in the
     *            <code>ConcurrentHashMap</code>
     */
    public void setKey(final int key) {
        this.key = key;
    }

    /**
     * @brief Getter method for the variable <tt>key</tt>
     *
     * @return the key that is going to be used as index in the
     *         <code>ConcurrentHashMap</code>
     */
    public int getKey() {
        return key;
    }

    /**
     * @brief Setter method for the variable <tt>layer</tt>
     *
     * @param layer
     *            the layer in the tree
     */
    public void setLayer(final int layer) {
        this.layer = layer;
    }

    /**
     * @brief Getter method for the variable <tt>layer</tt>
     *
     * @return the layer in the tree
     */
    public int getLayer() {
        return layer;
    }

    /**
     * @brief Setter method for the variable <tt>parentKey</tt>
     *
     * @param parentKey
     *            the key which identify the parent node in the
     *            <code>ConcurrentHashMap</code>
     */
    public void setParentKey(final int parentKey) {
        this.parentKey = parentKey;
    }

    /**
     * @brief Getter method for the variable <tt>parentKey</tt>
     *
     * @return the key of the parent node, used in the
     *         <code>ConcurrentHashMap</code>
     */
    public int getParentKey() {
        return parentKey;
    }

    /**
     * @brief Setter method for the variable <tt>point</tt>
     *
     * @description This method is not implemented yet. It might be implemented
     *              in a point class, which will have the state variable
     *              <tt>point</tt>
     *
     * @param x
     *            The x coordinate
     * @param y
     *            The y coordinate
     * @exception UnsupportedOperationException
     *                This method <strong>must</strong> be implemented in the
     *                subclass
     */
    public void setPoint(final double x, final double y) {
        new UnsupportedOperationException();
    }

    /**
     * @brief Setter method for the variable <tt>point</tt>
     *
     * @description This method is not implemented yet. It might be implemented
     *              in a point class, which will have the state variable
     *              <tt>point</tt>
     *
     * @param point
     *            The coordinate of the point
     * @exception UnsupportedOperationException
     *                This method <strong>must</strong> be implemented in the
     *                subclass
     */
    public void setPoint(final Coordinate2D point) {
        new UnsupportedOperationException();
    }

    /**
     * @brief Getter method for the variable <tt>point</tt>
     *
     * @description This method is not implemented yet. It might be implemented
     *              in a point class, which will have the state variable
     *              <tt>point</tt>
     *
     * @return the variable <tt>point</tt>
     * @exception UnsupportedOperationException
     *                This method <strong>must</strong> be implemented in the
     *                subclass
     */
    public Coordinate2D getPoint() {
        new UnsupportedOperationException();
        return new Coordinate2D(0.0, 0.0);
    }

    /**
     * @brief Setter method for the variable <tt>startPoint</tt>
     *
     * @description This method is not implemented yet. It might be implemented
     *              in a line, spline o polyline class, which will have the
     *              state variable <tt>startPoint</tt>
     *
     * @param x
     *            The x coordinate
     * @param y
     *            The y coordinate
     * @exception UnsupportedOperationException
     *                This method <strong>must</strong> be implemented in the
     *                subclass
     */
    public void setStartPoint(final double x, final double y) {
        new UnsupportedOperationException();
    }

    /**
     * @brief Setter method for the variable <tt>startPoint</tt>
     *
     * @description This method is not implemented yet. It might be implemented
     *              in a line, spline o polyline class, which will have the
     *              state variable <tt>startPoint</tt>
     *
     * @param startPoint
     *            The coordinate of the starting point of the feature
     * @exception UnsupportedOperationException
     *                This method <strong>must</strong> be implemented in the
     *                subclass
     */
    public void setStartPoint(final Coordinate2D startPoint) {
        new UnsupportedOperationException();
    }

    /**
     * @brief Getter method for the variable <tt>startPoint</tt>
     *
     * @description This method is not implemented yet. It might be implemented
     *              in a line, spline, o polyline class, which will have the
     *              state variable <tt>startPoint</tt>
     *
     * @return the variable <tt>startPoint</tt>
     * @exception UnsupportedOperationException
     *                This method <strong>must</strong> be implemented in the
     *                subclass
     */
    public Coordinate2D getStartPoint() {
        new UnsupportedOperationException();
        return new Coordinate2D(0.0, 0.0);
    }

    /**
     * @brief Setter method for the variable <tt>endPoint</tt>
     *
     * @description This method is not implemented yet. It might be implemented
     *              in a line, spline, o polyline class, which will have the
     *              state variable <tt>endPoint</tt>
     *
     * @param x
     *            The x coordinate
     * @param y
     *            The y coordinate
     * @exception UnsupportedOperationException
     *                This method <strong>must</strong> be implemented in the
     *                subclass
     */
    public void setEndPoint(final double x, final double y) {
        new UnsupportedOperationException();
    }

    /**
     * @brief Setter method for the variable <tt>endPoint</tt>
     *
     * @description This method is not implemented yet. It might be implemented
     *              in a line, spline, o polyline class, which will have the
     *              state variable <tt>endPoint</tt>
     *
     * @param endPoint
     *            The coordinate of the ending point of the feature
     * @exception UnsupportedOperationException
     *                This method <strong>must</strong> be implemented in the
     *                subclass
     */
    public void setEndPoint(final Coordinate2D endPoint) {
        new UnsupportedOperationException();
    }

    /**
     * @brief Getter method for the variable <tt>endPoint</tt>
     *
     * @description This method is not implemented yet. It might be implemented
     *              in a line, spline o polyline class, which will have the
     *              state variable <tt>endPoint</tt>
     *
     * @return the variable <tt>endPoint</tt>
     * @exception UnsupportedOperationException
     *                This method <strong>must</strong> be implemented in the
     *                subclass
     */
    public Coordinate2D getEndPoint() {
        new UnsupportedOperationException();
        return new Coordinate2D(0.0, 0.0);
    }

}
