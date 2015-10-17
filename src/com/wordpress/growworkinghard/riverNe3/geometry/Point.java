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
 * @brief class Point
 *
 * @description This class extends the abstrac class <tt>Geometry</tt> with the purpose of
 * creating a <tt>Point</tt> object mainly including its coordinates
 *
 * @author Francesco Serafin, francesco.serafin.3@gmail.com
 * @version 0.1
 * @date October 13, 2015
 * @copyright GNU Public License v3 AboutHydrology (Riccardo Rigon)
 */
public class Point extends Geometry {

    private Coordinate2D point; //!< coordinates

    /**
     * @brief Default constructor
     */
    public Point() {}

    /**
     * @brief Setter method for the variable <tt>point</tt>
     *
     * @param x
     *            The x coordinate
     * @param y
     *            The y coordinate
     */
    @Override
    public void setPoint(final double x, final double y) {
        point = new Coordinate2D(x, y);
    }

    /**
     * @brief Setter method for the variable <tt>point</tt>
     *
     * @param point
     *            The coordinates of the point
     */
    @Override
    public void setPoint(final Coordinate2D point) {
        this.point = point;
    }

    /**
     * @brief Getter method for the variable <tt>point</tt>
     *
     * @return The coordinates of the point
     */
    public Coordinate2D getPoint() {
        return point;
    }

}
