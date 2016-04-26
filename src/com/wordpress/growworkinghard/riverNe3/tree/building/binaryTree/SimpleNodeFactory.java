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
package com.wordpress.growworkinghard.riverNe3.tree.building.binaryTree;

import java.util.HashMap;

import org.geotools.graph.util.geom.Coordinate2D;

import com.wordpress.growworkinghard.riverNe3.composite.Component;
import com.wordpress.growworkinghard.riverNe3.composite.GhostNode;
import com.wordpress.growworkinghard.riverNe3.composite.Leaf;
import com.wordpress.growworkinghard.riverNe3.composite.Node;
import com.wordpress.growworkinghard.riverNe3.composite.entity.Basin;
import com.wordpress.growworkinghard.riverNe3.composite.entity.Entity;
import com.wordpress.growworkinghard.riverNe3.composite.entity.GhostBasin;
import com.wordpress.growworkinghard.riverNe3.composite.entity.Ground;
import com.wordpress.growworkinghard.riverNe3.composite.entity.Meteo;
import com.wordpress.growworkinghard.riverNe3.composite.entity.River;
import com.wordpress.growworkinghard.riverNe3.composite.key.BinaryConnections;
import com.wordpress.growworkinghard.riverNe3.composite.key.Connections;
import com.wordpress.growworkinghard.riverNe3.composite.key.Key;
import com.wordpress.growworkinghard.riverNe3.geometry.Geometry;

import net.jcip.annotations.ThreadSafe;

/**
 * @brief <strong>Simple Factory</strong> in order to instantiate the proper
 *        type of node
 *
 * @description This class is <em>ThreadSafe</em> because it has been
 *              implemented following the <strong>stack-confinment</strong>
 *              principle
 *
 * @author sidereus, francesco.serafin.3@gmail.com
 * @version 0.1
 * @date November 08, 2015
 * @copyright GNU Public License v3 AboutHydrology (Riccardo Rigon)
 */
@ThreadSafe
public class SimpleNodeFactory {

    /**
     * @brief Factory Method
     *
     * @param[in] root The root node
     * @param[in] leftChild The left child node
     * @param[in] rightChild The right child node
     * @return The appropriate constructed node
     */
    public Component createNewNode(final Geometry root, final Geometry leftChild, final Geometry rightChild) {

        Connections conn;
        final Key ID = root.getKey();
        final int layer = root.getLayer();
        final Coordinate2D startPoint = root.getStartPoint();
        final Coordinate2D endPoint = root.getEndPoint();

        if (isLeaf(leftChild, rightChild)) {
            conn = new BinaryConnections(ID, null, null);
            return new Leaf(conn, layer, newBasin(startPoint, endPoint));
        } else {

            conn = new BinaryConnections(ID);
            return (isGhost(root)) ? new GhostNode(conn, layer, newGhostBasin(startPoint, endPoint)) :
                                     new Node(conn, layer, newBasin(startPoint, endPoint));
        }

    }

    /**
     * @brief Verify if the root node is a ghost one
     *
     * @description A ghost node has starting point equal to the ending point
     *
     * @param[in] root The root node of the sub-tree
     * @retval TRUE The root is ghost
     * @retval FALSE The root is not ghost
     */
    private boolean isGhost(final Geometry root) {

        double xStart = root.getStartPoint().x;
        double yStart = root.getStartPoint().y;
        double xEnd = root.getEndPoint().x;
        double yEnd = root.getEndPoint().y;

        return (xStart == xEnd && yStart == yEnd) ? true : false;

    }

    /**
     * @brief Verify if the root node is a <tt>Leaf</tt> node
     *
     * @description A <tt>Leaf</tt> has both the children <code>null</code>
     *
     * @param[in] leftChild The left child
     * @param[in] rightChild The right child
     * @retval TRUE if both the children are <code>null</code>
     * @retval FALSE otherwise
     */
    private boolean isLeaf(final Geometry leftChild, final Geometry rightChild) {
        return (leftChild == null && rightChild == null) ? true : false;
    }

    /**
     * @param startPoint
     * @param endPoint
     * @return
     */
    private Entity newBasin(final Coordinate2D startPoint, final Coordinate2D endPoint) {

        River river = new River(startPoint, endPoint);
        Ground ground = new Ground(river, 1, 0.006);
        HashMap<Integer, double[]> precipitation = new HashMap<Integer, double[]>();
        HashMap<Integer, double[]> evapotranspiration = new HashMap<Integer, double[]>();
        Integer station1 = 1;
        double[] station1prec = {10, 11};
        double[] station1evap = {0.03, 0.05};
        Integer station2 = 2;
        double[] station2prec = {15, 12};
        double[] station2evap = {0.02, 0.01};
        precipitation.put(station1, station1prec);
        precipitation.put(station2, station2prec);
        evapotranspiration.put(station1, station1evap);
        evapotranspiration.put(station2, station2evap);
        Meteo meteo = new Meteo(precipitation, evapotranspiration);

        return new Basin(ground, meteo, 50.0);

    }

    private Entity newGhostBasin(final Coordinate2D startPoint, final Coordinate2D endPoint) {
        return new GhostBasin(startPoint, endPoint);
    }

}
