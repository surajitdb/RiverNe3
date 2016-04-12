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
package com.wordpress.growworkinghard.riverNe3.junitTests;

import java.util.HashMap;

import org.geotools.graph.util.geom.Coordinate2D;
import org.junit.Test;

import com.wordpress.growworkinghard.riverNe3.composite.Component;
import com.wordpress.growworkinghard.riverNe3.composite.Leaf;
import com.wordpress.growworkinghard.riverNe3.composite.Node;
import com.wordpress.growworkinghard.riverNe3.composite.entity.Basin;
import com.wordpress.growworkinghard.riverNe3.composite.entity.Entity;
import com.wordpress.growworkinghard.riverNe3.composite.entity.Ground;
import com.wordpress.growworkinghard.riverNe3.composite.entity.Meteo;
import com.wordpress.growworkinghard.riverNe3.composite.entity.River;
import com.wordpress.growworkinghard.riverNe3.composite.key.BinaryConnections;
import com.wordpress.growworkinghard.riverNe3.composite.key.Connections;
import com.wordpress.growworkinghard.riverNe3.composite.key.Key;
import com.wordpress.growworkinghard.riverNe3.simulations.Results;

public class NodeTest {

    @Test
    public void node() {

        Component parent = createNode("", 5);
        Component node = createNode("", 10);
        Component child1 = createNode("", 20);
        Component child2 = createNode("", 21);
        Results result1 = createResults();
        Results result2 = createResults();

        node.notify(child1.getConnections().getID(), result1);
        node.notify(child2.getConnections().getID(), result2);

        if (node.isReadyForSimulation())
            node.runSimulation(parent);
        else System.out.println("ERROR!!!"); 
    }

    private Component createNode(final String nodeType, final Integer id) {

        Key nodeID = new Key(id);
        Connections connKeys = new BinaryConnections(nodeID);
        Integer layer = 3;
        Entity basin = createBasin();

        if (nodeType.equals("Leaf")) return new Leaf(connKeys, layer, basin);
        else return new Node(connKeys, layer, basin);

    }

    private Basin createBasin() {

        Ground ground = createGround();
        Meteo meteo = createMeteo();
        Double area = 30.0;

        return new Basin(ground, meteo, area);

    }

    private Ground createGround() {

        Coordinate2D startingPoint = new Coordinate2D(10, 10);
        Coordinate2D endingPoint = new Coordinate2D(20, 20);
        River river = new River(startingPoint, endingPoint);

        Double poreVolumeInRootZone = 1.0;
        Double waterStorageMaxValue = 0.0057;
        return new Ground(river, poreVolumeInRootZone, waterStorageMaxValue);

    }

    private Meteo createMeteo() {

        HashMap<Integer, double[]> precipitation = new HashMap<Integer, double[]>();
        HashMap<Integer, double[]> evapotranspiration = new HashMap<Integer, double[]>();

        double[] precipData = {1.0, 2.0};
        double[] evapotData = {0.03, 0.06};

        Integer station = 1;

        precipitation.put(station, precipData);
        evapotranspiration.put(station, evapotData);

        return new Meteo(precipitation, evapotranspiration);

    }

    private Results createResults() {

        HashMap<Integer, double[]> result1 = new HashMap<Integer, double[]>();
        HashMap<Integer, double[]> result2 = new HashMap<Integer, double[]>();

        Integer station = 1;
        double[] discharge = {3.0, 3.5};

        result2.put(station, discharge);

        Results result = new Results();

        result.add(result1);
        result.add(result2);
        result.add(result1);
        result.add(result1);
        result.add(result1);

        return result;

    }

}

