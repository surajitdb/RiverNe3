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

import net.jcip.annotations.ThreadSafe;

/**
 * @brief Abstract class to apply the <strong>Strategy Pattern</strong> to
 *        <tt>Geometry</tt> features
 *
 * @description This class collect the behaviours of different type of geometry
 *              elements. It is implemented following the idea of the
 *              <strong>Strategy Pattern</strong>, in order to easily add new
 *              features.
 *
 * @author sidereus, francesco.serafin.3@gmail.com
 * @version 0.1
 * @date October 13, 2015
 * @copyright GNU Public License v3 AboutHydrology (Riccardo Rigon)
 */
@ThreadSafe
public abstract class Geometry {


    /**
     * @brief Identify if the feature is root of a subtree
     *
     * @retval TRUE if the variable feature is <tt>root</tt>
     * @retval FALSE otherwise
     */
    abstract public boolean isRoot();

    /**
     * @brief Setter method for the variable <tt>root</tt>
     *
     * @param[in] root <code>true</code> if the feature is root a subtree
     */
    abstract public void setRoot(final boolean root);

    /**
     * @brief Setter method for the variable <tt>key</tt>
     *
     * @param[in] key the key of the node that is going to be used as index in
     *            the <code>ConcurrentHashMap</code>
     */
    abstract public void setKey(final Key key);

    /**
     * @brief Getter method for the variable <tt>key</tt>
     *
     * @return the key of the node that is going to be used as index in the
     *         <code>ConcurrentHashMap</code>
     */
    abstract public Key getKey();

    /**
     * @brief Setter method for the variable <tt>layer</tt>
     *
     * @param[in] layer the layer in the tree
     */
    abstract public void setLayer(final int layer);

    /**
     * @brief Getter method for the variable <tt>layer</tt>
     *
     * @return the layer in the tree
     */
    abstract public int getLayer();

    /**
     * @brief Setter method for the variable <tt>parentKey</tt>
     *
     * @param[in] parentKey the key which identify the parent node in the
     *            <code>ConcurrentHashMap</code>
     */
    abstract public void setParentKey(final Key parentKey);

    /**
     * @brief Getter method for the variable <tt>parentKey</tt>
     *
     * @return the key of the parent node, used in the
     *         <code>ConcurrentHashMap</code>
     */
    abstract public Key getParentKey();

    /**
     * @brief Validate the states of the <tt>Geometry</tt> object
     */
    abstract protected void validateState();

    /**
     * @brief Setter method for the variable <tt>point</tt>
     *
     * @description This method is not implemented yet. It might be implemented
     *              in a point class, which will have the state variable
     *              <tt>point</tt>
     *
     * @param[in] x The x coordinate
     * @param[in] y The y coordinate
     * @exception UnsupportedOperationException
     *                This method <strong>must</strong> be implemented in the
     *                subclass
     */
    public synchronized void setPoint(final double x, final double y) {
        throw new UnsupportedOperationException();
    }

    /**
     * @brief Setter method for the variable <tt>point</tt>
     *
     * @description This method is not implemented yet. It might be implemented
     *              in a point class, which will have the state variable
     *              <tt>point</tt>
     *
     * @param[in] point The coordinate of the point
     * @exception UnsupportedOperationException
     *                This method <strong>must</strong> be implemented in the
     *                subclass
     */
    public synchronized void setPoint(final Coordinate2D point) {
        throw new UnsupportedOperationException();
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
    public synchronized Coordinate2D getPoint() {
        throw new UnsupportedOperationException();
    }

    /**
     * @brief Setter method for the variable <tt>startPoint</tt>
     *
     * @description This method is not implemented yet. It might be implemented
     *              in a line, spline o polyline class, which will have the
     *              state variable <tt>startPoint</tt>
     *
     * @param[in] x The x coordinate
     * @param[in] y The y coordinate
     * @exception UnsupportedOperationException
     *                This method <strong>must</strong> be implemented in the
     *                subclass
     */
    public synchronized void setStartPoint(final double x, final double y) {
        throw new UnsupportedOperationException();
    }

    /**
     * @brief Setter method for the variable <tt>startPoint</tt>
     *
     * @description This method is not implemented yet. It might be implemented
     *              in a line, spline o polyline class, which will have the
     *              state variable <tt>startPoint</tt>
     *
     * @param[in] startPoint The coordinate of the starting point of the feature
     * @exception UnsupportedOperationException
     *                This method <strong>must</strong> be implemented in the
     *                subclass
     */
    public synchronized void setStartPoint(final Coordinate2D startPoint) {
        throw new UnsupportedOperationException();
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
    public synchronized Coordinate2D getStartPoint() {
        throw new NullPointerException("Operation not supported");
    }

    /**
     * @brief Setter method for the variable <tt>endPoint</tt>
     *
     * @description This method is not implemented yet. It might be implemented
     *              in a line, spline, o polyline class, which will have the
     *              state variable <tt>endPoint</tt>
     *
     * @param[in] x The x coordinate
     * @param[in] y The y coordinate
     * @exception UnsupportedOperationException
     *                This method <strong>must</strong> be implemented in the
     *                subclass
     */
    public synchronized void setEndPoint(final double x, final double y) {
        throw new UnsupportedOperationException();
    }

    /**
     * @brief Setter method for the variable <tt>endPoint</tt>
     *
     * @description This method is not implemented yet. It might be implemented
     *              in a line, spline, o polyline class, which will have the
     *              state variable <tt>endPoint</tt>
     *
     * @param[in] endPoint The coordinate of the ending point of the feature
     * @exception UnsupportedOperationException
     *                This method <strong>must</strong> be implemented in the
     *                subclass
     */
    public synchronized void setEndPoint(final Coordinate2D endPoint) {
        throw new UnsupportedOperationException();
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
    public synchronized Coordinate2D getEndPoint() {
        throw new UnsupportedOperationException();
    }

    /**
     * @brief Validation of the input key
     *
     * @param[in] key The input key to validate
     * @exception NullPointerException
     *                if the key is null
     */
    protected synchronized void validateKey(final Key key) {
        if (key == null)
            throw new NullPointerException("Component keys cannot be null");
    }

    /**
     * @brief Validation of the input layer
     *
     * @param[in] layer The input layer to validate
     * @exception NullPointerException
     *                if the layer is null
     */
    protected synchronized void validateLayer(final Integer layer) {
        if (layer == null)
            throw new NullPointerException("Layer cannot be null");
        if (layer < 0)
            throw new IllegalArgumentException("Layer less than 0 not admitted");
    }

    /**
     * @brief Validation of the input point
     *
     * @param[in] point The input point to validate
     * @exception NullPointerException if the point is null
     */
    protected synchronized void validatePoint(final Coordinate2D point) {
        if (point == null)
            throw new NullPointerException("Point cannot be null");
    }

}
