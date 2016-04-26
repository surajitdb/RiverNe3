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

import java.util.HashMap;

import org.geotools.graph.util.geom.Coordinate2D;

import net.jcip.annotations.Immutable;

@Immutable
public class Basin extends Entity {

    final private Ground ground;
    final private Meteo meteo;
    final private Double area;
    final private double a = 752.3543670;
    final private double b = 1.75744;
    final private String dischargeModelName;
    final private String evapotranspirationModelName;
    final private String odeSolverModelName;


    public Basin (final Ground ground, final Meteo meteo, final Double area) {

        this.ground = ground;
        this.meteo = meteo;
        this.area = area;

        this.dischargeModelName = "NonLinearReservoir";
        this.evapotranspirationModelName = "AET";
        this.odeSolverModelName = "dp853";

    }

    public Double getBasinArea() {
        return area;
    }

    public HashMap<Integer, double[]> getPrecipitation() {
        return meteo.getPrecipitation();
    }

    public HashMap<Integer, double[]> getEvapotranspiration() {
        return meteo.getEvapotranspiration();
    }

    public double getPoreVolumeInRootZone() {
        return ground.getPoreVolumeInRootZone();
    }

    public double getWaterStorageMaxValue() {
        return ground.getWaterStorageMaxValue();
    }

    public double getRe() {
        return ground.getRe();
    }

    @Override
    public Coordinate2D getStartPoint() {
        return ground.getStartPoint();
    }

    @Override
    public Coordinate2D getEndPoint() {
        return ground.getEndPoint();
    }

    @Override
    public Coordinate2D getPoint() {

        // implement the computing of the centroid
        return super.getPoint();

    }

    public double getParameter(final String inputParameter) {
        return (inputParameter.compareTo("a") == 0) ? a : b;
    }

}
