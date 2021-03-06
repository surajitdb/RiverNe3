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
package com.wordpress.growworkinghard.riverNe3.composite.entity;

import org.geotools.graph.util.geom.Coordinate2D;

import net.jcip.annotations.Immutable;

@Immutable
public class Basin extends Entity {

    final private River river;
    final private Double area;

    public Basin (final River river, final Double area) {

        this.river = river;
        this.area = area;

    }

    public Double getBasinArea() {
        return area;
    }

    @Override
    public Coordinate2D getStartPoint() {
        return river.getStartPoint();
    }

    @Override
    public Coordinate2D getEndPoint() {
        return river.getEndPoint();
    }

    @Override
    public Coordinate2D getPoint() {

        // implement the computing of the centroid
        return super.getPoint();

    }

}
