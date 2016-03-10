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
package com.wordpress.growworkinghard.riverNe3.treeBuilding.binaryTree;

import org.geotools.graph.util.geom.Coordinate2D;

import com.wordpress.growworkinghard.riverNe3.composite.Component;
import com.wordpress.growworkinghard.riverNe3.composite.GhostNode;
import com.wordpress.growworkinghard.riverNe3.composite.Leaf;
import com.wordpress.growworkinghard.riverNe3.composite.Node;
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
            return new Leaf(conn, layer, startPoint, endPoint);
        } else {

            conn = new BinaryConnections(ID);
            return (isGhost(root)) ? new GhostNode(conn, layer, startPoint, endPoint) :
                                     new Node(conn, layer, startPoint, endPoint);
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

}
