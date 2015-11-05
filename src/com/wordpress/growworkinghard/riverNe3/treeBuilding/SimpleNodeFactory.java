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
package com.wordpress.growworkinghard.riverNe3.treeBuilding;

import org.geotools.graph.util.geom.Coordinate2D;

import com.wordpress.growworkinghard.riverNe3.composite.Component;
import com.wordpress.growworkinghard.riverNe3.composite.GhostNode;
import com.wordpress.growworkinghard.riverNe3.composite.Leaf;
import com.wordpress.growworkinghard.riverNe3.composite.Node;
import com.wordpress.growworkinghard.riverNe3.composite.key.BinaryConnections;
import com.wordpress.growworkinghard.riverNe3.composite.key.Connections;
import com.wordpress.growworkinghard.riverNe3.composite.key.Key;
import com.wordpress.growworkinghard.riverNe3.geometry.Geometry;

public class SimpleNodeFactory {

    /**
     * @brief class built on the Stack-confinment principle
     *
     * @param root
     * @param leftChild
     * @param rightChild
     * @return
     */
    public Component createNewNode(final Geometry root, final Geometry leftChild, final Geometry rightChild) {

        Connections conn;
        final Key ID = root.getKey();
        final int layer = root.getLayer();
        final Coordinate2D startPoint = root.getStartPoint();
        final Coordinate2D endPoint = root.getEndPoint();

        if (isLeaf(leftChild, rightChild)) {
            conn = new BinaryConnections(ID, null, null);
            return new Leaf(conn, layer, startPoint, endPoint);
        } else {

            conn = new BinaryConnections(ID);
            return (isGhost(root)) ? new GhostNode(conn, layer, startPoint, endPoint) :
                                     new Node(conn, layer, startPoint, endPoint);
        }

    }

    /**
     * @brief Verify if a root node is a ghost one at the same time
     *
     * @description A ghost node has starting point equal to the ending point
     *
     * @param[in] root
     *            The root node of the sub-tree
     * @return If the root is ghost or not
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

    private boolean isLeaf(final Geometry leftChild, final Geometry rightChild) {

        return (leftChild == null && rightChild == null) ? true : false;

    }

}
