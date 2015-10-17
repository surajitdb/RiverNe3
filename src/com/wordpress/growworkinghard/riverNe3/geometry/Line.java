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
 * @brief class Line
 *
 * @description This class extends the abstract class <tt>Geometry</tt> with the
 *              purpose of creating a <tt>Line</tt> object with a starting point
 *              <tt>startPoint</tt> and an ending point <tt>endPoint</tt>
 *
 * @author Francesco Serafin, francesco.serafin.3@gmail.com
 * @version 0.1
 * @date October 13, 2015
 * @copyright GNU Public License v3 AboutHydrology (Riccardo Rigon)
 */
public class Line extends Geometry {

    private Coordinate2D startPoint; //!< starting point
    private Coordinate2D endPoint; //!< ending point

    /**
     * @brief Default constructor
     */
    public Line() {}

    /**
     * @brief Setter method for the variable <tt>startPoint</tt>
     *
     * @param x
     *            The x coordinate
     * @param y
     *            The y coordinate
     */
    @Override
    public void setStartPoint(final double x, final double y) {
        startPoint = new Coordinate2D(x, y);
    }

    /**
     * @brief Setter method for the variable <tt>startPoint</tt>
     *
     * @param startPoint
     *            The coordinates of the starting point
     */
    @Override
    public void setStartPoint(final Coordinate2D startPoint) {
        this.startPoint = startPoint;
    }

    /**
     * @brief Getter method for the variable <tt>startPoint</tt>
     *
     * @return The coordinates of the starting point
     */
    @Override
    public Coordinate2D getStartPoint() {
        return new Coordinate2D(startPoint.x, startPoint.y);
    }

    /**
     * @brief Setter method for the variable <tt>endPoint</tt>
     *
     * @param x
     *            The x coordinate
     * @param y
     *            The y coordinate
     */
    @Override
    public void setEndPoint(final double x, final double y) {
        endPoint = new Coordinate2D(x, y);
    }

    /**
     * @brief Setter method for the variable <tt>endPoint</tt>
     *
     * @param endPoint
     *            The coordinates of the ending point
     */
    @Override
    public void setEndPoint(final Coordinate2D endPoint) {
        this.endPoint = endPoint;
    }

    /**
     * @brief Getter method for the variable <tt>endPoint</tt>
     *
     * @return The coordinates of the ending point
     */
    @Override
    public Coordinate2D getEndPoint() {
        return new Coordinate2D(endPoint.x, endPoint.y);
    }

}
